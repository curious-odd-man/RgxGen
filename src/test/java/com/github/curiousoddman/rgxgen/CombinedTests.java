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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CombinedTests extends CombinedTestTemplate<TestPattern> {
    public static Stream<TestPattern> getPatterns() {
        return Arrays.stream(TestPattern.values());
    }


    @ParameterizedTest
    @MethodSource("getPatterns")
    public void parseTest(TestPattern testPattern) {
        NodeTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(testPattern.getPattern());
        Node node = defaultTreeBuilder.get();
        assertEquals(testPattern.getResultNode().toString(), node.toString());
        NodePatternVerifyingVisitor visitor = new NodePatternVerifyingVisitor(testPattern.getResultNode());
        node.visit(visitor);
        assertTrue(visitor.getErrors().isEmpty(), visitor.getErrors().toString());
    }

    @ParameterizedTest
    @MethodSource("getPatterns")
    public void countUniqueUsingVisitorTest(TestPattern testPattern) {
        assumeTrue(testPattern.hasEstimatedCount());
        UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor(new RgxGenProperties());
        testPattern.getResultNode().visit(v);
        assertEquals(testPattern.getEstimatedCount(), v.getEstimation().orElse(null));
    }

    @ParameterizedTest
    @MethodSource("getPatterns")
    public void countUniqueTest(TestPattern testPattern) {
        assumeTrue(testPattern.hasEstimatedCount());
        RgxGen rgxGen = new RgxGen(testPattern.getPattern());
        assertEquals(testPattern.getEstimatedCount(), rgxGen.getUniqueEstimation().orElse(null));
    }

    @ParameterizedTest
    @MethodSource("getPatterns")
    public void generateUniqueTest(TestPattern testPattern) {
        assumeTrue(testPattern.hasAllUniqueValues());

        UniqueGenerationVisitor v = new UniqueGenerationVisitor(new RgxGenProperties());
        testPattern.getResultNode().visit(v);
        assertEquals(testPattern.getAllUniqueValues(), TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }

    @ParameterizedTest
    @MethodSource("getPatterns")
    public void classRgxGenTest(TestPattern testPattern) {
        RgxGen rgxGen = new RgxGen(testPattern.getPattern());
        if (testPattern.hasEstimatedCount()) {
            assertEquals(testPattern.getEstimatedCount(), rgxGen.getUniqueEstimation().orElse(null));
        }
        for (int i = 0; i < 100; i++) {
            Random rand = TestingUtilities.newRandom(i);
            for (int j = 0; j < 10; j++) {
                String generated = rgxGen.generate(rand);
                boolean result = isValidGenerated(testPattern, generated, 0);
                assertTrue(result, createMessage(generated, testPattern, i, j));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("getPatterns")
    public void classRgxGenCaseInsensitiveTest(TestPattern testPattern) {
        RgxGen rgxGen = new RgxGen(testPattern.getPattern());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        rgxGen.setProperties(properties);

        for (int i = 0; i < 100; i++) {
            Random random = TestingUtilities.newRandom(i);
            for (int j = 0; j < 10; j++) {
                String generated = rgxGen.generate(random);
                boolean result = isValidGenerated(testPattern, generated, Pattern.CASE_INSENSITIVE);
                assertTrue(result, createMessage(generated, testPattern, i, j));
            }
        }
    }

    private static String createMessage(String generated, DataInterface pattern, int i, int j) {
        return "Text: '" + generated + "' does not match pattern '" + pattern.getPattern() + "'. Seed used = " + i + "," + j;
    }
}
