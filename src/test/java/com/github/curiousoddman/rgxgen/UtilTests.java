package com.github.curiousoddman.rgxgen;

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
    public void powTest() {
        List<Integer[]> data = Arrays.asList(
                new Integer[]{10, 0, 1},
                new Integer[]{1, 1, 1},
                new Integer[]{1, 2, 1},
                new Integer[]{1, 3, 1},
                new Integer[]{2, 2, 4},
                new Integer[]{2, 10, 1024}
        );

        for (Object[] datum : data) {
            assertEquals(datum[2], (int) Util.pow((int) datum[0], (int) datum[1]));
        }
    }
}
