package com.github.curiousoddman.rgxgen;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class InfiniteGenerateTests {
    private static final int ITERATIONS = 100;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
        });
    }

    @Parameterized.Parameter
    public String aRegex;

    @Test
    public void generateTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        for (int i = 0; i < 100; i++) {
            String s = rgxGen.generate();

            assertTrue(s, Pattern.compile(aRegex)
                                 .matcher(s)
                                 .matches());
        }
    }

    @Test
    public void generateUniqueTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        for (int i = 0; i < 100; i++) {
            String s = rgxGen.generate();
            BigInteger estimation = rgxGen.numUnique();
            Stream<String> uniqueStrings = rgxGen.uStream();

            assertTrue(s, Pattern.compile(aRegex)
                                 .matcher(s)
                                 .matches());
        }
    }
}
