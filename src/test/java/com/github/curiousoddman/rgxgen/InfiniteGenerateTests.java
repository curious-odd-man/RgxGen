package com.github.curiousoddman.rgxgen;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class InfiniteGenerateTests {
    private static final int ITERATIONS = 100;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"a*"},
                {"a+"},
                {"za*"},
                {"za+"}
        });
    }

    @Parameterized.Parameter
    public String aRegex;

    @Test
    public void generateTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        for (int i = 0; i < ITERATIONS; i++) {
            String s = rgxGen.generate();
            assertTrue(s, Pattern.compile(aRegex)
                                 .matcher(s)
                                 .matches());
        }
    }

    @Test
    public void generateUniqueTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        Stream<String> uniqueStrings = rgxGen.uStream();
        List<String> collect = uniqueStrings.limit(ITERATIONS)
                                            .collect(Collectors.toList());

        for (String s : collect) {
            assertTrue(s, Pattern.compile(aRegex)
                                 .matcher(s)
                                 .matches());
        }
    }

    @Test
    @Ignore
    public void trulyInfiniteTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        Stream<String> uniqueStrings = rgxGen.uStream()
                                             .skip(10000)
                                             .limit(ITERATIONS);
        List<String> collect = uniqueStrings.collect(Collectors.toList());
        System.out.println(collect);
    }
}
