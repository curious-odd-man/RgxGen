package org.curious.regex;

import org.curious.regex.generator.nodes.*;
import org.curious.regex.parsing.DefaultTreeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

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
                }
        });
    }

    @Parameterized.Parameter
    public String aRegex;
    @Parameterized.Parameter(1)
    public Node   aExpected;

    @Test
    public void parseTest() {
        Node node = new DefaultTreeBuilder(aRegex).get();
        assertEquals(aExpected.toString(), node.toString());
    }
}
