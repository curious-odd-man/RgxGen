package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import com.github.curiousoddman.rgxgen.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.visitors.UniqueGenerationVisitor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.makeAsciiCharacterArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;


public class LimitedInfinitePatternsTests {
    public static Stream<Arguments> getTestData() {
        return Stream.of(
                arguments(
                        "a*", // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Repeat("a*", new FinalSymbol("a"), 0, 10),
                        IntStream.iterate(0, value -> value + 1)
                                 .limit(11)
                                 .mapToObj(v -> Stream.generate(() -> "a")
                                                      .limit(v)
                                                      .reduce("", String::concat))
                                 .collect(Collectors.toList())
                ),
                arguments(
                        "aa+", // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Sequence("aa+", new FinalSymbol("a"), new Repeat("a+", new FinalSymbol("a"), 1, 10)),
                        IntStream.iterate(1, value -> value + 1)
                                 .limit(10)
                                 .mapToObj(v -> 'a' + Stream.generate(() -> "a")
                                                            .limit(v)
                                                            .reduce("", String::concat))
                                 .collect(Collectors.toList())
                ),
                arguments(
                        "a.*",      // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Sequence("a.*", new FinalSymbol("a"), new Repeat(".*", SymbolSet.ofAsciiDotPattern(), 0, 2)),
                        Stream.concat(Stream.of(""), Stream.concat(Arrays.stream(makeAsciiCharacterArray()),
                                                                   Arrays.stream(makeAsciiCharacterArray())
                                                                         .flatMap(symbol -> Arrays.stream(makeAsciiCharacterArray())
                                                                                                  .map(v -> String.valueOf(symbol) + v))))
                              .map(v -> "" + 'a' + v)
                              .collect(Collectors.toList())
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void generateTest(String aRegex, Node aNode, List<String> aExpectedUnique) {
        Pattern p = Pattern.compile(aRegex);

        for (int i = 0; i < 100; i++) {
            GenerationVisitor generationVisitor = GenerationVisitor.builder()
                                                                   .get();
            aNode.visit(generationVisitor);
            assertTrue(p.matcher(generationVisitor.getString())
                        .matches());
        }
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void generateUniqueTest(String aRegex, Node aNode, List<String> aExpectedUnique) {
        UniqueGenerationVisitor v = new UniqueGenerationVisitor(new RgxGenProperties());
        aNode.visit(v);
        assertEquals(aExpectedUnique, TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }
}
