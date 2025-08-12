package com.github.curiousoddman.rgxgen.config;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.model.WhitespaceChar;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.random.RandomGenerator;
import java.util.regex.Pattern;

import static com.github.curiousoddman.rgxgen.testutil.TestingUtilities.newRandom;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WhitespaceConfigurationTests {
    public static final int COUNT_OF_ITERATIONS = 1000;
    public static final int LARGE_COUNT_OF_ITERATIONS = 1000;

    @Test
    void byDefaultOnlySpaceAndTabIsGeneratedTest() {
        RandomGenerator random = newRandom(100500);
        Pattern pattern = Pattern.compile("[ \t]");
        for (int i = 0; i < COUNT_OF_ITERATIONS; i++) {
            String generated = RgxGen.parse("\\s").generate(random);
            assertTrue(pattern.matcher(generated).matches(), "Failed for value '" + generated + '\'');
        }
    }

    @Test
    void configuredWhitespaceGenerationTest() {
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.WHITESPACE_DEFINITION.setInProperties(properties, Arrays.asList(WhitespaceChar.FORM_FEED, WhitespaceChar.VERTICAL_TAB));
        Pattern pattern = Pattern.compile("[\u000B\f]");
        RandomGenerator random = newRandom(100500);
        for (int i = 0; i < COUNT_OF_ITERATIONS; i++) {
            String generated = RgxGen.parse(properties, "\\s").generate(random);
            assertTrue(pattern.matcher(generated).matches(), "Failed for value '" + generated + '\'');
        }
    }

    @Test
    void notMatchingGenerationAlwaysExcludesAllWhitespacesTest() {
        RandomGenerator random = newRandom(100500);
        Pattern pattern = Pattern.compile("\\s");
        for (int i = 0; i < LARGE_COUNT_OF_ITERATIONS; i++) {
            String generated = RgxGen.parse("\\s").generateNotMatching(random);
            assertFalse(pattern.matcher(generated).matches(), "Failed for value '" + generated + '\'');
        }
    }

    @Test
    void notWhitespaceCharacterAlwaysExcludesAllWhitespacesTest() {
        RandomGenerator random = newRandom(100500);
        Pattern pattern = Pattern.compile("\\s");
        for (int i = 0; i < LARGE_COUNT_OF_ITERATIONS; i++) {
            String generated = RgxGen.parse("\\S").generate(random);
            assertFalse(pattern.matcher(generated).matches(), "Failed for value '" + generated + '\'');
        }
    }

    @Test
    void matchingGenerationWithinSquareBracketsTest() {
        RandomGenerator random = newRandom(100500);
        Pattern pattern = Pattern.compile("[a \t]");
        for (int i = 0; i < COUNT_OF_ITERATIONS; i++) {
            String generated = RgxGen.parse("[a\\s]").generate(random);
            assertTrue(pattern.matcher(generated).matches(), "Failed for value '" + generated + '\'');
        }
    }

    @Test
    void notMatchingGenerationWithinSquareBracketsTest() {
        RandomGenerator random = newRandom(100500);
        Pattern pattern = Pattern.compile("[^a\\s]");
        for (int i = 0; i < COUNT_OF_ITERATIONS; i++) {
            String generated = RgxGen.parse("[a\\s]").generateNotMatching(random);
            assertTrue(pattern.matcher(generated).matches(), "Failed for value '" + generated + '\'');
        }
    }

    @Test
    void matchingGenerationNegativeWithinSquareBracketsTest() {
        RandomGenerator random = newRandom(100500);
        Pattern pattern = Pattern.compile("[^a\\s]");
        for (int i = 0; i < COUNT_OF_ITERATIONS; i++) {
            String generated = RgxGen.parse("[^a\\s]").generate(random);
            assertTrue(pattern.matcher(generated).matches(), "Failed for value '" + generated + '\'');
        }
    }
}
