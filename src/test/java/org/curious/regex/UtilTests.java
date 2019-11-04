package org.curious.regex;

import org.curious.regex.util.Util;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class UtilTests {

    @Test
    public void splitTest() {
        String[] abcs = Util.stringToCharsSubstrings("abc");
        assertArrayEquals(new String[]{"a", "b", "c"}, abcs);
    }
}
