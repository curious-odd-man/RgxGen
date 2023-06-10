package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.github.curiousoddman.rgxgen.util.Util.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class UtilTests {
    @Test
    public void multiplicateTest() {
        assertEquals("", repeatChar(' ', -1));
        assertEquals("   ", repeatChar(' ', 3));
        assertEquals("XX", repeatChar('X', 2));
    }

    @Test
    public void randomChangeCaseTest() {
        boolean lower = false;
        boolean upper = false;
        for (int i = 0; i < 100; i++) {
            char result = randomlyChangeCase(TestingUtilities.newRandom(i), "a")
                    .charAt(0);
            if (Character.isLowerCase(result)) {
                lower = true;
            } else if (Character.isUpperCase(result)) {
                upper = true;
            }

            if (lower && upper) {
                break;
            }
        }

        assertTrue(lower && upper, "Generated both - upper and lower case letter");
    }

    @Test
    public void randomChangeCaseDigitTest() {
        boolean lower = false;
        boolean upper = false;
        for (int i = 0; i < 100; i++) {
            char result = randomlyChangeCase(TestingUtilities.newRandom(i), "1")
                    .charAt(0);
            assertEquals('1', result);
            if (Character.isLowerCase(result)) {
                lower = true;
            } else if (Character.isUpperCase(result)) {
                upper = true;
            }

            if (lower && upper) {
                break;
            }
        }

        assertFalse(lower || upper, "Digit case did not change.");
    }

    @Test
    public void countCaseInsensitiveVariationsTest() {
        assertEquals(1, countCaseInsensitiveVariations("1234").intValue());
        assertEquals(2, countCaseInsensitiveVariations("a").intValue());
        assertEquals(2, countCaseInsensitiveVariations("a1").intValue());
        assertEquals(2, countCaseInsensitiveVariations("1a").intValue());
        assertEquals(4, countCaseInsensitiveVariations("ab").intValue());
        assertEquals(4, countCaseInsensitiveVariations("AB").intValue());
        assertEquals(8, countCaseInsensitiveVariations("abc").intValue());
    }

    @Test
    public void indexOfNextCaseSensitiveCharacterTest() {
        assertEquals(0, indexOfNextCaseSensitiveCharacter("a123", 0).getAsInt());
        assertEquals(-142536, indexOfNextCaseSensitiveCharacter("a123", 1).orElse(-142536));
        assertEquals(1, indexOfNextCaseSensitiveCharacter("1a123", 0).getAsInt());
        assertEquals(3, indexOfNextCaseSensitiveCharacter("123a", 0).getAsInt());
    }

    @Test
    void textVariationsTest() {

        Set<String> strings = makeVariations(
                asList("L", "Spacing_Combining_Mark"),
                '_',
                '-', ' '
        );

        Set<String> expected = new HashSet<>(asList("Spacing_Combining_Mark", "Spacing-Combining-Mark", "L", "Spacing Combining Mark"));
        assertEquals(expected, strings);
    }

}

