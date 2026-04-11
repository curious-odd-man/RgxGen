package com.github.curiousoddman.rgxgen.config;

import com.github.curiousoddman.rgxgen.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.nodes.Repeat;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import com.github.curiousoddman.rgxgen.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.visitors.UniqueGenerationVisitor;
import joptsimple.internal.Strings;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;


public class LimitedInfinitePatternsTests {
    public static Stream<Arguments> getTestData() {
        return Stream.of(
                arguments(
                        "a*",
                        new Repeat("a*", new FinalSymbol("a"), 0, -1),
                        IntStream.iterate(0, value -> value + 1)
                                .limit(11)
                                .mapToObj(v -> Strings.repeat('a', v))
                                .collect(Collectors.toList())
                ),
                arguments(
                        "a+",
                        new Repeat("a+", new FinalSymbol("a"), 1, -1),
                        IntStream.iterate(1, value -> value + 1)
                                .limit(10)
                                .mapToObj(v -> Strings.repeat('a', v))
                                .collect(Collectors.toList())
                ),
                arguments(
                        "x*",
                        new Repeat("x*", new FinalSymbol("x"), 0, -1),
                        IntStream.iterate(0, value -> value + 1)
                                .limit(11)
                                .mapToObj(v -> Strings.repeat('x', v))
                                .collect(Collectors.toList())
                ),
                arguments(
                        "x{1,}",
                        new Repeat("x{1,}", new FinalSymbol("x"), 1, -1),
                        IntStream.iterate(1, value -> value + 1)
                                .limit(10)
                                .mapToObj(v -> Strings.repeat('x', v))
                                .collect(Collectors.toList())
                ),
                arguments(
                        "x{1,3}",
                        new Repeat("x{1,}", new FinalSymbol("x"), 1, 3),
                        IntStream.iterate(1, value -> value + 1)
                                .limit(3)
                                .mapToObj(v -> Strings.repeat('x', v))
                                .collect(Collectors.toList())
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void generateTest(String pattern, Node node, List<String> expectedUnique) {
        Pattern p = Pattern.compile(pattern);

        for (int i = 0; i < 100; i++) {
            GenerationVisitor generationVisitor = GenerationVisitor.builder()
                    .get();
            node.visit(generationVisitor);
            assertTrue(p.matcher(generationVisitor.getString())
                    .matches());
        }
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void generateUniqueTest(String pattern, Node node, List<String> expectedUnique) {
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(properties, 10);
        UniqueGenerationVisitor v = new UniqueGenerationVisitor(properties);
        node.visit(v);
        assertEquals(expectedUnique, TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void generateUniqueMinLengthTest(String pattern, Node node, List<String> expectedUnique) {
        RgxGenProperties properties = new RgxGenProperties();
        int minLength = 2;
        RgxGenOption.INFINITE_PATTERN_MINIMUM_REPETITION.setInProperties(properties, minLength);
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(properties, 10);

        var expectedFiltered = expectedUnique
                .stream()
                .filter(v -> v.length() >= minLength)
                .toList();

        UniqueGenerationVisitor v = new UniqueGenerationVisitor(properties);
        node.visit(v);
        assertEquals(expectedFiltered, TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }
}
