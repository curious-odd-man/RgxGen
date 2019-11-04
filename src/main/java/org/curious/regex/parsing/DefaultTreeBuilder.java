package org.curious.regex.parsing;

import org.curious.regex.generator.nodes.*;
import org.curious.regex.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultTreeBuilder implements NodeTreeBuilder {

    private final String aExpr;

    private int  aCurrentIndex = 0;
    private Node aNode;

    public DefaultTreeBuilder(String expr) {
        aExpr = expr;
    }

    public Node parseGroup() {
        ArrayList<Node> nodes = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean isChoice = false;

        while (aExpr.length() > aCurrentIndex) {
            char c = aExpr.charAt(aCurrentIndex++);
            switch (c) {
                case '[':
                    if (sb.length() != 0) {
                        nodes.add(new FinalSymbol(sb.toString()));
                        sb.delete(0, sb.length());
                    }
                    nodes.add(handleCharacterVariations());
                    break;

                case '(':
                    if (sb.length() != 0) {
                        nodes.add(new FinalSymbol(sb.toString()));
                        sb.delete(0, sb.length());
                    }
                    nodes.add(parseGroup());
                    break;

                case '|':
                    if (sb.length() != 0) {
                        nodes.add(new FinalSymbol(sb.toString()));
                        sb.delete(0, sb.length());
                    }
                    isChoice = true;
                    break;

                case ')':
                    if (sb.length() != 0) {
                        nodes.add(new FinalSymbol(sb.toString()));
                        sb.delete(0, sb.length());
                    }
                    return sequenceOrNot(nodes, isChoice);

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
                        if (sb.length() > 0) {
                            nodes.add(new FinalSymbol(sb.toString()));
                            sb.delete(0, sb.length());
                        }
                        repeatNode = new FinalSymbol(String.valueOf(charToRepeat));
                    }
                    nodes.add(handleRepeat(c, repeatNode));
                    break;

                case '.':
                    if (sb.length() != 0) {
                        nodes.add(new FinalSymbol(sb.toString()));
                        sb.delete(0, sb.length());
                    }
                    nodes.add(handleDot());
                    break;

                case '\\':
                    // Skip backslash and add next symbol to characters
                    c = aExpr.charAt(aCurrentIndex++);
                    //noinspection fallthrough
                default:
                    sb.append(c);
                    break;
            }
        }

        if (sb.length() != 0) {
            nodes.add(new FinalSymbol(sb.toString()));
        }
        return sequenceOrNot(nodes, isChoice);
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
            long min = -1;
            while (aExpr.length() > aCurrentIndex) {
                char tmpc = aExpr.charAt(aCurrentIndex++);
                switch (tmpc) {
                    case ',': {
                        min = Long.parseLong(sb.toString());
                        sb.delete(0, sb.length());
                    }
                    break;

                    case '}':
                        if (min == -1) {
                            return new Repeat(repeatNode, Long.parseLong(sb.toString()));
                        } else {
                            return new Repeat(repeatNode, min, Long.parseLong(sb.toString()));
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

            throw new RuntimeException("Unbalanced { - no }");
        }

        throw new RuntimeException("Unknown repetition character '" + c + '\'');
    }

    private static Node sequenceOrNot(List<Node> nodes, boolean isChoice) {
        if (nodes.size() == 1) {
            return nodes.get(0);
        } else {
            if (nodes.isEmpty()) {
                throw new RuntimeException("Empty nodes");
            }
            if (isChoice) {
                return new Choice(nodes.toArray(new Node[0]));
            } else {
                return new Sequence(nodes.toArray(new Node[0]));
            }
        }
    }

    private static Node handleDot() {
        return new AnySymbol();
    }

    private Node handleCharacterVariations() {
        StringBuilder sb = new StringBuilder();
        while (aExpr.length() > aCurrentIndex) {
            char c = aExpr.charAt(aCurrentIndex++);
            switch (c) {
                case ']':
                    Node[] nodes = Arrays.stream(Util.stringToCharsSubstrings(sb.toString()))
                                         .map(FinalSymbol::new)
                                         .toArray(Node[]::new);
                    return new Choice(nodes);

                case '-':
                    c = aExpr.charAt(aCurrentIndex++);
                    char currentChar = sb.charAt(sb.length() - 1);
                    sb.deleteCharAt(sb.length() - 1);
                    for (; currentChar <= c; ++currentChar) {
                        sb.append(currentChar);
                    }
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
