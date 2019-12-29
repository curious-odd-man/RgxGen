package com.github.curiousoddman.rgxgen.parsing.dflt;

/* **************************************************************************
   Copyright 2019 Vladislavs Varslavans

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
/* **************************************************************************/

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Default implementation of parser and NodeTreeBuilder.
 * It reads expression and creates a hierarchy of {@code Node}.
 */
public class DefaultTreeBuilder implements NodeTreeBuilder {

    private enum GroupType {
        POSITIVE_LOOKAHEAD,
        NEGATIVE_LOOKAHEAD,
        POSITIVE_LOOKBEHIND,
        NEGATIVE_LOOKBEHIND,
        CAPTURE_GROUP,
        NON_CAPTURE_GROUP;

        public boolean isNegative() {
            return this == NEGATIVE_LOOKAHEAD || this == NEGATIVE_LOOKBEHIND;
        }
    }

    private final CharIterator aCharIterator;

    private Node aNode;
    private int  aNextGroupIndex = 1;

    /**
     * Default implementation of parser and NodeTreeBuilder.
     * It reads expression and creates a hierarchy of {@code com.github.curiousoddman.rgxgen.generator.nodes.Node}.
     */
    public DefaultTreeBuilder(String expr) {
        aCharIterator = new CharIterator(expr);
    }

    private static void sbToFinal(StringBuilder sb, List<Node> nodes) {
        if (sb.length() != 0) {
            nodes.add(new FinalSymbol(sb.toString()));
            sb.delete(0, sb.length());
        }
    }

    private GroupType processGroupType() {
        switch (aCharIterator.next(2)) {
            case "?=":      // Positive Lookahead does not affect generation.
                return GroupType.POSITIVE_LOOKAHEAD;

            case "?:":      // Non-capture group does not affect generation.
                return GroupType.NON_CAPTURE_GROUP;

            case "?!":
                return GroupType.NEGATIVE_LOOKAHEAD;

            case "?<":
                GroupType res = GroupType.POSITIVE_LOOKBEHIND;
                final char next = aCharIterator.next();
                if (next == '!') {
                    res = GroupType.NEGATIVE_LOOKBEHIND;
                } else if (next != '=') {   // Positive Lookbehind does not affect generation.
                    throw new RuntimeException("Unexpected symbol in pattern: " + aCharIterator.context());
                }
                return res;

            default:
                aCharIterator.move(-2);
                return GroupType.CAPTURE_GROUP;
        }
    }

    public Node parseGroup(GroupType currentGroupType) {
        Integer captureGroupIndex = null;
        if (currentGroupType == GroupType.CAPTURE_GROUP) {
            captureGroupIndex = aNextGroupIndex++;
        }
        ArrayList<Node> choices = new ArrayList<>();
        ArrayList<Node> nodes = new ArrayList<>();
        StringBuilder sb = new StringBuilder(aCharIterator.remaining());
        boolean isChoice = false;

        while (aCharIterator.hasNext()) {
            char c = aCharIterator.next();
            switch (c) {
                case '[':
                    sbToFinal(sb, nodes);
                    nodes.add(handleCharacterVariations());
                    break;

                case '(':
                    sbToFinal(sb, nodes);
                    GroupType groupType = processGroupType();
                    if (groupType.isNegative()) {
                        String subPattern = aCharIterator.nextUntil(')');
                        nodes.add(new NotSymbol(subPattern));
                        aCharIterator.next();       // Past the closing ')'
                    } else {
                        nodes.add(parseGroup(groupType));
                    }
                    break;

                case '|':
                    if (sb.length() == 0 && nodes.isEmpty()) {
                        // Special case when '(|a)' is used - like empty or something
                        choices.add(new FinalSymbol(""));
                    } else {
                        sbToFinal(sb, nodes);
                        choices.add(sequenceOrNot(nodes, choices, false, null));
                        nodes.clear();
                    }
                    isChoice = true;
                    break;

                case ')':
                    sbToFinal(sb, nodes);
                    if (isChoice) {
                        choices.add(sequenceOrNot(nodes, choices, false, null));
                        nodes.clear();
                    }
                    return sequenceOrNot(nodes, choices, isChoice, captureGroupIndex);

                case '{':
                case '*':
                case '?':
                case '+':
                    // We had separate characters before
                    Node repeatNode = null;
                    if (sb.length() == 0) {
                        // Repetition for the last node
                        repeatNode = nodes.remove(nodes.size() - 1);
                    } else {
                        // Repetition for the last character
                        char charToRepeat = sb.charAt(sb.length() - 1);
                        sb.deleteCharAt(sb.length() - 1);
                        sbToFinal(sb, nodes);
                        repeatNode = new FinalSymbol(String.valueOf(charToRepeat));
                    }
                    nodes.add(handleRepeat(c, repeatNode));
                    break;

                case '.':
                    sbToFinal(sb, nodes);
                    nodes.add(new SymbolSet());
                    break;

                case '\\':
                    handleEscapedCharacter(sb, nodes, true);
                    break;

                default:
                    sb.append(c);
                    break;
            }
        }

        sbToFinal(sb, nodes);
        return sequenceOrNot(nodes, choices, isChoice, captureGroupIndex);
    }

