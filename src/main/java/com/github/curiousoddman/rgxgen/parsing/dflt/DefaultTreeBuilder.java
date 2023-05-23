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

import com.github.curiousoddman.rgxgen.model.GroupType;
import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import com.github.curiousoddman.rgxgen.util.Util;

import java.util.*;

import static com.github.curiousoddman.rgxgen.util.Util.ZERO_LENGTH_CHARACTER_ARRAY;

/**
 * Default implementation of parser and NodeTreeBuilder.
 * It reads expression and creates a hierarchy of {@code Node}.
 */
public class DefaultTreeBuilder implements NodeTreeBuilder {
    private static final Character[]        SINGLETON_UNDERSCORE_ARRAY = {'_'};
    private static final int                HEX_RADIX                  = 16;
    private static final Node[]             EMPTY_NODES_ARR            = new Node[0];
    private final        CharIterator       aCharIterator;
    private final        Map<Node, Integer> aNodesStartPos             = new IdentityHashMap<>();

    private Node aNode;
    private int  aNextGroupIndex = 1;

    /**
     * Default implementation of parser and NodeTreeBuilder.
     * It reads expression and creates a hierarchy of {@code com.github.curiousoddman.rgxgen.generator.nodes.Node}.
     *
     * @param expr expression to parse
     */
    public DefaultTreeBuilder(String expr) {
        aCharIterator = new CharIterator(expr);
    }

    /**
     * Convert all text aggregated in StringBuilder into FinalSymbol node.
     * Does nothing, if sb is empty
     *
     * @param sb    StringBuilder, that is read and emptied
     * @param nodes nodes collection to add created node to.
     */
    private void sbToFinal(StringBuilder sb, Collection<Node> nodes) {
        if (sb.length() != 0) {
            FinalSymbol finalSymbol = new FinalSymbol(sb.toString());
            aNodesStartPos.put(finalSymbol, aCharIterator.prevPos() - finalSymbol.getValue()
                                                                                 .length());
            nodes.add(finalSymbol);
            sb.delete(0, Integer.MAX_VALUE);
        }
    }

    /**
     * Discovers GroupType starting from current position.
     * After execution cursor is on first unprocessed character.
     *
     * @return type of the group (@see GroupType enum)
     */
    private GroupType processGroupType() {
        GroupType groupType;
        int skip = 2;
        if (aCharIterator.peek() == '?') {
            char pos2char = aCharIterator.peek(1);
            switch (pos2char) {
                case '<':
                    skip = 3;
                    char pos3char = aCharIterator.peek(2);
                    if (pos3char == '!') {
                        groupType = GroupType.NEGATIVE_LOOKBEHIND;
                    } else if (pos3char == '=') {
                        groupType = GroupType.POSITIVE_LOOKBEHIND;
                    } else {
                        aCharIterator.skip(skip);
                        throw new RgxGenParseException("Unexpected symbol in pattern: " + aCharIterator.context());
                    }
                    break;
                case '=':
                    groupType = GroupType.POSITIVE_LOOKAHEAD;
                    break;
                case ':':
                    groupType = GroupType.NON_CAPTURE_GROUP;
                    break;
                case '!':
                    groupType = GroupType.NEGATIVE_LOOKAHEAD;
                    break;
                default:
                    aCharIterator.skip(skip);
                    throw new RgxGenParseException("Unexpected symbol in pattern: " + aCharIterator.context());
            }
        } else {
            return GroupType.CAPTURE_GROUP;
        }

        aCharIterator.skip(skip);
        return groupType;
    }

