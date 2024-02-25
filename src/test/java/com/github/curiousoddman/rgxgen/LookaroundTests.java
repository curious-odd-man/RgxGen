package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * These tests are the tests which require find() instead of matches() to verify the generated patter.
 * Apparently lookahead and lookbehind does not match
 */
public class LookaroundTests {
    public static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of("Positive lookahead", "foo(?=b)"),
                Arguments.of("Negative lookahead", "foo(?!bab)"),
                Arguments.of("Positive lookbehind", "(?<=foo)bar"),
                Arguments.of("Negative lookbehind", "(?<!not)fof")
        );
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void generateTest(String name, String pattern) {
        RgxGen rgxGen = RgxGen.parse(pattern);
        for (int i = 0; i < 100; i++) {
            String s = rgxGen.generate();
            assertTrue(Pattern.compile(pattern)
                              .matcher(s)
                              .find(), "Text: '" + s + "'does not match pattern " + pattern);
        }
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void generateInfiniteTest(String name, String pattern) {
        RgxGen rgxGen = RgxGen.parse(pattern);
        StringIterator stringIterator = rgxGen.iterateUnique();
        for (int i = 0; i < 100 && stringIterator.hasNext(); i++) {
            String s = stringIterator.next();
            assertTrue(Pattern.compile(pattern)
                              .matcher(s)
                              .find(), "Text: '" + s + "'does not match pattern " + pattern);
        }
    }
}
