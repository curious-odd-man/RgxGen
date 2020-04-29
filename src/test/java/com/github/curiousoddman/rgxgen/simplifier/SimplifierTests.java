package com.github.curiousoddman.rgxgen.simplifier;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SimplifierTests {
    private static final LineStart   START    = new LineStart();
    private static final LineEnd     END      = new LineEnd();
    private static final FinalSymbol X_LETTER = new FinalSymbol("x");

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Remove line start", new Sequence(START, X_LETTER), X_LETTER},
                {"Remove line end", new Sequence(X_LETTER, END), X_LETTER}
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
            assertEquals(aExpectedNode.toString(), simpleNode.toString());
        } catch (Exception e) {
            assertEquals(aExpectedException, e);
        }
    }
}