    private Node handleGroupEndCharacter(int startPos, StringBuilder sb, List<Node> nodes, boolean isChoice, List<Node> choices, Integer captureGroupIndex, GroupType groupType) {
        if (sb.length() == 0 && nodes.isEmpty()) {
            // Special case when '(a|)' is used - like empty
            FinalSymbol finalSymbol = new FinalSymbol("");
            aNodesStartPos.put(finalSymbol, startPos);
            nodes.add(finalSymbol);
        } else {
            sbToFinal(sb, nodes);
        }

        if (isChoice) {
            choices.add(sequenceOrNot(startPos, nodes, choices, false, null));
            nodes.clear();
        }

        Node node = sequenceOrNot(startPos, nodes, choices, isChoice, captureGroupIndex);

        if (groupType.isNegative()) {
            return new NotSymbol(node.getPattern(), node);
        } else {
            return node;
        }
    }

    private Integer getGroupIndexIfCapture(GroupType currentGroupType) {
        if (currentGroupType == GroupType.CAPTURE_GROUP) {
            return aNextGroupIndex++;
        }
        return null;
    }

    private static void assertCorrectCharacter(char currentChar) {
        if (currentChar != '^' && currentChar != '$') {
            throw new RgxGenParseException("This method should not be called for character '" + currentChar + "'. Please inform developers.");
        }
    }

    /**
     * There is limited numbed of characters that can precede ^ or follow $.
     * This method verifies that the pattern is syntactically correct.
     *
     * @param currentChar character at current position
     *                    NOTE! Must be either caret or dollar sign
     */
    private void verifyStartEndMarkerConsistency(char currentChar) {
        assertCorrectCharacter(currentChar);
        char charAtPos = aCharIterator.peek(currentChar == '^' ? -2 : 0);
        String errorText;
        switch (charAtPos) {
            // These characters are allowed.
            // Repetition will be handled later only if it follows these characters.
            case '+':
            case '*':
            case '{':
            case '?':
            case 0x00:
            case '\n':
            case '\r':
            case '|':
                return;

            case '^':
            case '$':
                errorText = "Start and end of line markers cannot be put together.";
                break;

            case '(':
                if (currentChar == '$') {
                    errorText = "After dollar only new line is allowed!";
                } else {
                    return;
                }
                break;

            case ')':
                if (currentChar == '^') {
                    errorText = "Before caret only new line is allowed!";
                } else {
                    return;
                }
                break;

            default:
                errorText = currentChar == '^'
                            ? "Before caret only new line is allowed!"
                            : "After dollar only new line is allowed!";
                break;

        }

        throw new PatternDoesNotMatchAnythingException(errorText + aCharIterator.context());
    }

    private Node parseGroup(int groupStartPos, GroupType currentGroupType) {
        Integer captureGroupIndex = getGroupIndexIfCapture(currentGroupType);
        int remainingLength = aCharIterator.remaining();
        List<Node> choices = new ArrayList<>(remainingLength);
        List<Node> nodes = new ArrayList<>(remainingLength);
        StringBuilder sb = new StringBuilder(remainingLength);
        boolean isChoice = false;
        int choicesStartPos = groupStartPos;

        while (aCharIterator.hasNext()) {
            char c = aCharIterator.next();
            switch (c) {
                case '^':
                case '$':
                    verifyStartEndMarkerConsistency(c);
                    break;

                case '[':
                    sbToFinal(sb, nodes);
                    nodes.add(handleSquareBrackets());
                    break;

                case '(':
                    sbToFinal(sb, nodes);
                    int intGroupStartPos = aCharIterator.prevPos();
                    GroupType groupType = processGroupType();
                    nodes.add(parseGroup(intGroupStartPos, groupType));
                    break;

                case '|':
                    choicesStartPos = handlePipeCharacter(choices, nodes, sb, choicesStartPos);
                    isChoice = true;
                    break;

                case ')':
                    return handleGroupEndCharacter(groupStartPos, sb, nodes, isChoice, choices, captureGroupIndex, currentGroupType);

                case '{':
                case '*':
                case '?':
                case '+':
                    handleRepeatCharacter(nodes, sb, c);
                    break;

                case '.':
                    handleAnySymbolCharacter(nodes, sb);
                    break;

                case '\\':
                    handleEscapedCharacter(sb, nodes, true);
                    break;

                default:
                    sb.append(c);
                    break;
            }
        }

        return handleGroupEndCharacter(groupStartPos, sb, nodes, isChoice, choices, captureGroupIndex, currentGroupType);
    }

