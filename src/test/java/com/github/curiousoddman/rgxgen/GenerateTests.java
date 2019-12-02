package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.generator.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueGenerationVisitor;
import com.github.curiousoddman.rgxgen.util.Util;
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
public class GenerateTests {
    private static final int ITERATIONS = 100;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "a",
                        new FinalSymbol("a"),
                        Arrays.asList("a")
                },
                {
                        "\\d",
                        new Choice(IntStream.rangeClosed(0, 9)
                                            .mapToObj(Integer::toString)
                                            .map(FinalSymbol::new)
                                            .toArray(FinalSymbol[]::new)),
                        Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
                },
                {
                        "[ab]",
                        new Choice(new FinalSymbol("a"), new FinalSymbol("b")),
                        Arrays.asList("a", "b")
                },
                {
                        "a{2,5}",
                        new Repeat(new FinalSymbol("a"), 2, 5),
                        Arrays.asList("aa", "aaa", "aaaa", "aaaaa")
                },
                {
                        "a{2}",
                        new Repeat(new FinalSymbol("a"), 2),
                        Arrays.asList("aa")
                },
                {
                        "a{60000}",
                        new Repeat(new FinalSymbol("a"), 60000),
                        Arrays.asList(Stream.generate(() -> "a")
                                            .limit(60000)
                                            .reduce("", String::concat))
                },
                {
                        "(a|b){2}",
                        new Repeat(new Choice(new FinalSymbol("a"), new FinalSymbol("b")), 2),
                        Arrays.asList("aa", "ab", "ba", "bb")
                },
                {
                        "(a|b){0,2}",
                        new Repeat(new Choice(new FinalSymbol("a"), new FinalSymbol("b")), 0, 2),
                        Arrays.asList("", "a", "b", "aa", "ab", "ba", "bb")
                },
                {
                        "(a{0,2}|b{0,2})",
                        new Choice(new Repeat(new FinalSymbol("a"), 0, 2), new Repeat(new FinalSymbol("b"), 0, 2)),
                        Arrays.asList("", "a", "aa", "", "b", "bb")
                },
                {
                        "(|(a{1,2}|b{1,2}))",
                        new Choice(new FinalSymbol(""), new Choice(new Repeat(new FinalSymbol("a"), 1, 2), new Repeat(new FinalSymbol("b"), 1, 2))),
                        Arrays.asList("", "a", "aa", "b", "bb")
                },
                {
                        "a.",
                        new Sequence(new FinalSymbol("a"), new SymbolRange()),
                        Arrays.stream(SymbolRange.ALL_SYMBOLS)
                              .map(s -> "a" + s).collect(Collectors.toList())
                },
                {
                        "..",
                        new Sequence(new SymbolRange(), new SymbolRange()),
                        Arrays.stream(SymbolRange.ALL_SYMBOLS)
                              .flatMap(s -> Arrays.stream(SymbolRange.ALL_SYMBOLS)
                                                  .map(v -> s + v)).collect(Collectors.toList())
                },
                {
                        "a*", // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Repeat(new FinalSymbol("a"), 0, 10),
                        IntStream.iterate(0, value -> value + 1)
                                 .limit(11)
                                 .mapToObj(v -> Stream.generate(() -> "a")
                                                      .limit(v)
                                                      .reduce("", String::concat)).collect(Collectors.toList())
                },
                {
                        "aa?",
                        new Sequence(new FinalSymbol("a"), new Repeat(new FinalSymbol("a"), 0, 1)),
                        Arrays.asList("a", "aa")
                },
                {
                        "aa+", // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Sequence(new FinalSymbol("a"), new Repeat(new FinalSymbol("a"), 1, 10)),
                        IntStream.iterate(1, value -> value + 1)
                                 .limit(10)
                                 .mapToObj(v -> "a" + Stream.generate(() -> "a")
                                                            .limit(v)
                                                            .reduce("", String::concat))
                                .collect(Collectors.toList())
                },
                {
                        "a.*",      // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Sequence(new FinalSymbol("a"), new Repeat(new SymbolRange(), 0, 2)),
                        Stream.concat(Stream.of(""), Stream.concat(Arrays.stream(SymbolRange.ALL_SYMBOLS),
                                                                   Arrays.stream(SymbolRange.ALL_SYMBOLS)
                                                                         .flatMap(symbol -> Arrays.stream(SymbolRange.ALL_SYMBOLS)
                                                                                                  .map(v -> symbol + v))))
                              .map(v -> "a" + v)
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
            GenerationVisitor generationVisitor = new GenerationVisitor();
            aNode.visit(generationVisitor);
            assertTrue(p.matcher(generationVisitor.getString())
                        .matches());
        }
    }

    @Test
    public void generateUniqueTest() {
        UniqueGenerationVisitor v = new UniqueGenerationVisitor();
        aNode.visit(v);
        assertEquals(aExpectedUnique, Util.iteratorToList(v.getUniqueStrings()));
    }
}
