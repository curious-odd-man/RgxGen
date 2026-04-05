package com.github.curiousoddman.rgxgen.lineages;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.data.DollarAndCaretPatterns;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

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
        String pathGraph = parse.getPathGraph().toPlantUml();

        testPattern.assertFileContents(pathGraph);

        Spliterator<String> tSpliterator = Spliterators.spliteratorUnknownSize(parse.iterateUnique(), 0);
        List<String> list = StreamSupport.stream(tSpliterator, false).toList();

        assertEquals(
                testPattern.getUniqueValues(),
                list
        );
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
