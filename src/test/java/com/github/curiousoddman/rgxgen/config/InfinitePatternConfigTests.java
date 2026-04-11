package com.github.curiousoddman.rgxgen.config;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class InfinitePatternConfigTests {

    private static boolean isCorrect(String value) {
        return value != null && value.isEmpty()
                || "x".equals(value) || "xx".equals(value);
    }

    @Test
    void maxLengthIsRespectedTest() {
        RgxGenProperties properties_3 = new RgxGenProperties();
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(properties_3, 2);
        RgxGen rgxGen_3 = RgxGen.parse(properties_3, "x*");
        for (int i = 0; i < 1000; i++) {
            String value = rgxGen_3.generate(TestingUtilities.newRandom(i));
            assertTrue(isCorrect(value), "Expected to have either empty, or 'x' or 'xx' string. But got " + value);
        }
    }

    @Test
    void starMin0AllowsEmptyTest() {
        assertMinLength("x*", 0, 0);
    }

    @Test
    void starMin1DisallowsEmptyTest() {
        assertMinLength("x*", 1, 1);
    }

    @Test
    void starMin2Test() {
        assertMinLength("x*", 2, 2);
    }

    @Test
    void starShouldNeverGenerateBelowMinTest() {
        RgxGen rgxGen = getRgxGenWithMinRepetition("x*", 3);

        for (int i = 0; i < 50; i++) {
            String val = rgxGen.generate(new Random(i));
            assertFalse(val.length() < 3);
        }
    }

    @Test
    void plusDefaultIsOneTest() {
        assertMinLength("x+", 0, 1);
    }

    @Test
    void plusRespectsHigherMinTest() {
        assertMinLength("x+", 3, 3);
    }

    @Test
    void questionMarkMin0Test() {
        assertMinLength("x?", 0, 0);
    }

    @Test
    void questionMarkMin1Test() {
        assertMinLength("x?", 1, 1);
    }

    @Test
    void questionMarkMinGreaterThanMaxTest() {
        RgxGenConfigurationException e = assertThrows(RgxGenConfigurationException.class, () -> assertMinLength("x?", 2, 1));
        assertEquals("Min (2) repetition is greater than Max (1). Check configuration for infinite pattern repetition!", e.getMessage());
    }

    @Test
    void boundedLowerRespectsOwnWhenConfigIsSmallerThanMinTest() {
        assertMinLength("x{3,}", 1, 3);
    }

    @Test
    void boundedLowerOnlyRespectsConfigWhenConfigIsHigherThanMinTest() {
        assertMinLength("x{1,}", 4, 4);
    }

    @Test
    void boundedRangeNormalTest() {
        assertMinLength("x{2,5}", 1, 2);
    }

    @Test
    void boundedRangeConfigMinOverridesLowerTest() {
        assertMinLength("x{2,5}", 3, 3);
    }

    @Test
    void boundedRangeGlobalMinExceedsMaxTest() {
        RgxGen rgxGen = getRgxGenWithMinRepetition("x{2,5}", 10);

        Random random = new Random(0);
        RgxGenConfigurationException e = assertThrows(RgxGenConfigurationException.class, () -> rgxGen.generate(random));
        assertEquals("Min (10) repetition is greater than Max (5). Check configuration for infinite pattern repetition!", e.getMessage());
    }

    @Test
    void nestedPatternsTest() {
        assertMinLength("(ab)*", 2, 4); // 2 repetitions => "abab"
    }

    @Test
    void mixedPatternTest() {
        RgxGen rgxGen = getRgxGenWithMinRepetition("a.*b", 2);

        for (int i = 0; i < 50; i++) {
            String val = rgxGen.generate(new Random(i));
            assertTrue(val.length() >= 4); // a + 2 chars + b
        }
    }

    @Test
    void alternationWithStarTest() {
        assertMinLength("(a|b)*", 3, 3);
    }


    private void assertMinLength(String pattern, int minRepetition, int expectedMinLength) {
        RgxGen rgxGen = getRgxGenWithMinRepetition(pattern, minRepetition);

        for (int i = 0; i < 50; i++) {
            String val = rgxGen.generate(new Random(i));
            assertTrue(
                    val.length() >= expectedMinLength,
                    "Generated value '" + val + "' is shorter than expected min length " + expectedMinLength
            );
        }
    }

    private static RgxGen getRgxGenWithMinRepetition(String pattern, int minRepetition) {
        RgxGenProperties props = new RgxGenProperties();
        RgxGenOption.INFINITE_PATTERN_MINIMUM_REPETITION.setInProperties(props, minRepetition);
        return RgxGen.parse(props, pattern);
    }
}
