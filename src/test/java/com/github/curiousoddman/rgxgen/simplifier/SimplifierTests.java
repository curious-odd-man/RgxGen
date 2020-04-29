package com.github.curiousoddman.rgxgen.simplifier;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class SimplifierTests {
    private static final LineStart   START    = new LineStart();
    private static final LineEnd     END      = new LineEnd();
    private static final FinalSymbol X_LETTER = new FinalSymbol("x");
    private static final FinalSymbol A_LETTER = new FinalSymbol("a");
    private static final FinalSymbol C_LETTER = new FinalSymbol("c");

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"^", START, new PatternDoesntMatchAnyStringException("")},
                {"$", END, new PatternDoesntMatchAnyStringException("")},
                {"^x", new Sequence(START, X_LETTER), X_LETTER},
                {"x$", new Sequence(X_LETTER, END), X_LETTER},
                {"x^", new Sequence(X_LETTER, START), new PatternDoesntMatchAnyStringException("")},
                {"$x", new Sequence(END, X_LETTER), new PatternDoesntMatchAnyStringException("")},
                {"(x|a^)", new Group(1, new Choice(X_LETTER, new Sequence(A_LETTER, START))), X_LETTER},
                {"(&x|a)", new Group(1, new Choice(new Sequence(END, X_LETTER))), A_LETTER, A_LETTER},
                {"(x|^a)", new Group(1,
                                     new Choice(X_LETTER, new Sequence(START, A_LETTER))
                ), new Group(1, new Choice(X_LETTER, A_LETTER))},
                {"c(x|^a)", new Sequence(C_LETTER,
                                         new Group(1,
                                                   new Choice(X_LETTER, new Sequence(START, A_LETTER))
                                         )
                ), new FinalSymbol("cx")},
                {"(x&|a)c", new Sequence(
                        new Group(1,
                                  new Choice(new Sequence(X_LETTER, END), A_LETTER)
                        ), C_LETTER
                ), new FinalSymbol("ac")},
                {"(x&|a)",
                 new Group(1,
                           new Choice(new Sequence(X_LETTER, END), A_LETTER)
                 ), new Group(1,
                              new Choice(X_LETTER, A_LETTER)
                )}
        });
    }

    private Simplifier aSimplifier;

    private final String    aName;
    private final Node      aInputNode;
    private final Node      aExpectedNode;
    private final Exception aExpectedException;

    public SimplifierTests(String name, Node input, Object expected) {
        aName = name;
        aInputNode = input;
        if (expected instanceof Exception) {
            aExpectedException = (Exception) expected;
            aExpectedNode = null;
        } else {
            aExpectedException = null;
            aExpectedNode = (Node) expected;
        }

    }

    @Before
    public void setUp() {
        aSimplifier = new DefaultSimplifier();
    }

    @Test
    public void test() {
        try {
            Node simpleNode = aSimplifier.simplify(aInputNode);
            if (aExpectedException != null) {
                fail("Expected exception " + aExpectedException.getClass()
                                                               .getName());
            }
            assertEquals(aExpectedNode.toString(), simpleNode.toString());
        } catch (Exception e) {
            assertEquals(aExpectedException, e);
        }
    }
}
