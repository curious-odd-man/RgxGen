package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.data.FileTestUtils;

import java.nio.file.Path;

public record TestCase(String name, String pattern, Exception exception) implements FileTestUtils {
    TestCase(String name, String pattern) {
        this(name, pattern, null);
    }

    @Override
    public String toString() {
        return "TestCase{'" + pattern + "'}";
    }

    @Override
    public Path rootPath() {
        return Path.of("testdata/patterns");
    }
}
