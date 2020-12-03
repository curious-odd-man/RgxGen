package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CaretAndDollarParsingTests {
    static class TestCase {
        private final String    aPattern;
        private final Node      aResult;
        private final Exception aException;

        TestCase(String pattern, Node result) {
            this(pattern, result, null);
        }

        TestCase(String pattern, Exception exception) {
            this(pattern, null, exception);
        }

        private TestCase(String pattern, Node result, Exception exception) {
            aPattern = pattern;
            aResult = result;
            aException = exception;
        }

        public String getPattern() {
            return aPattern;
        }

        public Node getResult() {
            return aResult;
        }

        public Exception getException() {
            return aException;
        }

        @Override
        public String toString() {
            return "TestCase{'" + aPattern + "'}";
        }
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<TestCase> data() {
        return Arrays.asList(new TestCase("^a$", new FinalSymbol("a")),
                             new TestCase("^a", new FinalSymbol("a")),
                             new TestCase("a$", new FinalSymbol("a")),
                             new TestCase("(^a$)", new FinalSymbol("a")),
                             new TestCase("(^a|^b|^c)", new Choice("(^a|^b|^c)",
                                                                   new FinalSymbol("a"),
                                                                   new FinalSymbol("b"),
                                                                   new FinalSymbol("c"))),
                             new TestCase("(a$|b$|c$)", new Choice("(a$|b$|c$)",
                                                                   new FinalSymbol("a"),
                                                                   new FinalSymbol("b"),
                                                                   new FinalSymbol("c"))),
                             new TestCase("((^a|^b)xyz)", new Sequence("((^a|^b)xyz)",
                                                                       new Choice("(^a|^b)",
                                                                                  new FinalSymbol("a"),
                                                                                  new FinalSymbol("b")),
                                                                       new FinalSymbol("xyz"))),
                             new TestCase("(xyz(a$|b$))", new Sequence("((^a|^b)xyz)",
                                                                       new FinalSymbol("xyz"),
                                                                       new Choice("(^a|^b)",
                                                                                  new FinalSymbol("a"),
                                                                                  new FinalSymbol("b")))),
                             new TestCase("(^a)+", Repeat.minimum("(^a)+", new FinalSymbol("a"), 1)), // Correctly matches first a in string "aaaa"
                             new TestCase("(b$)+", Repeat.minimum("(b$)+", new FinalSymbol("b"), 1)), // Correctly matches last b letter in "bbbb"
                             new TestCase("a$\\n^b", new FinalSymbol("a\\nb")), // Correctly matches a and b on different lines. Note, would not work without newline
                             new TestCase("a\\n^b", new FinalSymbol("a\\nb")),
                             new TestCase("a$\\nb", new FinalSymbol("a\\nb")),
                             new TestCase("(a\\n^|c)", new Choice("(a\\n^|c)",
                                                                  new FinalSymbol("a\\b"), new FinalSymbol("c"))), // This pattern is good to go, since both parts may produce valid result
                             new TestCase("(a$|c)\\nx", new Sequence("(a$|c)\\nx",
                                                                     new Choice("(a$|c)",
                                                                                new FinalSymbol("a"),
                                                                                new FinalSymbol("c")),
                                                                     new FinalSymbol("\nx"))), // This pattern successfully matches "a\nx" and "c\nx"

                             // Error TokenNotQuanitfiable for any repetition
                             new TestCase("^+", new TokenNotQuantifiableException("")),
                             new TestCase("^*", new TokenNotQuantifiableException("")),
                             new TestCase("^{1,2}", new TokenNotQuantifiableException("")),
                             new TestCase("^?", new TokenNotQuantifiableException("")),
                             new TestCase("$+", new TokenNotQuantifiableException("")),
                             new TestCase("$*", new TokenNotQuantifiableException("")),
                             new TestCase("${1,2}", new TokenNotQuantifiableException("")),
                             new TestCase("$?", new TokenNotQuantifiableException("")),

                             // Error PatternDoesNotMatchAnything
                             new TestCase("a$^b", new PatternDoesNotMatchAnythingException("")),
                             new TestCase("a$b", new PatternDoesNotMatchAnythingException("")),
                             new TestCase("a^b", new PatternDoesNotMatchAnythingException("")),
                             new TestCase("a^", new PatternDoesNotMatchAnythingException("")),
                             new TestCase("$b", new PatternDoesNotMatchAnythingException("")),
                             new TestCase("$^", new PatternDoesNotMatchAnythingException("")),
                             new TestCase("^^", new PatternDoesNotMatchAnythingException("")),
                             new TestCase("(a^)", new PatternDoesNotMatchAnythingException("")),
                             new TestCase("(a^|c^)", new PatternDoesNotMatchAnythingException("")),
                             new TestCase("(a^|c)", new PatternDoesNotMatchAnythingException("")),      // Although this pattern will match 'c' letter - throw an exception, because from generation perspective a^ part is not valid.
                             new TestCase("(a^|c)\\nx", new PatternDoesNotMatchAnythingException(""))// Same as previous
        );
    }

    @Parameterized.Parameter
    public TestCase aTestCase;

    @Test
    public void parseTest() {
        NodeTreeBuilder builder = new DefaultTreeBuilder(aTestCase.getPattern());
        Exception expectedException = aTestCase.getException();

        if (expectedException == null) {
            assertEquals(aTestCase.getResult()
                                  .toString(), builder.get()
                                                      .toString());
        } else {
            try {
                builder.get();
                fail("Expected to throw an exception");
            } catch (RgxGenParseException e) {
                assertSame(expectedException.getClass(), e.getClass());
                assertEquals(expectedException.getMessage(), e.getMessage());
            }
        }
    }
}
