package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.data.Named;

public record TestCase(String name, String pattern, Exception exception) implements Named {
    TestCase(String name, String pattern) {
        this(name, pattern, null);
    }

    @Override
    public String toString() {
        return "TestCase{'" + pattern + "'}";
    }
}
