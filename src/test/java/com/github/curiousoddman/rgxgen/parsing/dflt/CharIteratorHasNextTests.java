package com.github.curiousoddman.rgxgen.parsing.dflt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CharIteratorHasNextTests {
    private static final String TEST_STRING = "0123456789ABCDEF";
    private CharIterator aCharIterator;

    public static Stream<Arguments> data() {
        return Stream.of(
                // Valid cases
                arguments(0, "0", true),
                arguments(0, "01", true),
                arguments(0, "012", true),

                arguments(3, "3", true),
                arguments(3, "34", true),
                arguments(3, "345", true),

                // Not matching
                arguments(0, "1", false),
                arguments(0, "02", false),
                arguments(0, "013", false),

                arguments(3, "4", false),
                arguments(3, "35", false),
                arguments(3, "347", false),

                // Not matching - not enough length
                arguments(16, "F", false),
                arguments(15, "F3", false),
                arguments(15, "EFX", false)
        );
    }

    @BeforeEach
    public void setUp() {
        aCharIterator = new CharIterator(TEST_STRING);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hasNextTest(int initialSkipCount, String testString, boolean expected) {
        aCharIterator.skip(initialSkipCount);

        assertEquals(expected, aCharIterator.hasNext(testString));
    }
}