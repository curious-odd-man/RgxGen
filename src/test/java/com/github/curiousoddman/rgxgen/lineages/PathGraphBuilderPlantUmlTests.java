package com.github.curiousoddman.rgxgen.lineages;

import com.github.curiousoddman.rgxgen.CombinedTestTemplate;
import com.github.curiousoddman.rgxgen.data.TestPattern;
import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultNodeCreator;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class PathGraphBuilderPlantUmlTests extends CombinedTestTemplate<TestPattern> {

    private static final Path FILES_ROOT = Paths.get("testdata/path-graph-plantuml");

    public static Stream<TestPattern> getPatterns() {
        return Arrays.stream(TestPattern.values());
    }

    @ParameterizedTest
    @MethodSource("getPatterns")
    public void parseTest(TestPattern testPattern) throws IOException {
        NodeTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(testPattern.getPattern(), new DefaultNodeCreator(), null);
        Node node = defaultTreeBuilder.get();

        Path targetFile = getTargetFilePath(testPattern);

        PathGraph pathGraph = PathGraphBuilder.build(node);
        String plantUml = pathGraph.toPlantUml();

        if (Files.exists(targetFile)) {
            String expected = Files.readString(targetFile);
            if (!expected.equals(plantUml)) {
                Files.writeString(targetFile, plantUml);
                fail("Pattern " + testPattern.name() + " plantuml diagram is wrong");
            }
        } else {
            Files.writeString(targetFile, plantUml);
            fail("Pattern " + testPattern.name() + " no expected file - please review!");
        }
    }

    private Path getTargetFilePath(TestPattern testPattern) {
        return FILES_ROOT.resolve(testPattern.name() + ".puml");
    }
}
