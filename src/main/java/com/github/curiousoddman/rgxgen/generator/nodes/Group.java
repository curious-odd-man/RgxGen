package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;

public class Group implements Node {
    private final Node    aNode;
    private final int     aGroupIndex;
    private final boolean aIsCapturable;

    public Group(Node node) {
        aNode = node;
        aGroupIndex = -1;
        aIsCapturable = false;
    }

    public Group(int index, Node node) {
        aNode = node;
        aGroupIndex = index;
        aIsCapturable = true;
    }

    public boolean isCapture() {
        return aIsCapturable;
    }

    public int getIndex() {
        return aGroupIndex;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Node getNode() {
        return aNode;
    }

    @Override
    public String toString() {
        return "Group[" + aGroupIndex +
                "]{" + aNode +
                '}';
    }
}
