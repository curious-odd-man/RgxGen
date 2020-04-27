package com.github.curiousoddman.rgxgen.iterators;

import com.github.curiousoddman.rgxgen.RgxGen;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class InfiniteGenerateTests {
    private static final int ITERATIONS = 100;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"a*", false},
                {"aa+", false},
                {"a.*", false},
                {"a+", false},
                {"za*", false},
                {"za+", false},
                {"foo(?!bar)", true},
                {"(?<!not)foo", true}
        });
    }

    @Parameterized.Parameter
    public String aRegex;

    @Parameterized.Parameter(1)
    public boolean aUseFind;

    private Pattern p;

    @Before
    public void setUp() {
        p = Pattern.compile(aRegex);
    }

    @Test
    public void generateTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        for (int i = 0; i < ITERATIONS; i++) {
            String s = rgxGen.generate();
            if (aUseFind) {
                assertTrue(s, p.matcher(s)
                               .find());
            } else {
                assertTrue(s, p.matcher(s)
                               .matches());
            }
        }
    }

    @Test
    public void aLotOfValuesAvailableTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        StringIterator stringIterator = rgxGen.iterateUnique();
        Set<String> set = new HashSet<>();

        for (int i = 0; i < ITERATIONS * ITERATIONS; i++) {
            String next = stringIterator.next();
            assertTrue(stringIterator.hasNext());
            if (aUseFind) {
                assertTrue(next, p.matcher(next)
                                  .find());
            } else {
                assertTrue(next, p.matcher(next)
                                  .matches());
            }
            assertFalse("Duplicate value: " + next, set.contains(next));
            set.add(next);
        }
    }
}
