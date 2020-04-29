package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;

public class LineStart implements Node {
    private final String aContext;

    public LineStart(String context) {
        aContext = context;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public String getContext() {
        return aContext;
    }

    @Override
    public String toString() {
        return "LineStart{" +
                "aContext='" + aContext + '\'' +
                '}';
    }
}
