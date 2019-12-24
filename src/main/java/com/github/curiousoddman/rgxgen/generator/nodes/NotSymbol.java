package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class NotSymbol implements Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotSymbol.class);

    private final Pattern aSubPattern;

    public NotSymbol(String pattern) {
        LOGGER.trace("Crating '{}'", pattern);
        aSubPattern = Pattern.compile(pattern);
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Pattern getSubPattern() {
        return aSubPattern;
    }

    @Override
    public String toString() {
        return "NotSymbol{" + aSubPattern.pattern() + '}';
    }
}
