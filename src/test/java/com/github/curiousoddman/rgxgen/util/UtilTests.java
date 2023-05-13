package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTests {
    @Test
    public void multiplicateTest() {
        assertEquals("", Util.repeat_char(' ', -1));
        assertEquals("   ", Util.repeat_char(' ', 3));
        assertEquals("XX", Util.repeat_char('X', 2));
    }

    @Test
    public void randomChangeCaseTest() {
        boolean lower = false;
        boolean upper = false;
        for (int i = 0; i < 100; i++) {
            char result = Util.randomlyChangeCase(TestingUtilities.newRandom(i), "a")
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
            char result = Util.randomlyChangeCase(TestingUtilities.newRandom(i), "1")
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
        assertEquals(1, Util.countCaseInsensitiveVariations("1234")
                            .intValue());
        assertEquals(2, Util.countCaseInsensitiveVariations("a")
                            .intValue());
        assertEquals(2, Util.countCaseInsensitiveVariations("a1")
                            .intValue());
        assertEquals(2, Util.countCaseInsensitiveVariations("1a")
                            .intValue());
        assertEquals(4, Util.countCaseInsensitiveVariations("ab")
                            .intValue());
        assertEquals(4, Util.countCaseInsensitiveVariations("AB")
                            .intValue());
        assertEquals(8, Util.countCaseInsensitiveVariations("abc")
                            .intValue());
    }

    @Test
    public void indexOfNextCaseSensitiveCharacterTest() {
        assertEquals(0, Util.indexOfNextCaseSensitiveCharacter("a123", 0)
                            .getAsInt());
        assertEquals(-142536, Util.indexOfNextCaseSensitiveCharacter("a123", 1)
                                  .orElse(-142536));
        assertEquals(1, Util.indexOfNextCaseSensitiveCharacter("1a123", 0)
                            .getAsInt());
        assertEquals(3, Util.indexOfNextCaseSensitiveCharacter("123a", 0)
                            .getAsInt());
    }
}

