package com.github.curiousoddman.rgxgen.nodes;

import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;

public class AnchorNode extends Node {
    protected AnchorNode(String pattern) {
        super(pattern);
    }

    public boolean isCaret() {
        return getPattern().equals("^");
    }

    public boolean isDollar() {
        return getPattern().equals("$");
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
