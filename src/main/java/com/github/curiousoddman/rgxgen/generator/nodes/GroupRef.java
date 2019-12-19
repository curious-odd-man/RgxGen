package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;

public class GroupRef implements Node {
    private final int aIndex;

    public GroupRef(int index) {
        aIndex = index;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public int getIndex() {
        return aIndex;
    }

    @Override
    public String toString() {
        return "GroupRef{" + aIndex + '}';
    }
}
