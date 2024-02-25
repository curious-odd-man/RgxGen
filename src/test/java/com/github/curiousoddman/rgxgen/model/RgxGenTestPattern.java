package com.github.curiousoddman.rgxgen.model;


import java.util.regex.Pattern;

public class RgxGenTestPattern {
    private static final Pattern COMPILE = Pattern.compile("\\{\\d+}");

    private final String          pattern;
    private final Pattern         compiled;
    private final UnicodeCategory unicodeCategory;
    private final boolean         expectToMatch;

    public RgxGenTestPattern(String pattern, Pattern compiled, UnicodeCategory unicodeCategory, boolean expectToMatch) {
        this.pattern = pattern;
        this.compiled = compiled;
        this.unicodeCategory = unicodeCategory;
        this.expectToMatch = expectToMatch;
    }

    public String getPattern() {
        return pattern;
    }

    public Pattern getCompiled() {
        return compiled;
    }

    public UnicodeCategory getUnicodeCategory() {
        return unicodeCategory;
    }

    public boolean isExpectToMatch() {
        return expectToMatch;
    }

    public String getPatternWithoutLength() {
        return COMPILE.split(pattern)[0];
    }
}
