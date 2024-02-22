package com.github.curiousoddman.rgxgen.model;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

import static com.github.curiousoddman.rgxgen.testutil.TestingUtilities.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class UnicodeCategoryTest {
    public static final int GENERATE_ITERATIONS = 100;

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
    public class SymbolsInCategoryTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategoryAndSingleSymbol")
        void correctSymbolsInCategoryTest(String name, String key, UnicodeCategory category, char symbol) {
            String pattern = "\\p" + key;
            Optional<Pattern> compiled = compile(pattern, category);
            if (!compiled.isPresent()) {
                return;
            }

            assertTrue(compiled.get().matcher("" + symbol).matches());
        }
    }

    @Nested
    public class SymbolsNotInCategoryTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategoryAndSingleSymbolNotInCategory")
        void correctSymbolsInCategoryTest(String name, String key, UnicodeCategory category, char symbol) {
            String pattern = "\\P" + key;
            Optional<Pattern> compiled = compile(pattern, category);
            if (!compiled.isPresent()) {
                return;
            }

            assertTrue(compiled.get().matcher("" + symbol).matches());
        }
    }

    @Nested
    public class GenerateInCategoryTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateInCategoryTest(String key, UnicodeCategory category) {
            String pattern = "\\p" + key + "{5,20}";
            RgxGen rgxGen = RgxGen.parse(pattern);
            Random random = newRandom(pattern.hashCode());
            Optional<Pattern> compiled = compile(pattern, category);
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, compiled, category, true);
            ValidationResult validationResult = new ValidationResult();
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validateGeneratedText(rgxGenTestPattern, () -> rgxGen.generate(random), validationResult);
            }
            validationResult.assertPassed();
        }
    }

    @Nested
    public class GenerateInCategoryNotMatchingTest extends UnicodeCategoryGenerateTestBase {

        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateInCategoryNotMatchingTest(String key, UnicodeCategory category) {
            String pattern = "\\p" + key + "{5,20}";
            RgxGen rgxGen = RgxGen.parse(pattern);
            Random random = newRandom(pattern.hashCode());
            Optional<Pattern> compiled = compile(pattern, category);
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, compiled, category, false);
            ValidationResult validationResult = new ValidationResult();
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validateGeneratedText(rgxGenTestPattern, () -> rgxGen.generateNotMatching(random), validationResult);
            }
            validationResult.assertPassed();
        }
    }

    @Nested
    public class GenerateUniqueTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateUniqueTest(String key, UnicodeCategory category) {
            String pattern = "\\p" + key + "{5,20}";
            RgxGen rgxGen = RgxGen.parse(pattern);
            StringIterator stringIterator = rgxGen.iterateUnique();
            Optional<Pattern> compiled = compile(pattern, category);
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, compiled, category, true);
            ValidationResult validationResult = new ValidationResult();
            for (int i = 0; i < GENERATE_ITERATIONS && stringIterator.hasNext(); i++) {
                validateGeneratedText(rgxGenTestPattern, stringIterator::next, validationResult);
            }
            validationResult.assertPassed();
        }
    }

    @Nested
    public class GenerateNotInCategoryTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateNotInCategoryTest(String key, UnicodeCategory category) {
            String pattern = "\\P" + key + "{5,20}";
            RgxGen rgxGen = RgxGen.parse(pattern);
            Optional<Pattern> compiled = compile(pattern, category);
            Random random = newRandom(pattern.hashCode());
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, compiled, category, true);
            ValidationResult validationResult = new ValidationResult();
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validateGeneratedText(rgxGenTestPattern, () -> rgxGen.generate(random), validationResult);
            }
            validationResult.assertPassed();
        }
    }
}