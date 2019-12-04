package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParsingTests {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "a",
                        new FinalSymbol("a")
                },
                {
                        "[ab]",
                        new SymbolSet(new String[]{"a", "b"}, true)
                },
                {
                        "[0-9]",
                        new SymbolSet(Collections.singletonList(new SymbolSet.SymbolRange('0', '9')), true)
                },
                {
                        "[a-cA-C]",
                        new SymbolSet(Arrays.asList(new SymbolSet.SymbolRange('a', 'c'), new SymbolSet.SymbolRange('A', 'C')), true)
                },
                {
                        "a{2,5}",
                        new Repeat(new FinalSymbol("a"), 2, 5)
                },
                {
                        "a{2}",
                        new Repeat(new FinalSymbol("a"), 2)
                },
                {
                        "a{60000}",
                        new Repeat(new FinalSymbol("a"), 60000)
                },
                {
                        "(a|b){2}",
                        new Repeat(new Choice(new FinalSymbol("a"), new FinalSymbol("b")), 2)
                },
                {
                        "(a|b){0,2}",
                        new Repeat(new Choice(new FinalSymbol("a"), new FinalSymbol("b")), 0, 2)
                },
                {
                        "(a{0,2}|b{0,2})",
                        new Choice(new Repeat(new FinalSymbol("a"), 0, 2), new Repeat(new FinalSymbol("b"), 0, 2))
                },
                {
                        "a.",
                        new Sequence(new FinalSymbol("a"), new SymbolSet())
                },
                {
                        "..",
                        new Sequence(new SymbolSet(), new SymbolSet())
                },
                {
                        "a*",
                        Repeat.minimum(new FinalSymbol("a"), 0)
                },
                {
                        "aa?",
                        new Sequence(new FinalSymbol("a"), new Repeat(new FinalSymbol("a"), 0, 1))
                },
                {
                        "aa+",
                        new Sequence(new FinalSymbol("a"), Repeat.minimum(new FinalSymbol("a"), 1))
                },
                {
                        "a.*",
                        new Sequence(new FinalSymbol("a"), Repeat.minimum(new SymbolSet(), 0))
                },
                {
                        "(25[01]|2[01])",
                        new Choice(new Sequence(new FinalSymbol("25"), new SymbolSet(new String[]{"0", "1"}, true)),
                                   new Sequence(new FinalSymbol("2"), new SymbolSet(new String[]{"0", "1"}, true)))
                },
                {
                        "a{4,}",
                        Repeat.minimum(new FinalSymbol("a"), 4)
                },
                {
                        "[^a]",
                        new SymbolSet(Arrays.stream(SymbolSet.getAllSymbols())
                                            .filter(s -> !s.equals("a"))
                                            .toArray(String[]::new), true)
                },
                {
                        "[^a-dE-F]",
                        new SymbolSet(Arrays.stream(SymbolSet.getAllSymbols())
                                            .filter(s -> !(s.equals("a") || s.equals("b") || s.equals("c") || s.equals("d") || s.equals("E") || s.equals("F")))
                                            .toArray(String[]::new), true)
                },
                {
                        "\\s",      // Any White Space
                        new SymbolSet(new String[]{" ", "\t", "\n"}, true)
                },
                {
                        "\\S",      // Any Non White Space
                        new SymbolSet(new String[]{" ", "\t", "\n"}, false)
                },
                {
                        "\\d",      // Any digit
                        new SymbolSet(IntStream.rangeClosed(0, 9)
                                               .mapToObj(Integer::toString)
                                               .toArray(String[]::new), true)
                },
                {
                        "\\D",      // Any non-digit
                        new SymbolSet(IntStream.rangeClosed(0, 9)
                                               .mapToObj(Integer::toString)
                                               .toArray(String[]::new), false)
                },
                {
                        "\\w",      // Any word character  [a-zA-Z0-9_]
                        new SymbolSet(Arrays.asList(new SymbolSet.SymbolRange('a', 'z'), new SymbolSet.SymbolRange('A', 'Z'), new SymbolSet.SymbolRange('0', '9')), new String[]{"_"}, true)
                },
                {
                        "\\W",      // Any non-word symbol  [a-zA-Z0-9_]
                        new SymbolSet(Arrays.asList(new SymbolSet.SymbolRange('a', 'z'), new SymbolSet.SymbolRange('A', 'Z'), new SymbolSet.SymbolRange('0', '9')), new String[]{"_"}, false)
                },
                {
                        "\\x20", // Space
                        new FinalSymbol(" ")
                },
                {
                        "\\x{26F8}",
                        new FinalSymbol("⛸")
                },
                {
                        "\\x20a", // Space
                        new FinalSymbol(" a")
                },
                {
                        "\\x{26F8}a",
                        new FinalSymbol("⛸a")
                }
        });
    }

    @Parameterized.Parameter
    public String aRegex;
    @Parameterized.Parameter(1)
    public Node   aExpected;

    @Test
    public void parseTest() {
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(aRegex);
        Node node = defaultTreeBuilder.get();
        assertEquals(aExpected.toString(), node.toString());
    }
}
