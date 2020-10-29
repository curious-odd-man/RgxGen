package com.github.curiousoddman.rgxgen.util;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UtilTests {

    @Test
    public void splitTest() {
        String[] abcs = Util.stringToCharsSubstrings("abc");
        assertArrayEquals(new String[]{"a", "b", "c"}, abcs);
    }

    @Test
    public void multiplicateTest() {
        assertEquals("", Util.multiplicate(' ', -1));
        assertEquals("   ", Util.multiplicate(' ', 3));
        assertEquals("XX", Util.multiplicate('X', 2));
    }

    @Test
    public void randomStringTest() {
        Random rnd1 = new Random(10);
        Random rnd2 = new Random(10);
        assertEquals(Util.randomString(rnd1, "asdf"), Util.randomString(rnd2, "asdf"));
    }
}

