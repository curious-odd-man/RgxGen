package com.github.curiousoddman.rgxgen.model;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
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

    public static Stream<String> getKey() {
        return Arrays.stream(UnicodeCategory.values())
                     .flatMap(uc -> uc.getKeys().stream());

    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getKey")
    void generateInCategoryTest(String key) {
        String pattern = "\\p{" + key + "}{5,10}";
        RgxGen rgxGen = new RgxGen(pattern);
        Random random = new Random(10);
        Pattern compiled = Pattern.compile(pattern, Pattern.UNICODE_CHARACTER_CLASS);
        for (int i = 0; i < 10; i++) {
            String s = assertDoesNotThrow(() -> rgxGen.generate(random));
            System.out.println(s);
            assertTrue(compiled.matcher(s).matches());
        }
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getKey")
    void generateInCategoryNotMatchingTest(String key) {
        String pattern = "\\p{" + key + "}{5,10}";
        RgxGen rgxGen = new RgxGen(pattern);
        Random random = new Random(10);
        Pattern compiled = Pattern.compile(pattern, Pattern.UNICODE_CHARACTER_CLASS);
        for (int i = 0; i < 10; i++) {
            String s = assertDoesNotThrow(() -> rgxGen.generateNotMatching(random));
            System.out.println(s);
            assertTrue(compiled.matcher(s).matches());
        }
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getKey")
    void generateUniqueTest(String key) {
        String pattern = "\\p{" + key + "}{5,10}";
        RgxGen rgxGen = new RgxGen(pattern);
        Random random = new Random(10);
        StringIterator stringIterator = rgxGen.iterateUnique();
        Pattern compiled = Pattern.compile(pattern, Pattern.UNICODE_CHARACTER_CLASS);
        for (int i = 0; i < 10 && stringIterator.hasNext(); i++) {
            String s = stringIterator.next();
            System.out.println(s);
            assertTrue(compiled.matcher(s).matches());
        }
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getKey")
    void generateNotInCategoryTest(String key) {
        String pattern = "\\P{" + key + "}{5,10}";
        RgxGen rgxGen = new RgxGen(pattern);
        Pattern compiled = Pattern.compile(pattern, Pattern.UNICODE_CHARACTER_CLASS);
        Random random = new Random(10);
        for (int i = 0; i < 10; i++) {
            String s = assertDoesNotThrow(() -> rgxGen.generate(random));
            System.out.println(s);
            assertTrue(compiled.matcher(s).matches());
        }
    }
}