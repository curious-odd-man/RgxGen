package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.data.DataInterface;
import com.github.curiousoddman.rgxgen.data.TestPattern;
import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultNodeCreator;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import com.github.curiousoddman.rgxgen.visitors.PrettyPrintVisitor;
import com.github.curiousoddman.rgxgen.visitors.UniqueGenerationVisitor;
import com.github.curiousoddman.rgxgen.visitors.UniqueValuesCountingVisitor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CombinedTests extends CombinedTestTemplate<TestPattern> {
    private static final Map<TestPattern, Node> NODES_CACHE = new EnumMap<>(TestPattern.class);

    public static Node getOrCreateNode(TestPattern testPattern) {
        return NODES_CACHE.computeIfAbsent(testPattern, k -> {
            NodeTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(testPattern.getPattern(), new DefaultNodeCreator(), null);
            return defaultTreeBuilder.get();
        });
    }

    private static String createMessage(String generated, DataInterface pattern, int i, int j) {
        return "Text: '" + generated + "' does not match pattern '" + pattern.getPattern() + "'. Seed used = " + i + ',' + j;
    }

    @ParameterizedTest
    @EnumSource(TestPattern.class)
    public void parseTest(TestPattern testPattern) throws IOException {
        Node node = getOrCreateNode(testPattern);
        PrettyPrintVisitor prettyPrintVisitor = new PrettyPrintVisitor();
        node.visit(prettyPrintVisitor);
        String prettyPrintedNodes = prettyPrintVisitor.getResult();
        testPattern.assertFileContents(prettyPrintedNodes);
    }

    @ParameterizedTest
    @EnumSource(TestPattern.class)
    public void countUniqueUsingVisitorTest(TestPattern testPattern) {
        assumeTrue(testPattern.hasEstimatedCount());
        Node node = getOrCreateNode(testPattern);

        UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor(new RgxGenProperties());
        node.visit(v);
        assertEquals(testPattern.getEstimatedCount(), v.getEstimation().orElse(null));
    }

    @ParameterizedTest
    @EnumSource(TestPattern.class)
    public void countUniqueTest(TestPattern testPattern) {
        assumeTrue(testPattern.hasEstimatedCount());
        RgxGen rgxGen = RgxGen.parse(testPattern.getPattern());
        assertEquals(testPattern.getEstimatedCount(), rgxGen.getUniqueEstimation().orElse(null));
    }

    @ParameterizedTest
    @EnumSource(TestPattern.class)
    public void generateUniqueTest(TestPattern testPattern) {
        assumeTrue(testPattern.hasAllUniqueValues());

        UniqueGenerationVisitor v = new UniqueGenerationVisitor(new RgxGenProperties());
        getOrCreateNode(testPattern).visit(v);
        assertEquals(testPattern.getAllUniqueValues(), TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }

    @ParameterizedTest
    @EnumSource(TestPattern.class)
    public void classRgxGenTest(TestPattern testPattern) {
        RgxGen rgxGen = RgxGen.parse(testPattern.getPattern());
        if (testPattern.hasEstimatedCount()) {
            assertEquals(testPattern.getEstimatedCount(), rgxGen.getUniqueEstimation().orElse(null));
        }
        for (int i = 0; i < 100; i++) {
            RandomGenerator rand = TestingUtilities.newRandom(i);
            for (int j = 0; j < 10; j++) {
                String generated = rgxGen.generate(rand);
                boolean result = isValidGenerated(testPattern, generated, 0);
                assertTrue(result, createMessage(generated, testPattern, i, j));
            }
        }
    }

    @ParameterizedTest
    @EnumSource(TestPattern.class)
    public void classRgxGenCaseInsensitiveTest(TestPattern testPattern) {
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        RgxGen rgxGen = RgxGen.parse(properties, testPattern.getPattern());

        for (int i = 0; i < 100; i++) {
            RandomGenerator random = TestingUtilities.newRandom(i);
            for (int j = 0; j < 10; j++) {
                String generated = rgxGen.generate(random);
                boolean result = isValidGenerated(testPattern, generated, Pattern.CASE_INSENSITIVE);
                assertTrue(result, createMessage(generated, testPattern, i, j));
            }
        }
    }
}
