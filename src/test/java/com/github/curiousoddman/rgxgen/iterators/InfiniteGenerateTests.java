package com.github.curiousoddman.rgxgen.iterators;

import com.github.curiousoddman.rgxgen.RgxGen;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InfiniteGenerateTests {
    private static final int ITERATIONS = 100;

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("a*", false),
                Arguments.of("aa+", false),
                Arguments.of("a.*", false),
                Arguments.of("a+", false),
                Arguments.of("za*", false),
                Arguments.of("za+", false),
                Arguments.of("foo(?!bar)", true),
                Arguments.of("(?<!not)foo", true)
        );
    }


    @ParameterizedTest
    @MethodSource("data")
    public void generateTest(String aRegex,
                             boolean aUseFind) {
        Pattern p = Pattern.compile(aRegex);
        RgxGen rgxGen = RgxGen.parse(aRegex);
        for (int i = 0; i < ITERATIONS; i++) {
            String s = rgxGen.generate();
            if (aUseFind) {
                assertTrue(p.matcher(s)
                            .find(), s);
            } else {
                assertTrue(p.matcher(s)
                            .matches(), s);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void aLotOfValuesAvailableTest(String aRegex,
                                          boolean aUseFind) {
        Pattern p = Pattern.compile(aRegex);
        RgxGen rgxGen = RgxGen.parse(aRegex);
        StringIterator stringIterator = rgxGen.iterateUnique();
        Set<String> set = new HashSet<>();

        for (int i = 0; i < ITERATIONS; i++) {
            String next = stringIterator.next();
            assertTrue(stringIterator.hasNext());
            if (aUseFind) {
                assertTrue(p.matcher(next)
                            .find(), next);
            } else {
                assertTrue(p.matcher(next)
                            .matches(), next);
            }
            assertFalse(set.contains(next), "Duplicate value: " + next);
            set.add(next);
        }
    }
}
