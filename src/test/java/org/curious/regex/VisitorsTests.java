package org.curious.regex;

import org.curious.regex.generator.nodes.*;
import org.curious.regex.generator.visitors.GenerationVisitor;
import org.curious.regex.generator.visitors.UniqueGenerationVisitor;
import org.curious.regex.generator.visitors.UniqueValuesCountingVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class VisitorsTests {
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
                        Arrays.asList("", "a", "aa", "b", "bb")
                },
                {
                        "a.",
                        new Sequence(new FinalSymbol("a"), new AnySymbol()),
                        Arrays.stream(AnySymbol.ALL_SYMBOLS)
                              .map(s -> "a" + s).collect(Collectors.toList())
                },
                {
                        "..",
                        new Sequence(new AnySymbol(), new AnySymbol()),
                        Arrays.stream(AnySymbol.ALL_SYMBOLS)
                              .flatMap(s -> Arrays.stream(AnySymbol.ALL_SYMBOLS)
                                                  .map(v -> v + s)).collect(Collectors.toList())
                },
                {
                        "a*",
                        Repeat.minimum(new FinalSymbol("a"), 0),
                        IntStream.iterate(0, value -> value + 1)
                                 .limit(Repeat.getMaxRepeat() + 1)
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
                        "aa+",
                        new Sequence(new FinalSymbol("a"), Repeat.minimum(new FinalSymbol("a"), 1)),
                        IntStream.iterate(1, value -> value + 1)
                                 .limit(Repeat.getMaxRepeat())
                                 .mapToObj(v -> "a" + Stream.generate(() -> "a")
                                                            .limit(v)
                                                            .reduce("", String::concat))
                                .collect(Collectors.toList())
                },
                {
                        "a.*",      // FIXME: This fails to generate unique values with OutOfMemoryError
                        new Sequence(new FinalSymbol("a"), Repeat.minimum(new AnySymbol(), 0)),
                        IntStream.iterate(0, value -> value + 1)
                                 .limit(1)
                                 .mapToObj(v -> Arrays.stream(AnySymbol.ALL_SYMBOLS)
                                                      .parallel()
                                                      .map(symbol -> Stream.generate(() -> symbol)
                                                                           .limit(v)
                                                                           .reduce("", String::concat)))
                                 .flatMap(Function.identity())
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
    public void countTest() {
        UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor();
        aNode.visit(v);
        assertEquals(aExpectedUnique.size(), v.getCount()
                                              .intValueExact());
    }

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
        long maxRepeat = Repeat.getMaxRepeat();
        // Otherwise test is taking too long
        Repeat.setMaxRepeat(1);
        UniqueGenerationVisitor v = new UniqueGenerationVisitor();
        aNode.visit(v);
        Collections.sort(aExpectedUnique);
        List<String> actual = v.getUniqueStrings()
                               .sorted()
                               .collect(Collectors.toList());
        Repeat.setMaxRepeat(maxRepeat);
        assertEquals(aExpectedUnique, actual);
    }
}
