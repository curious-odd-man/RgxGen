package com.github.curiousoddman.rgxgen.model;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import com.github.curiousoddman.rgxgen.model.data.CategoryLetterTestData;
import com.github.curiousoddman.rgxgen.model.data.CategoryTestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

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
        void correctSymbolsInCategoryTest(CategoryLetterTestData testData) {
            String pattern = "\\p" + testData.getCategoryTestData().getKey();
            registerTestedCategory(testData.getCategoryTestData().getCategory());
            assertTrue(Pattern.compile(pattern).matcher("" + testData.getChar()).matches());
        }
    }

    @Nested
    public class SymbolsNotInCategoryTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategoryAndSingleSymbolNotInCategory")
        void correctSymbolsInCategoryTest(CategoryLetterTestData testData) {
            String pattern = "\\P" + testData.getCategoryTestData().getKey();
            registerTestedCategory(testData.getCategoryTestData().getCategory());
            assertTrue(Pattern.compile(pattern).matcher("" + testData.getChar()).matches());
        }
    }

    @Nested
    public class GenerateInCategoryTest extends UnicodeCategoryGenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getCategoryTestData")
        void generateInCategoryTest(CategoryTestData categoryTestData) {
            Pattern inCategoryPattern = categoryTestData.getInCategoryPattern();
            String pattern = inCategoryPattern.pattern();
            RgxGen rgxGen = RgxGen.parse(pattern);
            Random random = newRandom(pattern.hashCode());
            registerTestedCategory(categoryTestData.getCategory());
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, inCategoryPattern, categoryTestData.getCategory(), true);
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
        @MethodSource("getCategoryTestData")
        void generateInCategoryNotMatchingTest(CategoryTestData categoryTestData) {
            Pattern inCategoryPattern = categoryTestData.getInCategoryPattern();
            String pattern = inCategoryPattern.pattern();
            RgxGen rgxGen = RgxGen.parse(pattern);
            Random random = newRandom(pattern.hashCode());
            registerTestedCategory(categoryTestData.getCategory());
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, inCategoryPattern, categoryTestData.getCategory(), false);
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
        @MethodSource("getCategoryTestData")
        void generateUniqueTest(CategoryTestData categoryTestData) {
            Pattern inCategoryPattern = categoryTestData.getInCategoryPattern();
            String pattern = inCategoryPattern.pattern();
            RgxGen rgxGen = RgxGen.parse(pattern);
            StringIterator stringIterator = rgxGen.iterateUnique();
            registerTestedCategory(categoryTestData.getCategory());
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, inCategoryPattern, categoryTestData.getCategory(), true);
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
        @MethodSource("getCategoryTestData")
        void generateNotInCategoryTest(CategoryTestData categoryTestData) {
            Pattern notInCategoryPattern = categoryTestData.getNotInCategoryPattern();
            String pattern = notInCategoryPattern.pattern();
            RgxGen rgxGen = RgxGen.parse(pattern);
            registerTestedCategory(categoryTestData.getCategory());
            Random random = newRandom(pattern.hashCode());
            RgxGenTestPattern rgxGenTestPattern = new RgxGenTestPattern(pattern, notInCategoryPattern, categoryTestData.getCategory(), true);
            ValidationResult validationResult = new ValidationResult();
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validateGeneratedText(rgxGenTestPattern, () -> rgxGen.generate(random), validationResult);
            }
            validationResult.assertPassed();
        }
    }
}