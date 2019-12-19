package com.github.curiousoddman.rgxgen.parsing.dflt;

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

        public boolean isPositive() {
            return this == POSITIVE_LOOKAHEAD || this == POSITIVE_LOOKBEHIND;
        }

        public boolean isNegative() {
            return this == NEGATIVE_LOOKAHEAD || this == NEGATIVE_LOOKBEHIND;
        }
    }

    private final String aExpr;

    private int  aCurrentIndex   = 0;
    private Node aNode;
    private int  aNextGroupIndex = 1;

    /**
     * Default implementation of parser and NodeTreeBuilder.
     * It reads expression and creates a hierarchy of {@code com.github.curiousoddman.rgxgen.generator.nodes.Node}.
     */
    public DefaultTreeBuilder(String expr) {
        aExpr = expr;
    }

    private static void sbToFinal(StringBuilder sb, List<Node> nodes) {
        if (sb.length() != 0) {
            nodes.add(new FinalSymbol(sb.toString()));
            sb.delete(0, sb.length());
        }
    }

    private GroupType processGroupType() {
        switch (aExpr.substring(aCurrentIndex, aCurrentIndex + 2)) {
            case "?=":      // Positive Lookahead does not affect generation.
                aCurrentIndex += 2;
                return GroupType.POSITIVE_LOOKAHEAD;

            case "?:":      // Non-capture group does not affect generation.
                aCurrentIndex += 2;
                return GroupType.NON_CAPTURE_GROUP;

            case "?!":
                aCurrentIndex += 2;
                return GroupType.NEGATIVE_LOOKAHEAD;

            case "?<":
                GroupType res = GroupType.POSITIVE_LOOKBEHIND;
                if (aExpr.charAt(aCurrentIndex + 2) == '!') {
                    res = GroupType.NEGATIVE_LOOKBEHIND;
                } else if (aExpr.charAt(aCurrentIndex + 2) != '=') {   // Positive Lookbehind does not affect generation.
                    throw new RuntimeException("Unexpected symbol in pattern: " + aExpr.charAt(aCurrentIndex + 2) + " at " + aCurrentIndex);
                }
                aCurrentIndex += 3;
                return res;

            default:
                return GroupType.CAPTURE_GROUP;
        }
    }

    public Node parseGroup(GroupType currentGroupType) {
        ArrayList<Node> choices = new ArrayList<>();
        ArrayList<Node> nodes = new ArrayList<>();
        StringBuilder sb = new StringBuilder(aExpr.length());
        boolean isChoice = false;

        while (aExpr.length() > aCurrentIndex) {
            char c = aExpr.charAt(aCurrentIndex++);
            switch (c) {
                case '[':
                    sbToFinal(sb, nodes);
                    nodes.add(handleCharacterVariations());
                    break;

                case '(':
                    sbToFinal(sb, nodes);
                    GroupType groupType = processGroupType();
                    if (groupType.isNegative()) {
                        String subPattern = Util.substringUntil(aExpr, aCurrentIndex, ')');
                        nodes.add(new NotSymbol(subPattern));
                        aCurrentIndex += subPattern.length() + 1;       // Past the closing ')'
                    } else {
                        nodes.add(parseGroup(groupType));
                    }
                    break;

                case '|':
                    sbToFinal(sb, nodes);
                    choices.add(sequenceOrNot(nodes, choices, false, GroupType.NON_CAPTURE_GROUP));
                    nodes.clear();
                    isChoice = true;
                    break;

                case ')':
                    sbToFinal(sb, nodes);
                    if (isChoice) {
                        choices.add(sequenceOrNot(nodes, choices, false, GroupType.NON_CAPTURE_GROUP));
                        nodes.clear();
                    }
                    return sequenceOrNot(nodes, choices, isChoice, currentGroupType);

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
        return sequenceOrNot(nodes, choices, isChoice, currentGroupType);
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
        char c = aExpr.charAt(aCurrentIndex++);
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
                c = aExpr.charAt(aCurrentIndex++);
                int startIdx;
                int endIndex;
                if (c == '{') {
                    startIdx = aCurrentIndex;
                    while (aExpr.charAt(aCurrentIndex++) != '}') {
                        if (aCurrentIndex >= aExpr.length()) {
                            throw new RuntimeException("While parsing hex value at position " + startIdx + " at " + aExpr.substring(startIdx, startIdx + 5) + " did not find closing '}'");
                        }
                    }
                    endIndex = aCurrentIndex - 1;
                } else {
                    startIdx = aCurrentIndex - 1;
                    endIndex = startIdx + 2;
                    aCurrentIndex = endIndex;
                }

                String hexValue = aExpr.substring(startIdx, endIndex);
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
                    String digitsSubstring = Util.takeWhile(aExpr, aCurrentIndex - 1, Character::isDigit);
                    aCurrentIndex = aCurrentIndex - 1 + digitsSubstring.length();
                    nodes.add(new GroupRef(Integer.parseInt(digitsSubstring)));
                } else {
                    throw new RuntimeException("Group ref is not expected here. " + aExpr.substring(aCurrentIndex));
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
            while (aExpr.length() > aCurrentIndex) {
                char tmpc = aExpr.charAt(aCurrentIndex++);
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
                        tmpc = aExpr.charAt(aCurrentIndex++);
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

    private Node sequenceOrNot(List<Node> nodes, List<Node> choices, boolean isChoice, GroupType groupType) {
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

        if (groupType == GroupType.CAPTURE_GROUP) {
            return new Group(aNextGroupIndex++, resultNode);
        } else {
            return resultNode;
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
        if (aExpr.charAt(aCurrentIndex) == '^') {
            positive = false;
            ++aCurrentIndex;
        }

        StringBuilder sb = new StringBuilder();
        List<SymbolSet.SymbolRange> symbolRanges = new LinkedList<>();
        boolean rangeStarted = false;

        while (aExpr.length() > aCurrentIndex) {
            char c = aExpr.charAt(aCurrentIndex++);
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
                    if (aExpr.charAt(aCurrentIndex) == ']' || aExpr.charAt(aCurrentIndex - 2) == '[') {
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
                            throw new RuntimeException("Cannot make range with a shorthand escape sequences before '" + aExpr.substring(aCurrentIndex) + '\'');
                        }
                        rangeStarted = handleRange(rangeStarted, sb, symbolRanges);
                    }

                    if (!nodes.isEmpty()) {
                        if (nodes.size() > 1) {
                            throw new RuntimeException("Multiple nodes found inside square brackets escape sequence before '" + aExpr.substring(aCurrentIndex) + '\'');
                        } else {
                            if (nodes.get(0) instanceof SymbolSet) {
                                for (String symbol : ((SymbolSet) nodes.get(0)).getSymbols()) {
                                    sb.append(symbol);
                                }
                            } else {
                                throw new RuntimeException("Unexpected node found inside square brackets escape sequence before '" + aExpr.substring(aCurrentIndex) + '\'');
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
        aNode = parseGroup(GroupType.NON_CAPTURE_GROUP);
        if (aCurrentIndex < aExpr.length()) {
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
