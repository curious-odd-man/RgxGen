package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.generator.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueGenerationVisitor;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class LimitedInfinitePatternsTests {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "a*", // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Repeat("a*", new FinalSymbol("a"), 0, 10),
                        IntStream.iterate(0, value -> value + 1)
                                 .limit(11)
                                 .mapToObj(v -> Stream.generate(() -> "a")
                                                      .limit(v)
                                                      .reduce("", String::concat)).collect(Collectors.toList())
                },
                {
                        "aa+", // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Sequence("aa+", new FinalSymbol("a"), new Repeat("a+", new FinalSymbol("a"), 1, 10)),
                        IntStream.iterate(1, value -> value + 1)
                                 .limit(10)
                                 .mapToObj(v -> 'a' + Stream.generate(() -> "a")
                                                            .limit(v)
                                                            .reduce("", String::concat))
                                .collect(Collectors.toList())
                },
                {
                        "a.*",      // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Sequence("a.*", new FinalSymbol("a"), new Repeat(".*", new SymbolSet(), 0, 2)),
                        Stream.concat(Stream.of(""), Stream.concat(Arrays.stream(SymbolSet.getAllSymbols()),
                                                                   Arrays.stream(SymbolSet.getAllSymbols())
                                                                         .flatMap(symbol -> Arrays.stream(SymbolSet.getAllSymbols())
                                                                                                  .map(v -> symbol + v))))
                              .map(v -> 'a' + v)
                                .collect(Collectors.toList())
                }
        });
    }

    @Parameterized.Parameter
    public String       aRegex;
    @Parameterized.Parameter(1)
    public Node         aNode;
    @Parameterized.Parameter(2)
    public List<String> aExpectedUnique;

    @Test
    public void generateTest() {
        Pattern p = Pattern.compile(aRegex);

        for (int i = 0; i < 100; i++) {
            GenerationVisitor generationVisitor = GenerationVisitor.builder()
                                                                   .get();
            aNode.visit(generationVisitor);
            assertTrue(p.matcher(generationVisitor.getString())
                        .matches());
        }
    }

    @Test
    public void generateUniqueTest() {
        UniqueGenerationVisitor v = new UniqueGenerationVisitor();
        aNode.visit(v);
        assertEquals(aExpectedUnique, TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }
}
