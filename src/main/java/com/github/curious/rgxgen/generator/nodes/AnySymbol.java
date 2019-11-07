package com.github.curious.rgxgen.generator.nodes;

import com.github.curious.rgxgen.generator.visitors.NodeVisitor;

/**
 * Generate Any printable character.
 */
public class AnySymbol implements Node {
    public static final String[] ALL_SYMBOLS = new String[127 - 32];

    static {
        for (char c = 32; c < 127; ++c) {
            ALL_SYMBOLS[c - 32] = Character.valueOf(c)
                                           .toString();
        }
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "AnySymbol{}";
    }
}
