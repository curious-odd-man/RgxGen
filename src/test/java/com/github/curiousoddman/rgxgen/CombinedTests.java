package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.data.TestPattern;
import com.github.curiousoddman.rgxgen.generator.nodes.Node;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueGenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueValuesCountingVisitor;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import com.github.curiousoddman.rgxgen.testutil.NodePatternVerifyingVisitor;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class CombinedTests extends CombinedTestTemplate {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object> data() {
        return Arrays.asList(TestPattern.values());
    }

    @Test
    public void parseTest() {
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(aTestPattern.getPattern());
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
    public void countTest() {
        assumeTrue(aTestPattern.hasEstimatedCount());

        UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor();
        aTestPattern.getResultNode()
                    .visit(v);
        assertEquals(aTestPattern.getEstimatedCount(), v.getCount());
    }

    @Test
    public void generateUniqueTest() {
        assumeTrue(aTestPattern.hasAllUniqueValues());

        UniqueGenerationVisitor v = new UniqueGenerationVisitor();
        aTestPattern.getResultNode()
                    .visit(v);
        assertEquals(aTestPattern.getAllUniqueValues(), TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }

    @Test
    public void classRgxGenTest() {
        RgxGen rgxGen = new RgxGen(aTestPattern.getPattern());
        if (aTestPattern.hasEstimatedCount()) {
            assertEquals(aTestPattern.getEstimatedCount(), rgxGen.numUnique());
        }
        List<String> strings = rgxGen.stream()
                                     .limit(1000)
                                     .collect(Collectors.toList());
        for (String generated : strings) {
            boolean result = isValidGenerated(generated);
            assertTrue("Text: '" + generated + "'does not match pattern " + aTestPattern.getPattern(), result);

        }
    }
}
