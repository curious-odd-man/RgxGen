package com.github.curiousoddman.rgxgen;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * These tests are the tests which require find() instead of matches() to verify the generated patter.
 * Apparently lookahead and lookbehind does not match
 */
@RunWith(Parameterized.class)
public class LookaroundTests {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Positive lookahead", "foo(?=b)"},
                {"Negative lookahead", "foo(?!bab)"},
                {"Positive lookbehind", "(?<=foo)bar"},
                {"Negative lookbehind", "(?<!not)fof"}
        });
    }

    @Parameterized.Parameter
    public String aName;

    @Parameterized.Parameter(1)
    public String aRegex;

    @Test
    public void generateTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        for (int i = 0; i < 100; i++) {
            String s = rgxGen.generate();
            assertTrue("Text: '" + s + "'does not match pattern " + aRegex, Pattern.compile(aRegex)
                                                                                   .matcher(s)
                                                                                   .find());
        }
    }
}