    /**
     * Handles next character after escape sequence - \
     * It will either:
     * a) add new node to nodes, if that was any special escape sequence, or
     * b) append character to sb, otherwise
     *
     * @param sb    string builder containing all previous characters before the escape
     * @param nodes previously created nodes; new node will be appended here
     */
    private void handleEscapedCharacter(StringBuilder sb, List<Node> nodes, boolean groupRefAllowed) {
        char c = aCharIterator.next();
        switch (c) {
            case 'd':  // Any decimal digit
            case 'D':  // Any non-decimal digit
                sbToFinal(sb, nodes);
                String[] digits = IntStream.rangeClosed(0, 9)
                                           .mapToObj(Integer::toString)
                                           .toArray(String[]::new);

                nodes.add(new SymbolSet(digits, c == 'd'));
                break;

            case 's':  // Any white space
            case 'S':  // Any non-white space
                sbToFinal(sb, nodes);
                String[] whiteSpaces = {" ", "\t", "\n"};
                nodes.add(new SymbolSet(whiteSpaces, c == 's'));
                break;

            case 'w':  // Any word characters
            case 'W':  // Any non-word characters
                sbToFinal(sb, nodes);
                String[] wordSymbols = {"_"};
                nodes.add(new SymbolSet(Arrays.asList(new SymbolSet.SymbolRange('a', 'z'), new SymbolSet.SymbolRange('A', 'Z'), new SymbolSet.SymbolRange('0', '9')), wordSymbols, c == 'w'));
                break;

            // Hex character:
            //   \xNN or \x{NNNN}
            case 'x':
                c = aCharIterator.peek();
                String hexValue;
                if (c == '{') {
                    aCharIterator.move();
                    hexValue = aCharIterator.nextUntil('}');
                    aCharIterator.move();
                } else {
                    hexValue = aCharIterator.next(2);
                }
                int value = Integer.parseInt(hexValue, 16);
                sb.append((char) value);
                break;

            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                sbToFinal(sb, nodes);
                if (groupRefAllowed) {
                    String digitsSubstring = aCharIterator.takeWhile(Character::isDigit);
                    nodes.add(new GroupRef(Integer.parseInt(c + digitsSubstring)));
                } else {
                    throw new RuntimeException("Group ref is not expected here. " + aCharIterator.context());
                }
                break;

            default:
                sb.append(c);
                break;
        }

    }

    /**
     * Creates appropriate repetition for a node.
     *
     * @param c          character that starts repetitions pattern - *, +, ?, {
     * @param repeatNode node that shall be repeated
     * @return Repeat node
     */
    private Repeat handleRepeat(char c, Node repeatNode) {
        if (c == '*') {
            return Repeat.minimum(repeatNode, 0);
        } else if (c == '?') {
            return new Repeat(repeatNode, 0, 1);
        } else if (c == '+') {
            return Repeat.minimum(repeatNode, 1);
        } else if (c == '{') {
            StringBuilder sb = new StringBuilder();
            int min = -1;
            while (aCharIterator.hasNext()) {
                char tmpc = aCharIterator.next();
                switch (tmpc) {
                    case ',': {
                        min = Integer.parseInt(sb.toString());
                        sb.delete(0, sb.length());
                    }
                    break;

                    case '}':
                        if (min == -1) {
                            return new Repeat(repeatNode, Integer.parseInt(sb.toString()));
                        } else {
                            if (sb.length() == 0) {
                                return Repeat.minimum(repeatNode, min);
                            } else {
                                return new Repeat(repeatNode, min, Integer.parseInt(sb.toString()));
                            }
                        }

                        // TODO: Is it really possible and allowed???
                    case '\\':
                        // Skip backslash and add next symbol to characters
                        tmpc = aCharIterator.next();
                        //noinspection fallthrough
                    default:
                        sb.append(tmpc);
                        break;
                }
            }

            throw new RuntimeException("Unbalanced '{' - missing '}'");
        }

        throw new RuntimeException("Unknown repetition character '" + c + '\'');
    }

