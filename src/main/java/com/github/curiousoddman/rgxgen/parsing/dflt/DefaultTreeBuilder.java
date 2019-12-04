package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class DefaultTreeBuilder implements NodeTreeBuilder {
    private final String aExpr;

    private int  aCurrentIndex = 0;
    private Node aNode;

    public DefaultTreeBuilder(String expr) {
        aExpr = expr;
    }

    private static void sbToFinal(StringBuilder sb, List<Node> nodes) {
        if (sb.length() != 0) {
            nodes.add(new FinalSymbol(sb.toString()));
            sb.delete(0, sb.length());
        }
    }

    public Node parseGroup() {
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
                    nodes.add(parseGroup());
                    break;

                case '|':
                    sbToFinal(sb, nodes);
                    choices.add(sequenceOrNot(nodes, choices, false));
                    nodes.clear();
                    isChoice = true;
                    break;

                case ')':
                    sbToFinal(sb, nodes);
                    if (isChoice) {
                        choices.add(sequenceOrNot(nodes, choices, false));
                        nodes.clear();
                    }
                    return sequenceOrNot(nodes, choices, isChoice);

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
                    handleEscapedCharacter(sb, nodes);
                    break;

                default:
                    sb.append(c);
                    break;
            }
        }

        sbToFinal(sb, nodes);
        return sequenceOrNot(nodes, choices, isChoice);
    }

    /**
     * Handles next character after escape sequence - \
     * It will either:
     * a) add new node to nodes, if that was any special esapce sequence, or
     * b) append character to sb, otherwise
     *
     * @param sb    string builder containing all previous characters before the escape
     * @param nodes previously created nodes; new node will be appended here
     */
    private void handleEscapedCharacter(StringBuilder sb, List<Node> nodes) {
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

            default:
                sb.append(c);
                break;
        }

    }

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

    private static Node sequenceOrNot(List<Node> nodes, List<Node> choices, boolean isChoice) {
        if (nodes.size() == 1) {
            return nodes.get(0);
        } else {
            if (isChoice) {
                if (choices.isEmpty()) {
                    throw new RuntimeException("Empty nodes");
                }
                return new Choice(choices.toArray(new Node[0]));
            } else {
                if (nodes.isEmpty()) {
                    throw new RuntimeException("Empty nodes");
                }
                return new Sequence(nodes.toArray(new Node[0]));
            }
        }
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

        while (aExpr.length() > aCurrentIndex) {
            char c = aExpr.charAt(aCurrentIndex++);
            switch (c) {
                case ']':
                    String[] strings;
                    if (sb.length() == 0) {
                        strings = new String[0];
                    } else {
                        strings = Util.stringToCharsSubstrings(sb.toString());
                    }
                    return new SymbolSet(symbolRanges, strings, positive);

                case '-':
                    c = aExpr.charAt(aCurrentIndex++);
                    char currentChar = sb.charAt(sb.length() - 1);
                    sb.deleteCharAt(sb.length() - 1);
                    symbolRanges.add(new SymbolSet.SymbolRange(currentChar, c));
                    break;

                case '\\':
                    // Skip backslash and add next symbol to characters
                    c = aExpr.charAt(aCurrentIndex++);
                    //noinspection fallthrough
                default:
                    sb.append(c);
            }
        }
        throw new RuntimeException("Unexpected End Of Expression. Didn't find closing ']'");
    }

    public void build() {
        aNode = parseGroup();
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
