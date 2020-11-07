package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.data.TestPattern;
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
public class CombinedRepeatableTests extends CombinedTestTemplate {
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
        GenerationVisitor generationVisitor = GenerationVisitor.builder()
                                                               .withRandom(new Random(aSeed))
                                                               .get();
        aTestPattern.getResultNode()
                    .visit(generationVisitor);
        boolean result = isValidGenerated(generationVisitor.getString());
        assertTrue("Text: '" + generationVisitor.getString() + "'does not match pattern " + aTestPattern.getPattern(), result);
    }

    @Test
    public void repeatableGenerationTest() {
        Random rnd1 = new Random(aSeed);
        Random rnd2 = new Random(aSeed);

        RgxGen rgxGen_1 = new RgxGen(aTestPattern.getPattern());
        RgxGen rgxGen_2 = new RgxGen(aTestPattern.getPattern());
        assertEquals(rgxGen_1.generate(rnd1), rgxGen_2.generate(rnd2));
    }

    @Test(timeout = 5000)
    public void generateNotMatchingTest() {
        GenerationVisitor generationVisitor = NotMatchingGenerationVisitor.builder()
                                                                          .withRandom(new Random(aSeed))
                                                                          .get();
        aTestPattern.getResultNode()
                    .visit(generationVisitor);
        boolean result = isValidGenerated(generationVisitor.getString());
        assertFalse("Text: '" + generationVisitor.getString() + "' matches pattern " + aTestPattern.getPattern(), result);
    }

    @Test(timeout = 5000)
    public void repeatableNotMatchingGenerationTest() {
        Random rnd1 = new Random(aSeed);
        Random rnd2 = new Random(aSeed);

        RgxGen rgxGen_1 = new RgxGen(aTestPattern.getPattern());
        RgxGen rgxGen_2 = new RgxGen(aTestPattern.getPattern());
        assertEquals(rgxGen_1.generateNotMatching(rnd1), rgxGen_2.generateNotMatching(rnd2));
    }
}
