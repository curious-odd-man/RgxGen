package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.parsing.dflt.CharIterator;
import com.github.curiousoddman.rgxgen.util.Util;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UtilTests {

    @Test
    public void splitTest() {
        String[] abcs = Util.stringToCharsSubstrings("abc");
        assertArrayEquals(new String[]{"a", "b", "c"}, abcs);
    }

    @Test
    public void substringUntilTest() {
        List<Object[]> data = Arrays.asList(
                new Object[]{"()", 1, ')', ""},
                new Object[]{"(a)", 1, ')', "a"},
                new Object[]{"(a\\)b)", 1, ')', "a\\)b"},
                new Object[]{"(ac\\\\)", 1, ')', "ac\\\\"}
        );

        for (Object[] datum : data) {
            final CharIterator charIterator = new CharIterator(datum[0].toString());
            charIterator.next((int) datum[1]);
            assertEquals(Arrays.toString(datum), datum[3], charIterator.nextUntil((char) datum[2]));
        }
    }

    @Test
    public void takeWhileTest() {
        List<Object[]> data = Arrays.asList(
                new Object[]{"asdf1234fdsa", 4, "1234"},
                new Object[]{"asdf1234", 4, "1234"}
        );

        for (Object[] datum : data) {
            final CharIterator charIterator = new CharIterator(datum[0].toString());
            charIterator.next((int) datum[1]);
            assertEquals(Arrays.toString(datum), datum[2], charIterator.takeWhile(Character::isDigit));
        }
    }

    @Test
    public void multiplicateTest() {
        assertEquals("", Util.multiplicate(" ", -1));
        assertEquals("   ", Util.multiplicate(" ", 3));
        assertEquals("XxXXxX", Util.multiplicate("XxX", 2));
    }
}

