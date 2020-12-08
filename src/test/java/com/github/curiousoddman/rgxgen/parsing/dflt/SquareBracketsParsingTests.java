package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.nodes.SymbolSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class SquareBracketsParsingTests {

    private static SymbolSet mkSS(Character... chars) {
        return new SymbolSet(Arrays.toString(chars), chars, SymbolSet.TYPE.POSITIVE);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"[a-c]", mkSS('a', 'b', 'c')},
                {"[a-c-]", mkSS('a', 'b', 'c', '-')},
                {"[a-c-x]", mkSS('a', 'b', 'c', '-', 'x')},
                {"[-a-c]", mkSS('-', 'a', 'b', 'c')},
                {"[\\x30-\\x{0032}]", mkSS('0', '1', '2')},
                {"[\\s-]", mkSS('\r', '\n', '\t', '\f', '\u000B', ' ', '-')},
                {"[-]", mkSS('-')},
                {"[\\s-a-\\s]", new RgxGenParseException("Cannot make range with a shorthand escape sequences before '\n" +
                                                                 "'s-a-\\s]'\n" +
                                                                 "      ^'")},
                {"[\\s-a]", mkSS('\r', '\n', '\t', '\f', '\u000B', ' ', '-', 'a')},
                {"[\\s]", mkSS('\r', '\n', '\t', '\f', '\u000B', ' ')},
                {"[a-]", mkSS('a', '-')}
        });
    }

    @Parameterized.Parameter
    public String aPattern;

    @Parameterized.Parameter(1)
    public Object aExpected;

    @Test
    public void parsingTest() {
        try {
            DefaultTreeBuilder builder = new DefaultTreeBuilder(aPattern);
            Node node = builder.get();
            assertEquals(aExpected.toString(), node.toString());
        } catch (RgxGenParseException e) {
            if (aExpected instanceof Throwable) {
                assertEquals(e.getMessage(), ((Throwable) aExpected).getMessage(), e.getMessage());
            } else {
                e.printStackTrace();
                fail("Got exception when expected SymbolSet. " + e.getMessage());
            }
        }
    }
}