    private Node sequenceOrNot(List<Node> nodes, List<Node> choices, boolean isChoice, Integer captureGroupIndex) {
        Node resultNode;

        if (nodes.size() == 1) {
            resultNode = nodes.get(0);
        } else {
            if (isChoice) {
                if (choices.isEmpty()) {
                    throw new RuntimeException("Empty nodes");
                }
                resultNode = new Choice(choices.toArray(new Node[0]));
            } else {
                if (nodes.isEmpty()) {
                    throw new RuntimeException("Empty nodes");
                }
                resultNode = new Sequence(nodes.toArray(new Node[0]));
            }
        }

        if (captureGroupIndex == null) {
            return resultNode;
        } else {
            return new Group(captureGroupIndex, resultNode);
        }
    }

    private static boolean handleRange(boolean rangeStarted, StringBuilder sb, List<SymbolSet.SymbolRange> symbolRanges) {
        if (rangeStarted) {
            char lastChar = sb.charAt(sb.length() - 1);
            char firstChar = sb.charAt(sb.length() - 2);
            sb.delete(sb.length() - 2, sb.length());
            symbolRanges.add(new SymbolSet.SymbolRange(firstChar, lastChar));
        }

        return false;
    }

    /**
     * This function parses expression in square brackets [...]
     * It should be called when aCurrentIndex has index of first character after opening bracket - [
     *
     * @return Node that covers expression in square brackets
     */
    private Node handleCharacterVariations() {
        boolean positive = true;
        if (aCharIterator.peek() == '^') {
            positive = false;
            aCharIterator.next();
        }

        StringBuilder sb = new StringBuilder(aCharIterator.remaining());
        List<SymbolSet.SymbolRange> symbolRanges = new LinkedList<>();
        boolean rangeStarted = false;

        while (aCharIterator.hasNext()) {
            char c = aCharIterator.next();
            switch (c) {
                case ']':
                    handleRange(rangeStarted, sb, symbolRanges);
                    String[] strings;
                    if (sb.length() == 0) {
                        strings = new String[0];
                    } else {
                        strings = Util.stringToCharsSubstrings(sb.toString());
                    }
                    return new SymbolSet(symbolRanges, strings, positive);

                case '-':
                    if (aCharIterator.peek() == ']' || aCharIterator.peek(-2) == '[') {
                        sb.append(c);
                    } else {
                        rangeStarted = true;
                    }
                    break;

                case '\\':
                    // Skip backslash and add next symbol to characters
                    List<Node> nodes = new LinkedList<>();
                    handleEscapedCharacter(sb, nodes, false);
                    if (rangeStarted) {
                        if (!nodes.isEmpty()) {
                            throw new RuntimeException("Cannot make range with a shorthand escape sequences before '" + aCharIterator.context() + '\'');
                        }
                        rangeStarted = handleRange(rangeStarted, sb, symbolRanges);
                    }

                    if (!nodes.isEmpty()) {
                        if (nodes.size() > 1) {
                            throw new RuntimeException("Multiple nodes found inside square brackets escape sequence before '" + aCharIterator.context() + '\'');
                        } else {
                            if (nodes.get(0) instanceof SymbolSet) {
                                for (String symbol : ((SymbolSet) nodes.get(0)).getSymbols()) {
                                    sb.append(symbol);
                                }
                            } else {
                                throw new RuntimeException("Unexpected node found inside square brackets escape sequence before '" + aCharIterator.context() + '\'');
                            }
                        }
                    }
                    break;

                default:
                    sb.append(c);
                    rangeStarted = handleRange(rangeStarted, sb, symbolRanges);
            }
        }
        throw new RuntimeException("Unexpected End Of Expression. Didn't find closing ']'");
    }

    public void build() {
        if (aCharIterator.peek() == '^') {
            aCharIterator.next();
        }

        if (aCharIterator.last() == '$') {
            aCharIterator.setBound(-1);
        }

        aNode = parseGroup(GroupType.NON_CAPTURE_GROUP);
        if (aCharIterator.hasNext()) {
            throw new RuntimeException("Expression was not fully parsed");
        }
    }

    @Override
    public Node get() {
        if (aNode == null) {
            build();
        }
        return aNode;
    }
}
