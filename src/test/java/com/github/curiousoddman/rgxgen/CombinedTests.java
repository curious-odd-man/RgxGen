package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.Node;
import com.github.curiousoddman.rgxgen.generator.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.NotMatchingGenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueGenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueValuesCountingVisitor;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class CombinedTests {
    @Parameterized.Parameters(name = "{1}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.stream(TestPattern.values())
                     .flatMap(testPattern -> IntStream.range(0, 100)
                                                      .mapToObj(index -> new Object[]{testPattern, index}))
                     .collect(Collectors.toList());
    }

    @Parameterized.Parameter
    public TestPattern aTestPattern;

    @Parameterized.Parameter(1)
    public int aSeed;

    private Pattern aPattern;

    @Before
    public void setUp() {
        aPattern = Pattern.compile(aTestPattern.aPattern);
    }

    @Test
    public void parseTest() {
        assumeTrue("Only need this test once", aSeed == 0);
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(aTestPattern.aPattern);
        Node node = defaultTreeBuilder.get();
        assertEquals(aTestPattern.aResultNode.toString(), node.toString());
        NodePatternVerifyingVisitor visitor = new NodePatternVerifyingVisitor(aTestPattern.aResultNode);
        node.visit(visitor);
        assertTrue(visitor.getErrors()
                          .toString(),
                   visitor.getErrors()
                          .isEmpty());
    }

    @Test
    public void countTest() {
        assumeTrue("Only need this test once", aSeed == 0);
        assumeTrue(aTestPattern.hasEstimatedCound());

        UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor();
        aTestPattern.aResultNode.visit(v);
        assertEquals(aTestPattern.aEstimatedCount, v.getCount());
    }

    private boolean isValidGenerated(String value) {
        if (aTestPattern.useFindForMatching()) {
            return aPattern.matcher(value)
                           .find();
        } else {
            return aPattern.matcher(value)
                           .matches();
        }
    }

    @Test
    public void generateTest() {
        GenerationVisitor generationVisitor = new GenerationVisitor(new Random(aSeed));
        aTestPattern.aResultNode.visit(generationVisitor);
        boolean result = isValidGenerated(generationVisitor.getString());
        assertTrue("Text: '" + generationVisitor.getString() + "'does not match pattern " + aTestPattern.aPattern, result);
    }

    @Test
    public void generateUniqueTest() {
        assumeTrue("Only need this test once", aSeed == 0);
        assumeTrue(aTestPattern.hasAllUniqueValues());

        UniqueGenerationVisitor v = new UniqueGenerationVisitor();
        aTestPattern.aResultNode.visit(v);
        assertEquals(aTestPattern.aAllUniqueValues, TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }

    @Test
    public void classRgxGenTest() {
        assumeTrue("Only need this test once", aSeed == 0);
        RgxGen rgxGen = new RgxGen(aTestPattern.aPattern);
        if (aTestPattern.hasEstimatedCound()) {
            assertEquals(aTestPattern.aEstimatedCount, rgxGen.numUnique());
        }
        List<String> strings = rgxGen.stream()
                                     .limit(1000)
                                     .collect(Collectors.toList());
        for (String string : strings) {
            boolean result = isValidGenerated(string);
            assertTrue("Text: '" + string + "'does not match pattern " + aTestPattern.aPattern, result);

        }
    }

    @Test
    public void repeatableGenerationTest() {
        Random rnd1 = new Random(aSeed);
        Random rnd2 = new Random(aSeed);

        RgxGen rgxGen_1 = new RgxGen(aTestPattern.aPattern);
        RgxGen rgxGen_2 = new RgxGen(aTestPattern.aPattern);
        assertEquals(rgxGen_1.generate(rnd1), rgxGen_2.generate(rnd2));
    }

    @Test(timeout = 5000)
    public void generateNotMatchingTest() {
        GenerationVisitor generationVisitor = new NotMatchingGenerationVisitor(new Random(aSeed));
        aTestPattern.aResultNode.visit(generationVisitor);
        boolean result = isValidGenerated(generationVisitor.getString());
        assertFalse("Text: '" + generationVisitor.getString() + "' matches pattern " + aTestPattern.aPattern, result);
    }

    @Test(timeout = 10000)
    public void repeatableNotMatchingGenerationTest() {
        Random rnd1 = new Random(aSeed);
        Random rnd2 = new Random(aSeed);

        RgxGen rgxGen_1 = new RgxGen(aTestPattern.aPattern);
        RgxGen rgxGen_2 = new RgxGen(aTestPattern.aPattern);
        assertEquals(rgxGen_1.generateNotMatching(rnd1), rgxGen_2.generateNotMatching(rnd2));
    }
}
