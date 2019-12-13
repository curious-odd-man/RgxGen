package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;

import java.util.regex.Pattern;

public class NotSymbol implements Node {

    private final Pattern aSubPattern;

    public NotSymbol(String pattern) {
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
