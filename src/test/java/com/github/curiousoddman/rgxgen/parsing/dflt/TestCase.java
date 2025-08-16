package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.nodes.Node;

public record TestCase(String pattern, Node result, Exception exception) {
    TestCase(String pattern, Node result) {
        this(pattern, result, null);
    }

    TestCase(String pattern, Exception exception) {
        this(pattern, null, exception);
    }

    @Override
    public String toString() {
        return "TestCase{'" + pattern + "'}";
    }
}
