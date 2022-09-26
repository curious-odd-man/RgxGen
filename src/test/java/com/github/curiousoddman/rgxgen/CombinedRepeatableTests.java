package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.data.TestPattern;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import com.github.curiousoddman.rgxgen.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.visitors.NotMatchingGenerationVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CombinedRepeatableTests extends CombinedTestTemplate<TestPattern> {
    @Parameterized.Parameters(name = "{1}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.stream(TestPattern.values())
                     .flatMap(testPattern -> IntStream.range(0, 100)
                                                      .mapToObj(index -> new Object[]{testPattern, index}))
                     .collect(Collectors.toList());
    }

    @Parameterized.Parameter(1)
    public int aSeed;

    @Test
    public void generateTest() {
        RgxGen rgxGen = aTestPattern.getRgxGen();
        String text = rgxGen.generate(TestingUtilities.newRandom(aSeed));

        boolean result = isValidGenerated(text);
        assertTrue(aTestPattern.getLocation() + "\nText: '" + text + "'does not match pattern " + aTestPattern.getPattern(), result);
    }

    @Test
    public void repeatableGenerationTest() {
        Random rnd1 = TestingUtilities.newRandom(aSeed);
        Random rnd2 = TestingUtilities.newRandom(aSeed);

        RgxGen rgxGen_1 = new RgxGen(aTestPattern.getPattern());
        RgxGen rgxGen_2 = new RgxGen(aTestPattern.getPattern());
        assertEquals(rgxGen_1.generate(rnd1), rgxGen_2.generate(rnd2));
    }

    @Test(timeout = 5000)
    public void generateNotMatchingTest() {
        GenerationVisitor generationVisitor = NotMatchingGenerationVisitor.builder()
                                                                          .withRandom(TestingUtilities.newRandom(aSeed))
                                                                          .get();
        aTestPattern.getResultNode()
                    .visit(generationVisitor);
        boolean result = isValidGenerated(generationVisitor.getString());
        assertFalse("Text: '" + generationVisitor.getString() + "' matches pattern " + aTestPattern.getPattern(), result);
    }

    @Test(timeout = 5000)
    public void repeatableNotMatchingGenerationTest() {
        Random rnd1 = TestingUtilities.newRandom(aSeed);
        Random rnd2 = TestingUtilities.newRandom(aSeed);

        RgxGen rgxGen_1 = new RgxGen(aTestPattern.getPattern());
        RgxGen rgxGen_2 = new RgxGen(aTestPattern.getPattern());
        assertEquals(rgxGen_1.generateNotMatching(rnd1), rgxGen_2.generateNotMatching(rnd2));
    }
}
