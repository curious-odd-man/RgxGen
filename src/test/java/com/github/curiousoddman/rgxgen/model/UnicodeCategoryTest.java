package com.github.curiousoddman.rgxgen.model;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.regex.Pattern;

import static com.github.curiousoddman.rgxgen.model.UnicodeCategory.*;
import static org.junit.jupiter.api.Assertions.*;

class UnicodeCategoryTest {
    public static final List<UnicodeCategory> UNCOMPILABLE_KEYS = Arrays.asList(
            CASED_LETTER,
            IN_LATIN_1_SUPPLEMENT,
            IN_LATIN_EXTENDED_A,
            IN_LATIN_EXTENDED_B,
            IN_GREEK_AND_COPTIC,
            IN_COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS,
            IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A,
            IN_SUPPLEMENTAL_ARROWS_A,
            IN_SUPPLEMENTAL_ARROWS_B,
            IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B,
            IN_ARABIC_PRESENTATION_FORMS_A,
            IN_ARABIC_PRESENTATION_FORMS_B
    );

    public static final int GENERATE_ITERATIONS = 1000;

    @ParameterizedTest
    @EnumSource(UnicodeCategory.class)
    void keysAreDefinedForEachCategoryTest(UnicodeCategory unicodeCategory) {
        assertNotNull(unicodeCategory.getKeys());
        assertFalse(unicodeCategory.getKeys().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(UnicodeCategory.class)
    void descriptionIsDefinedForEachCategoryTest(UnicodeCategory unicodeCategory) {
        assertNotNull(unicodeCategory.getDescription());
    }

    @ParameterizedTest
    @EnumSource(UnicodeCategory.class)
    void isValidCategoryTest(UnicodeCategory unicodeCategory) {
        boolean rangesPresent = unicodeCategory.getSymbolRanges() != null && !unicodeCategory.getSymbolRanges().isEmpty();
        boolean symbolsPresent = unicodeCategory.getSymbols() != null && unicodeCategory.getSymbols().length != 0;
        assertTrue(rangesPresent || symbolsPresent);
    }

    @Nested
    public class GenerateInCategoryTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateInCategoryTest(String key, UnicodeCategory category) {
            String pattern = "\\p" + key + "{5,20}";
            RgxGen rgxGen = new RgxGen(pattern);
            Random random = new Random(pattern.hashCode());
            Optional<Pattern> compiled = compile(pattern, category);
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, compiled, category, true);
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validatePattern(rgxGenTestPattern, () -> rgxGen.generate(random));
            }
        }
    }

    @Nested
    public class GenerateInCategoryNotMatchingTest extends UnicodeCategoryGenerateTestBase {

        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateInCategoryNotMatchingTest(String key, UnicodeCategory category) {
            String pattern = "\\p" + key + "{5,20}";
            RgxGen rgxGen = new RgxGen(pattern);
            Random random = new Random(pattern.hashCode());
            Optional<Pattern> compiled = compile(pattern, category);
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, compiled, category, false);
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validatePattern(rgxGenTestPattern, () -> rgxGen.generateNotMatching(random));
            }
        }
    }

    @Nested
    public class GenerateUniqueTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateUniqueTest(String key, UnicodeCategory category) {
            String pattern = "\\p" + key + "{5,20}";
            RgxGen rgxGen = new RgxGen(pattern);
            StringIterator stringIterator = rgxGen.iterateUnique();
            Optional<Pattern> compiled = compile(pattern, category);
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, compiled, category, true);
            for (int i = 0; i < GENERATE_ITERATIONS && stringIterator.hasNext(); i++) {
                validatePattern(rgxGenTestPattern, stringIterator::next);
            }
        }
    }

    @Nested
    public class GenerateNotInCategoryTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateNotInCategoryTest(String key, UnicodeCategory category) {
            String pattern = "\\P" + key + "{5,20}";
            RgxGen rgxGen = new RgxGen(pattern);
            Optional<Pattern> compiled = compile(pattern, category);
            Random random = new Random(pattern.hashCode());
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, compiled, category, true);
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validatePattern(rgxGenTestPattern, () -> rgxGen.generate(random));
            }
        }
    }
}