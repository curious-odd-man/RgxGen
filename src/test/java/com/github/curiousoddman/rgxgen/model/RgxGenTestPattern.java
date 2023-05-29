package com.github.curiousoddman.rgxgen.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public class RgxGenTestPattern {
    private static final Pattern COMPILE = Pattern.compile("\\{\\d+,\\d+}");

    private final String            pattern;
    private final Optional<Pattern> compiled;
    private final UnicodeCategory   unicodeCategory;
    private final boolean           expectToMatch;

    public String getPatternWithoutLength() {
        return COMPILE.split(pattern)[0];
    }
}
