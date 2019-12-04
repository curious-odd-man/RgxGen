package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueValuesCountingVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class CountTests {
    private static final int ITERATIONS = 100;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "a",
                        new FinalSymbol("a"),
                        BigInteger.ONE
                },
                {
                        "[ab]",
                        new Choice(new FinalSymbol("a"), new FinalSymbol("b")),
                        BigInteger.valueOf(2)
                },
                {
                        "[ab]c",
                        new Choice(new FinalSymbol("a"), new FinalSymbol("b")),
                        BigInteger.valueOf(2)
                },
                {
                        "d[ab]c",
                        new Choice(new FinalSymbol("a"), new FinalSymbol("b")),
                        BigInteger.valueOf(2)
                },
                {
                        "a{2,5}",
                        new Repeat(new FinalSymbol("a"), 2, 5),
                        BigInteger.valueOf(4)
                },
                {
                        "a{2}",
                        new Repeat(new FinalSymbol("a"), 2),
                        BigInteger.ONE
                },
                {
                        "a{60000}",
                        new Repeat(new FinalSymbol("a"), 60000),
                        BigInteger.ONE
                },
                {
                        "(a|b){2}",
                        new Repeat(new Choice(new FinalSymbol("a"), new FinalSymbol("b")), 2),
                        BigInteger.valueOf(4)
                },
                {
                        "(a|b){0,2}",
                        new Repeat(new Choice(new FinalSymbol("a"), new FinalSymbol("b")), 0, 2),
                        BigInteger.valueOf(7)
                },
                {
                        "(a{0,2}|b{0,2})",
                        new Choice(new Repeat(new FinalSymbol("a"), 0, 2), new Repeat(new FinalSymbol("b"), 0, 2)),
                        BigInteger.valueOf(6)       // Actual is 5! but left and right side can yield same values - that can't be analysed by counting
                },
                {
                        "(|(a{1,2}|b{1,2}))",
                        new Choice(new FinalSymbol(""), new Choice(new Repeat(new FinalSymbol("a"), 1, 2), new Repeat(new FinalSymbol("b"), 1, 2))),
                        BigInteger.valueOf(5)
                },
                {
                        "a.",
                        new Sequence(new FinalSymbol("a"), new SymbolSet()),
                        BigInteger.valueOf(95)
                },
                {
                        "..",
                        new Sequence(new SymbolSet(), new SymbolSet()),
                        BigInteger.valueOf(95 * 95)
                },
                {
                        "a*",
                        Repeat.minimum(new FinalSymbol("a"), 0),
                        null
                },
                {
                        "aa?",
                        new Sequence(new FinalSymbol("a"), new Repeat(new FinalSymbol("a"), 0, 1)),
                        BigInteger.valueOf(2)
                },
                {
                        "aa+",
                        new Sequence(new FinalSymbol("a"), Repeat.minimum(new FinalSymbol("a"), 1)),
                        null
                },
                {
                        "a.*",      // If use unlimited repeteation that will cause an error when trying to save all data in memory, thus we limit repetition times
                        new Sequence(new FinalSymbol("a"), Repeat.minimum(new SymbolSet(), 0)),
                        null
                }
        });
    }

    @Parameterized.Parameter
    public String     aRegex;
    @Parameterized.Parameter(1)
    public Node       aNode;
    @Parameterized.Parameter(2)
    public BigInteger aExpectedUnique;

    @Test
    public void countTest() {
        UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor();
        aNode.visit(v);
        if (aRegex.contains("*") || aRegex.contains("+")) {
            assertNull(v.getCount());
        } else {
            assertEquals(aExpectedUnique, v.getCount());
        }
    }
}
