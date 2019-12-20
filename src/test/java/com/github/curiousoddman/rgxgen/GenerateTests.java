package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.generator.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueGenerationVisitor;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
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
                        new SymbolSet(IntStream.rangeClosed(0, 9)
                                               .mapToObj(Integer::toString)
                                               .toArray(String[]::new), true),
                        Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
                },
                {
                        "[ab]",
                        new SymbolSet(new String[]{"a", "b"}, true),
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
                        new Repeat(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))), 2),
                        Arrays.asList("aa", "ab", "ba", "bb")
                },
                {
                        "(a|b){0,2}",
                        new Repeat(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))), 0, 2),
                        Arrays.asList("", "a", "b", "aa", "ab", "ba", "bb")
                },
                {
                        "(a{0,2}|b{0,2})",
                        new Group(1, new Choice(new Repeat(new FinalSymbol("a"), 0, 2), new Repeat(new FinalSymbol("b"), 0, 2))),
                        Arrays.asList("", "a", "aa", "", "b", "bb")
                },
                {
                        "(|(a{1,2}|b{1,2}))",
                        new Group(1, new Choice(new FinalSymbol(""), new Group(2, new Choice(new Repeat(new FinalSymbol("a"), 1, 2), new Repeat(new FinalSymbol("b"), 1, 2))))),
                        Arrays.asList("", "a", "aa", "b", "bb")
                },
                {
                        "a.",
                        new Sequence(new FinalSymbol("a"), new SymbolSet()),
                        Arrays.stream(SymbolSet.getAllSymbols())
                              .map(s -> "a" + s).collect(Collectors.toList())
                },
                {
                        "..",
                        new Sequence(new SymbolSet(), new SymbolSet()),
                        Arrays.stream(SymbolSet.getAllSymbols())
                              .flatMap(s -> Arrays.stream(SymbolSet.getAllSymbols())
                                                  .map(v -> s + v)).collect(Collectors.toList())
                },
                {
                        "aa?",
                        new Sequence(new FinalSymbol("a"), new Repeat(new FinalSymbol("a"), 0, 1)),
                        Arrays.asList("a", "aa")
                },
                {
                        "<([abc])>d<\\/\\1>",
                        new Sequence(new FinalSymbol("<"),
                                     new Group(1, new SymbolSet(new String[]{"a", "b", "c"}, true)),
                                     new FinalSymbol(">d</"),
                                     new GroupRef(1),
                                     new FinalSymbol(">")
                        ),
                        Arrays.asList("<a>d</a>", "<b>d</b>", "<c>d</c>")
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
    public void parseTest() {
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(aRegex);
        Node node = defaultTreeBuilder.get();
        assertEquals(aNode.toString(), node.toString());
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
        UniqueGenerationVisitor v = new UniqueGenerationVisitor();
        aNode.visit(v);
        assertEquals(aExpectedUnique, TestingUtilities.iteratorToList(v.getUniqueStrings()));
    }
}
