package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
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
                        new Choice(new FinalSymbol("a"), new FinalSymbol("b"))
                },
                {
                        "[0-9]",
                        new Choice(IntStream.rangeClosed(0, 9)
                                            .mapToObj(Integer::toString)
                                            .map(FinalSymbol::new)
                                            .toArray(FinalSymbol[]::new))
                },
                {
                        "[a-cA-C]",
                        new Choice(new FinalSymbol("a"), new FinalSymbol("b"), new FinalSymbol("c"), new FinalSymbol("A"), new FinalSymbol("B"), new FinalSymbol("C"))
                },
                {
                        "\\d",
                        new Choice(IntStream.rangeClosed(0, 9)
                                            .mapToObj(Integer::toString)
                                            .map(FinalSymbol::new)
                                            .toArray(FinalSymbol[]::new))
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
                        new Sequence(new FinalSymbol("a"), new AnySymbol())
                },
                {
                        "..",
                        new Sequence(new AnySymbol(), new AnySymbol())
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
                        new Sequence(new FinalSymbol("a"), Repeat.minimum(new AnySymbol(), 0))
                },
                {
                        "(25[01]|2[01])",
                        new Choice(new Sequence(new FinalSymbol("25"), new Choice(new FinalSymbol("0"), new FinalSymbol("1"))),
                                   new Sequence(new FinalSymbol("2"), new Choice(new FinalSymbol("0"), new FinalSymbol("1"))))
                },
                {
                        "a{4,}",
                        Repeat.minimum(new FinalSymbol("a"), 4)
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
