package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinalSymbol implements Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinalSymbol.class);

    private final String aValue;

    public FinalSymbol(String value) {
        LOGGER.trace("Creating '{}'", value);
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