    private void handleAnySymbolCharacter(Collection<Node> nodes, StringBuilder sb) {
        sbToFinal(sb, nodes);
        SymbolSet symbolSet = new SymbolSet();
        aNodesStartPos.put(symbolSet, aCharIterator.prevPos());
        nodes.add(symbolSet);
    }

    private int handlePipeCharacter(List<Node> choices, List<Node> nodes, StringBuilder sb, int choicesStartPos) {
        if (sb.length() == 0 && nodes.isEmpty()) {
            // Special case when '(|a)' is used - like empty or something
            FinalSymbol finalSymbol = new FinalSymbol("");
            aNodesStartPos.put(finalSymbol, aCharIterator.prevPos() + 1);
            choices.add(finalSymbol);
        } else {
            sbToFinal(sb, nodes);
            choices.add(sequenceOrNot(choicesStartPos, nodes, choices, false, null));
            choicesStartPos = aCharIterator.prevPos() + 1;
            nodes.clear();
        }
        return choicesStartPos;
    }

    private void handleRepeatCharacter(List<Node> nodes, StringBuilder sb, char c) {
        // We had separate characters before
        Node repeatNode;
        if (sb.length() == 0) {
            // Repetition for the last node
            if (nodes.isEmpty()) {
                char previousChar = aCharIterator.peek(-2);
                if (previousChar == '^' || previousChar == '$') {
                    throw new TokenNotQuantifiableException(previousChar + " at " + aCharIterator.context());
                } else {
                    throw new RgxGenParseException("Cannot repeat nothing at" + aCharIterator.context());
                }
            } else {
                repeatNode = nodes.remove(nodes.size() - 1);
            }
        } else {
            // Repetition for the last character
            char charToRepeat = sb.charAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sbToFinal(sb, nodes);
            repeatNode = new FinalSymbol(String.valueOf(charToRepeat));
            aNodesStartPos.put(repeatNode, aCharIterator.prevPos() - 1);
        }
        nodes.add(handleRepeat(c, repeatNode));
    }

    /**
     * Parse hexadecimal string into a integer value.
     * Format: NN or {NNNN}
     *
     * @return integer value
     */
    private int parseHexadecimal() {
        char c = aCharIterator.peek();
        String hexValue;
        if (c == '{') {
            aCharIterator.skip();
            hexValue = aCharIterator.nextUntil('}');
        } else {
            hexValue = aCharIterator.next(2);
        }
        return Integer.parseInt(hexValue, HEX_RADIX);
    }

    /**
     * Parse unicode hexadecimal string into a integer value.
     * Format: NNNN
     *
     * @return integer value
     */
    private int parseUnicode() {
        String hexValue = aCharIterator.next(4);
        return Integer.parseInt(hexValue, HEX_RADIX);
    }

