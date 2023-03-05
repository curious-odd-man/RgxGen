package com.github.curiousoddman.rgxgen.nodes;

import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;

public class NonCaptureGroup extends Node {

    private final Node aNode;

    public NonCaptureGroup(String pattern, Node node) {
        super(pattern);
        aNode = node;
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
        return "NonCaptureGroup" +
                "{" + aNode +
                '}';
    }
}
