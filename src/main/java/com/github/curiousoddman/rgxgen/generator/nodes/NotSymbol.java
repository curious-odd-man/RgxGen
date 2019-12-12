package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;

public class NotSymbol implements Node {

    private final Node aNode;

    public NotSymbol(Node node) {
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
        return "NotSymbol{" + aNode + '}';
    }
}
