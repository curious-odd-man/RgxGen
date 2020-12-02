package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.data.DataInterface;
import com.github.curiousoddman.rgxgen.data.TestPattern;
import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import com.github.curiousoddman.rgxgen.testutil.NodePatternVerifyingVisitor;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import com.github.curiousoddman.rgxgen.visitors.UniqueGenerationVisitor;
import com.github.curiousoddman.rgxgen.visitors.UniqueValuesCountingVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class CombinedTests extends CombinedTestTemplate<TestPattern> {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object> data() {
        return Arrays.asList(TestPattern.values());
    }

    @Test
    public void parseTest() {
        NodeTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(aTestPattern.getPattern());
        Node node = defaultTreeBuilder.get();
        assertEquals(aTestPattern.getResultNode()
                                 .toString(), node.toString());
        NodePatternVerifyingVisitor visitor = new NodePatternVerifyingVisitor(aTestPattern.getResultNode());
        node.visit(visitor);
        assertTrue(visitor.getErrors()
                          .toString(),
                   visitor.getErrors()
                          .isEmpty());
    }

    @Test
    public void countUniqueUsingVisitorTest() {
        assumeTrue(aTestPattern.hasEstimatedCount());
        UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor(new RgxGenProperties());
        aTestPattern.getResultNode()
                    .visit(v);
        assertEquals(aTestPattern.getEstimatedCount(), v.getEstimation()
                                                        .orElse(null));
    }


    @Test
    public void countUniqueTest() {
        assumeTrue(aTestPattern.hasEstimatedCount());
        RgxGen rgxGen = new RgxGen(aTestPattern.getPattern());
        assertEquals(aTestPattern.getEstimatedCount(), rgxGen.getUniqueEstimation()
                                                             .orElse(null));
    }

    @Test
    public void generateUniqueTest() {
        assumeTrue(aTestPattern.hasAllUniqueValues());

        UniqueGenerationVisitor v = new UniqueGenerationVisitor(new RgxGenProperties());
        aTestPattern.getResultNode()
                    .visit(v);
        assertEquals(aTestPattern.getAllUniqueValues(), TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }

    @Test
    public void classRgxGenTest() {
        RgxGen rgxGen = new RgxGen(aTestPattern.getPattern());
        if (aTestPattern.hasEstimatedCount()) {
            assertEquals(aTestPattern.getEstimatedCount(), rgxGen.getUniqueEstimation()
                                                                 .orElse(null));
        }
        for (int i = 0; i < 100; i++) {
            Random rand = new Random(i);
            for (int j = 0; j < 10; j++) {
                String generated = rgxGen.generate(rand);
                boolean result = isValidGenerated(generated);
                assertTrue(createMessage(generated, aTestPattern, i, j), result);
            }
        }
    }

    @Test
    public void classRgxGenCaseInsensitiveTest() {
        setCompiledPattern(Pattern.CASE_INSENSITIVE);

        RgxGen rgxGen = new RgxGen(aTestPattern.getPattern());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        rgxGen.setProperties(properties);

        for (int i = 0; i < 100; i++) {
            Random random = new Random(i);
            for (int j = 0; j < 10; j++) {
                String generated = rgxGen.generate(random);
                boolean result = isValidGenerated(generated);
                assertTrue(createMessage(generated, aTestPattern, i, j), result);
            }
        }
    }

    private static String createMessage(String generated, DataInterface pattern, int i, int j) {
        return "Text: '" + generated + "' does not match pattern '" + pattern.getPattern() + "'. Seed used = " + i + "," + j;
    }
}
