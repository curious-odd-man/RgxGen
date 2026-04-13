package com.github.curiousoddman.rgxgen.lineages;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.data.DollarAndCaretPatterns;
import com.github.curiousoddman.rgxgen.lineages.optimization.GraphOptimizer;
import com.github.curiousoddman.rgxgen.lineages.optimization.NodePosition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static com.github.curiousoddman.rgxgen.lineages.optimization.GraphOptimizerTest.astNodes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GraphOptimizationTests {
    @ParameterizedTest
    @EnumSource(DollarAndCaretPatterns.MatchingNothing.class)
    public void matchesNothingTests(DollarAndCaretPatterns.MatchingNothing testPattern) {
        try {
            RgxGen.parse(testPattern.getPattern());
            fail("Expected to fail with exception");
        } catch (PatternMatchesNothingException e) {
            // Success
        }
    }

    @ParameterizedTest
    @EnumSource(DollarAndCaretPatterns.EdgeCasesPotentialTrouble.class)
    public void edgeCasesTests(DollarAndCaretPatterns.EdgeCasesPotentialTrouble testPattern) throws IOException {
        RgxGen parse = RgxGen.parse(testPattern.getPattern());
        String pathGraph = parse.getPathGraph().toPlantUml();

        testPattern.assertFileContents(pathGraph);
    }

    @ParameterizedTest
    @EnumSource(DollarAndCaretPatterns.Optimizable.class)
    public void optimizablePatternsTests(DollarAndCaretPatterns.Optimizable testPattern) throws IOException {
        RgxGen parse = RgxGen.parse(testPattern.getPattern());
        PathGraph suboptimalGraph = parse.getPathGraph();
        String suboptimalGraphPlantUml = suboptimalGraph.toPlantUml();

        PathGraph markedNodesGraph = GraphOptimizer.markNodesPositions(suboptimalGraph);
        List<PathNode> astPathNodes = astNodes(markedNodesGraph);

        StringBuilder sb = new StringBuilder("# Pattern: ").append(testPattern.getPattern()).append('\n');
        for (PathNode node : astPathNodes) {
            String pattern = node.getAstNode().getPattern();
            NodePosition.First firstPosition = node.getFirstPosition();
            NodePosition.Last lastPosition = node.getLastPosition();
            sb.append("- pattern: ").append('"').append(pattern).append('"').append('\n')
                    .append("  first: ").append(firstPosition).append('\n')
                    .append("  last: ").append(lastPosition).append('\n');
        }

        testPattern.assertFileContents(sb.toString(), "-node-marks.yml");
        testPattern.assertFileContents(suboptimalGraphPlantUml);

        Spliterator<String> tSpliterator = Spliterators.spliteratorUnknownSize(parse.iterateUnique(), 0);
        List<String> list = StreamSupport.stream(tSpliterator, false).toList();

        assertEquals(
                testPattern.getUniqueValues(),
                list
        );
    }

    @ParameterizedTest
    @EnumSource(DollarAndCaretPatterns.OptimizableCasesMarks.class)
    public void extendedOptimizablePatternsTests(DollarAndCaretPatterns.OptimizableCasesMarks testPattern) throws IOException {
        RgxGen parse = RgxGen.parse(testPattern.getPattern());
        PathGraph suboptimalGraph = parse.getPathGraph();

        PathGraph markedNodesGraph = GraphOptimizer.markNodesPositions(suboptimalGraph);
        List<PathNode> astPathNodes = astNodes(markedNodesGraph);

        StringBuilder sb = new StringBuilder("# Pattern: ").append(testPattern.getPattern()).append('\n');
        for (PathNode node : astPathNodes) {
            String pattern = node.getAstNode().getPattern();
            NodePosition.First firstPosition = node.getFirstPosition();
            NodePosition.Last lastPosition = node.getLastPosition();
            sb.append("- pattern: ").append('"').append(pattern).append('"').append('\n')
                    .append("  first: ").append(firstPosition).append('\n')
                    .append("  last: ").append(lastPosition).append('\n');
        }

        testPattern.assertFileContents(sb.toString());
    }

    @ParameterizedTest
    @EnumSource(DollarAndCaretPatterns.Optimal.class)
    public void optimalPatternsTests(DollarAndCaretPatterns.Optimal testPattern) throws IOException {
        RgxGen parse = RgxGen.parse(testPattern.getPattern());
        String pathGraph = parse.getPathGraph().toPlantUml();

        testPattern.assertFileContents(pathGraph);

        Spliterator<String> tSpliterator = Spliterators.spliteratorUnknownSize(parse.iterateUnique(), 0);
        List<String> list = StreamSupport.stream(tSpliterator, false).toList();

        assertEquals(
                testPattern.getAllUniqueValues(),
                list
        );
    }
}
