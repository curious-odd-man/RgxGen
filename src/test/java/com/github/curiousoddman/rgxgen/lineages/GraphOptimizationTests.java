package com.github.curiousoddman.rgxgen.lineages;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.data.DollarAndCaretPatterns;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphOptimizationTests {
    public static Stream<DollarAndCaretPatterns> getPatterns() {
        return Arrays.stream(DollarAndCaretPatterns.values());
    }

    @ParameterizedTest
    @MethodSource("getPatterns")
    public void parseTest(DollarAndCaretPatterns testPattern) throws IOException {
        RgxGen parse = RgxGen.parse(testPattern.getPattern());
        String pathGraph = parse.getPathGraph().toPlantUml();

        try {
            assertEquals(
                    testPattern.getOptimizedGraph(),
                    pathGraph
            );
        } catch (AssertionFailedError e) {
            Path expectedFilePath = testPattern.getExpectedFilePath();
            Files.writeString(expectedFilePath, pathGraph);
            throw e;
        }

        Spliterator<String> tSpliterator = Spliterators.spliteratorUnknownSize(parse.iterateUnique(), 0);
        List<String> list = StreamSupport.stream(tSpliterator, false).toList();

        assertEquals(
                testPattern.getAllUniqueValues(),
                list
        );
    }
}
