package com.github.curious.rgxgen.generator.nodes;

import com.github.curious.rgxgen.generator.visitors.NodeVisitor;

public class FinalSymbol implements Node {

    private final String aValue;

    public FinalSymbol(String value) {
        aValue = value;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public String getValue() {
        return aValue;
    }

    @Override
    public String toString() {
        return "FinalSymbol{" +
                '\'' + aValue + '\'' +
                '}';
    }
}