    /**
     * Create group reference node.
     * It starts after escape character AND first digit of group index.
     * aCharIterator after execution is on position right after group index digits.
     *
     * @param groupRefAllowed if at this position group reference is allowed
     * @param nodes           nodes to which add group reference node when created.
     * @param firstCharacter  first digit character, since we're starting after that
     * @throws RgxGenParseException if groupRefAllowed is false
     */
    private void handleGroupReference(boolean groupRefAllowed, Collection<Node> nodes, char firstCharacter) {
        if (groupRefAllowed) {
            int startPos = aCharIterator.prevPos() - 1;
            String digitsSubstring = aCharIterator.takeWhile(Character::isDigit);
            String groupNumber = firstCharacter + digitsSubstring;
            GroupRef groupRef = new GroupRef('\\' + groupNumber, Integer.parseInt(groupNumber));
            aNodesStartPos.put(groupRef, startPos);
            nodes.add(groupRef);
        } else {
            throw new RgxGenParseException("Group ref is not expected here. " + aCharIterator.context());
        }
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
    private void handleEscapedCharacter(StringBuilder sb, Collection<Node> nodes, boolean groupRefAllowed) {
        char c = aCharIterator.next();
        Node createdNode = null;
        switch (c) {
            case 'd':  // Any decimal digit
            case 'D':  // Any non-decimal digit
                sbToFinal(sb, nodes);
                createdNode = new SymbolSet("\\" + c, ConstantsProvider.getDigits(), getMatchType(c, 'd'));
                break;

            case 's':  // Any white space
            case 'S':  // Any non-white space
                sbToFinal(sb, nodes);
                createdNode = new SymbolSet("\\" + c, ConstantsProvider.getWhitespaces(), getMatchType(c, 's'));
                break;

            case 'w':  // Any word characters
            case 'W':  // Any non-word characters
                sbToFinal(sb, nodes);
                createdNode = new SymbolSet("\\" + c, ConstantsProvider.getWordCharRanges(), SINGLETON_UNDERSCORE_ARRAY, getMatchType(c, 'w'));
                break;

            case 'p':   // Character classes
            case 'P':   // Not-matching character classes
                sbToFinal(sb, nodes);
                createdNode = createUnicodeSymbolSetNode(sb, c, getMatchType(c, 'p'));
                break;

            // Hex character:
            //   \xNN or \x{NNNN}
            case 'x':
                sb.append((char) parseHexadecimal());
                break;

            case 'u':
                sb.append((char) parseUnicode());
                break;

            case 'Q':
                sb.append(aCharIterator.nextUntil("\\E"));
                break;

            case 'E':       // End of escape sequence can be ignored.
                break;

            case 'b':      // These both cannot only be used at start/end of the pattern.
            case 'B':      // Later I could add validation that these are not used in the middle of pattern.
                break;

            // Group reference number
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
                handleGroupReference(groupRefAllowed, nodes, c);
                break;

            default:
                sb.append(c);
                break;
        }

        if (createdNode != null) {
            aNodesStartPos.put(createdNode, aCharIterator.prevPos() - 1);
            nodes.add(createdNode);
        }
    }

    private Node createUnicodeSymbolSetNode(StringBuilder sb, char c, MatchType matchType) {
        String characterClassKey = getCharacterClassKey();
        UnicodeCategory unicodeCategory = UnicodeCategory.ALL_CATEGORIES.get(characterClassKey);
        String pattern = "\\" + c + '{' + characterClassKey + '}';
        return new SymbolSet(pattern, unicodeCategory, getMatchType(c, 'w'));
    }

    private String getCharacterClassKey() {
        if (aCharIterator.peek() == '{') {
            aCharIterator.skip();
            return aCharIterator.nextUntil('}');
        } else {
            return aCharIterator.next(1);
        }
    }

    private static MatchType getMatchType(char parsedCharacter, char positiveMatchCharacter) {
        return parsedCharacter == positiveMatchCharacter ? MatchType.POSITIVE : MatchType.NEGATIVE;
    }

    /**
     * Parses min and max repetitions from the {min,max} or {max} expressions (starting after {
     *
     * @param repeatNode node that should be repeated
     * @return Repeat node
     */
    private Repeat handleRepeatInCurvyBraces(int startPos, Node repeatNode) {
        StringBuilder sb = new StringBuilder(10);
        int min = -1;
        int contextIndex = aCharIterator.prevPos();
        while (aCharIterator.hasNext()) {
            char c = aCharIterator.next();
            switch (c) {
                case ',': {
                    int tmpContextIndex = aCharIterator.prevPos() - 1;
                    try {
                        min = Integer.parseInt(sb.toString());
                    } catch (NumberFormatException e) {
                        throw new RgxGenParseException("Malformed lower bound number." + aCharIterator.context(tmpContextIndex), e);
                    }
                    sb.delete(0, sb.length());
                }
                break;

                case '}':
                    if (min == -1) {
                        return new Repeat(aCharIterator.substringToCurrPos(startPos), repeatNode, Integer.parseInt(sb.toString()));
                    } else {
                        if (sb.length() == 0) {
                            return Repeat.minimum(aCharIterator.substringToCurrPos(startPos), repeatNode, min);
                        } else {
                            try {
                                return new Repeat(aCharIterator.substringToCurrPos(startPos), repeatNode, min, Integer.parseInt(sb.toString()));
                            } catch (NumberFormatException e) {
                                throw new RgxGenParseException("Malformed upper bound number." + aCharIterator.context(), e);
                            }
                        }
                    }

                case '\\':
                    throw new RgxGenParseException("Escape character inside curvy repetition is not supported. " + aCharIterator.context());

                default:
                    sb.append(c);
                    break;
            }
        }

        throw new RgxGenParseException("Unbalanced '{' - missing '}' at " + aCharIterator.context(contextIndex));
    }

    /**
     * Creates appropriate repetition for a node.
     *
     * @param c          character that starts repetitions pattern - *, +, ?, {
     * @param repeatNode node that shall be repeated
     * @return Repeat node
     */
    private Repeat handleRepeat(char c, Node repeatNode) {
        int startPos = aNodesStartPos.get(repeatNode);
        Repeat node;
        switch (c) {
            case '*':
                node = Repeat.minimum(aCharIterator.substringToCurrPos(startPos), repeatNode, 0);
                break;
            case '?':
                node = new Repeat(aCharIterator.substringToCurrPos(startPos), repeatNode, 0, 1);
                break;
            case '+':
                node = Repeat.minimum(aCharIterator.substringToCurrPos(startPos), repeatNode, 1);
                break;
            case '{':
                node = handleRepeatInCurvyBraces(startPos, repeatNode);
                break;

            default:
                throw new RgxGenParseException("Unknown repetition character '" + c + '\'' + aCharIterator.context());
        }

        aNodesStartPos.put(node, startPos);
        return node;
    }

    /**
     * Wraps multiple nodes into correct container node (Choice, Sequence) or node as is and wraps it into Group node, if this is a capture group
     *
     * @param nodes             list of nodes (sequence or single node)
     * @param choices           list of nodes to select one from
     * @param isChoice          true when {@code} choices should be used
     * @param captureGroupIndex index of capture group
     * @return Group, Node
     */
    private Node sequenceOrNot(int startPos, List<Node> nodes, List<Node> choices, boolean isChoice, Integer captureGroupIndex) {
        Node resultNode;

        if (nodes.size() == 1) {
            resultNode = nodes.get(0);
        } else {
            if (isChoice) {
                if (choices.isEmpty()) {
                    throw new RgxGenParseException("Empty nodes");
                }
                resultNode = new Choice(aCharIterator.substringToCurrPos(startPos), choices.toArray(EMPTY_NODES_ARR));
            } else {
                if (nodes.isEmpty()) {
                    throw new RgxGenParseException("Empty nodes");
                }
                resultNode = new Sequence(aCharIterator.substringToCurrPos(startPos), nodes.toArray(EMPTY_NODES_ARR));
            }
        }

        aNodesStartPos.put(resultNode, startPos);
        if (captureGroupIndex == null) {
            return resultNode;
        } else {
            Group group = new Group(aCharIterator.substringToCurrPos(startPos), captureGroupIndex, resultNode);
            aNodesStartPos.put(group, startPos);
            return group;
        }
    }

    /**
     * It should be called when aCharIterator is on first character after opening bracket - [
     *
     * @return Node representing expression in square brackets
     */
    private Node handleSquareBrackets() {
        int openSquareBraceIndex = aCharIterator.prevPos();
        MatchType symbolSetType = determineSymbolSetType(aCharIterator);
        StringBuilder characters = new StringBuilder(aCharIterator.remaining());
        List<SymbolRange> symbolRanges = new ArrayList<>();
        List<SymbolSet> symbolSets = new ArrayList<>();
        boolean rangeStarted = false;

        while (aCharIterator.hasNext()) {
            char c = aCharIterator.next();
            switch (c) {
                case ']':
                    String pattern = aCharIterator.substringToCurrPos(openSquareBraceIndex);
                    SymbolSet finalSymbolSet = createSymbolSetFromSquareBrackets(pattern, symbolSetType, characters, symbolRanges, symbolSets);
                    aNodesStartPos.put(finalSymbolSet, openSquareBraceIndex);
                    return finalSymbolSet;

                case '-':
                    if (aCharIterator.peek() == ']' || aCharIterator.peek(-2) == '[') {
                        characters.append('-');
                    } else {
                        rangeStarted = true;
                    }
                    break;

                case '\\':
                    Optional<SymbolSet> symbolSet = handleBackslashInsideSquareBrackets(characters);

                    if (rangeStarted) {
                        if (symbolSet.isPresent()) {
                            throw new RgxGenParseException("Cannot make range with a shorthand escape sequences before '" + aCharIterator.context() + '\'');
                        }
                        handleSymbolRange(characters, symbolRanges);
                    } else {
                        symbolSet.ifPresent(symbolSets::add);
                    }
                    rangeStarted = false;
                    break;

                default:
                    characters.append(c);
                    if (rangeStarted) {
                        handleSymbolRange(characters, symbolRanges);
                        rangeStarted = false;
                    }
                    break;
            }
        }

        throw new RgxGenParseException("Unexpected End Of Expression. Didn't find closing ']'" + aCharIterator.context(openSquareBraceIndex));
    }

    private static MatchType determineSymbolSetType(CharIterator charIterator) {
        if (charIterator.peek() == '^') {
            charIterator.skip();
            return MatchType.NEGATIVE;
        } else {
            return MatchType.POSITIVE;
        }
    }

    private Optional<SymbolSet> handleBackslashInsideSquareBrackets(StringBuilder characters) {
        // Skip backslash and add next symbol to characters
        List<Node> nodes = new ArrayList<>(5);

        StringBuilder sb = new StringBuilder(0);
        handleEscapedCharacter(sb, nodes, false);
        characters.append(sb);

        if (nodes.isEmpty()) {
            return Optional.empty();
        }

        if (nodes.size() > 1) {
            throw new RgxGenParseException("Multiple nodes found inside square brackets escape sequence before '" + aCharIterator.context() + '\'');
        } else {
            return Optional.of((SymbolSet) nodes.get(0));
        }
    }

    private static void handleSymbolRange(StringBuilder characters, Collection<SymbolRange> symbolRanges) {
        // If we're here, then previous character was '-'.
        // But dash can be used in such way: [a-c-]. In this case last dash is only a character, not a range start.
        if (characters.length() < 2) {
            characters.append('-');
        } else {
            char lastChar = characters.charAt(characters.length() - 1);
            char firstChar = characters.charAt(characters.length() - 2);
            characters.delete(characters.length() - 2, characters.length());
            symbolRanges.add(SymbolRange.of(firstChar, lastChar));
        }
    }

    private static SymbolSet createSymbolSetFromSquareBrackets(String pattern, MatchType matchType, CharSequence sb, List<SymbolRange> symbolRanges, Iterable<SymbolSet> symbolSets) {
        List<Character> characters = new ArrayList<>();
        if (sb.length() > 0) {
            characters.addAll(Arrays.asList(Util.stringToChars(sb)));
        }

        for (SymbolSet symbolSet : symbolSets) {
            characters.addAll(Arrays.asList(symbolSet.getSymbols()));
        }

        return new SymbolSet(pattern, symbolRanges, characters.toArray(ZERO_LENGTH_CHARACTER_ARRAY), matchType);
    }

    public void build() {
        aNode = parseGroup(aCharIterator.prevPos() + 1, GroupType.NON_CAPTURE_GROUP);
        if (aCharIterator.hasNext()) {
            throw new RgxGenParseException("Expression was not fully parsed: " + aCharIterator.context());
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
