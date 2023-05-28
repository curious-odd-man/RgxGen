package com.github.curiousoddman.rgxgen.model;

import com.github.curiousoddman.rgxgen.RgxGen;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UnicodeCategoryTest {


    @ParameterizedTest
    @EnumSource(UnicodeCategory.class)
    void keysArePresentTest(UnicodeCategory unicodeCategory) {
        assertNotNull(unicodeCategory.getKeys());
        assertFalse(unicodeCategory.getKeys()
                                   .isEmpty());
    }

    @ParameterizedTest
    @EnumSource(UnicodeCategory.class)
    void descriptionPresentTest(UnicodeCategory unicodeCategory) {
        assertNotNull(unicodeCategory.getDescription());
    }

    @ParameterizedTest
    @EnumSource(UnicodeCategory.class)
    void symbolsOrSymbolRangesPresentTest(UnicodeCategory unicodeCategory) {
        boolean rangesPresent = unicodeCategory.getSymbolRanges() != null && !unicodeCategory.getSymbolRanges()
                                                                                             .isEmpty();
        boolean symbolsPresent = unicodeCategory.getSymbols() != null && unicodeCategory.getSymbols().length != 0;
        assertTrue(rangesPresent || symbolsPresent);
    }

    public static Stream<String> getKeyAndPattern() {
        return Arrays.stream(UnicodeCategory.values())
                     .flatMap(uc -> uc.getKeys().stream());

    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getKeyAndPattern")
    void canGeneratedMatchingPatternTest(String key) {
        String pattern = "\\p{" + key + "}{5,10}";
        RgxGen rgxGen = new RgxGen(pattern);
        Random random = new Random(10);
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> rgxGen.generate(random));
        }
    }
}