package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


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


    public static Stream<TestCase> data() {
        return Stream.of(new TestCase("^a$", new FinalSymbol("a")),
                         new TestCase("^a", new FinalSymbol("a")),
                         new TestCase("a$", new FinalSymbol("a")),
                         new TestCase("(^a$)", new Group("(^a$)", 1, new FinalSymbol("a"))),
                         new TestCase("(^a|^b|^c)", new Group("(^a|^b|^c)", 1, new Choice("(^a|^b|^c)",
                                                                                          new FinalSymbol("a"),
                                                                                          new FinalSymbol("b"),
                                                                                          new FinalSymbol("c")))),
                         new TestCase("(a$|b$|c$)", new Group("(a$|b$|c$)", 1,
                                                              new Choice("(a$|b$|c$)",
                                                                         new FinalSymbol("a"),
                                                                         new FinalSymbol("b"),
                                                                         new FinalSymbol("c")))),
                         new TestCase("((^a|^b)xyz)", new Group("((^a|^b)xyz)", 1,
                                                                new Sequence("((^a|^b)xyz)",
                                                                             new Group("(^a|^b)", 2,
                                                                                       new Choice("(^a|^b)",
                                                                                                  new FinalSymbol("a"),
                                                                                                  new FinalSymbol("b"))),
                                                                             new FinalSymbol("xyz")))),
                         new TestCase("(xyz(a$|b$))", new Group("(xyz(a$|b$))", 1,
                                                                new Sequence("((^a|^b)xyz)",
                                                                             new FinalSymbol("xyz"),
                                                                             new Group("(^a|^b)", 2,
                                                                                       new Choice("(^a|^b)",
                                                                                                  new FinalSymbol("a"),
                                                                                                  new FinalSymbol("b")))))),
                         new TestCase("(^a)+", Repeat.minimum("(^a)+", new Group("(^a)", 1, new FinalSymbol("a")), 1)), // Correctly matches first a in string "aaaa"
                         new TestCase("(b$)+", Repeat.minimum("(b$)+", new Group("(b$)", 1, new FinalSymbol("b")), 1)), // Correctly matches last b letter in "bbbb"
                         new TestCase("a$\n^b", new FinalSymbol("a\nb")), // Correctly matches a and b on different lines. Note, would not work without newline
                         new TestCase("a\n^b", new FinalSymbol("a\nb")),
                         new TestCase("a$\nb", new FinalSymbol("a\nb")),
                         new TestCase("(a\n^|c)", new Group("(a\n^|c)", 1,
                                                            new Choice("(a\n^|c)",
                                                                       new FinalSymbol("a\n"), new FinalSymbol("c")))), // This pattern is good to go, since both parts may produce valid result
                         new TestCase("(a$|c)\nx", new Sequence("(a$|c)\nx",
                                                                new Group("(a$|c)", 1,
                                                                          new Choice("(a$|c)",
                                                                                     new FinalSymbol("a"),
                                                                                     new FinalSymbol("c"))),
                                                                new FinalSymbol("\nx"))), // This pattern successfully matches "a\nx" and "c\nx"

                         // Error TokenNotQuanitfiable for any repetition
                         new TestCase("^+", new TokenNotQuantifiableException("^ at \n" +
                                                                                      "'^+'\n" +
                                                                                      "  ^")),
                         new TestCase("^*", new TokenNotQuantifiableException("^ at \n" +
                                                                                      "'^*'\n" +
                                                                                      "  ^")),
                         new TestCase("^{1,2}", new TokenNotQuantifiableException("^ at \n" +
                                                                                          "'^{1,2}'\n" +
                                                                                          "  ^")),
                         new TestCase("^?", new TokenNotQuantifiableException("^ at \n" +
                                                                                      "'^?'\n" +
                                                                                      "  ^")),
                         new TestCase("$+", new TokenNotQuantifiableException("$ at \n" +
                                                                                      "'$+'\n" +
                                                                                      "  ^")),
                         new TestCase("$*", new TokenNotQuantifiableException("$ at \n" +
                                                                                      "'$*'\n" +
                                                                                      "  ^")),
                         new TestCase("${1,2}", new TokenNotQuantifiableException("$ at \n" +
                                                                                          "'${1,2}'\n" +
                                                                                          "  ^")),
                         new TestCase("$?", new TokenNotQuantifiableException("$ at \n" +
                                                                                      "'$?'\n" +
                                                                                      "  ^")),

                         // Error PatternDoesNotMatchAnything
                         new TestCase("a$^b", new PatternDoesNotMatchAnythingException("Start and end of line markers cannot be put together.\n" +
                                                                                               "'a$^b'\n" +
                                                                                               "  ^")),
                         new TestCase("$^", new PatternDoesNotMatchAnythingException("Start and end of line markers cannot be put together.\n" +
                                                                                             "'$^'\n" +
                                                                                             " ^")),
                         new TestCase("^^", new PatternDoesNotMatchAnythingException("Start and end of line markers cannot be put together.\n" +
                                                                                             "'^^'\n" +
                                                                                             "  ^")),

                         new TestCase("a$b", new PatternDoesNotMatchAnythingException("After dollar only new line is allowed!\n" +
                                                                                              "'a$b'\n" +
                                                                                              "  ^")),
                         new TestCase("a^b", new PatternDoesNotMatchAnythingException("Before caret only new line is allowed!\n" +
                                                                                              "'a^b'\n" +
                                                                                              "  ^")),
                         new TestCase("a^", new PatternDoesNotMatchAnythingException("Before caret only new line is allowed!\n" +
                                                                                             "'a^'\n" +
                                                                                             "  ^")),
                         new TestCase("$b", new PatternDoesNotMatchAnythingException("After dollar only new line is allowed!\n" +
                                                                                             "'$b'\n" +
                                                                                             " ^")),
                         new TestCase("(a^)", new PatternDoesNotMatchAnythingException("Before caret only new line is allowed!\n" +
                                                                                               "'(a^)'\n" +
                                                                                               "   ^")),

                         new TestCase("(a^|c^)", new PatternDoesNotMatchAnythingException("Before caret only new line is allowed!\n" +
                                                                                                  "'(a^|c^)'\n" +
                                                                                                  "   ^")),
                         new TestCase("(a^|c)", new PatternDoesNotMatchAnythingException("Before caret only new line is allowed!\n" +
                                                                                                 "'(a^|c)'\n" +
                                                                                                 "   ^")),      // Although this pattern will match 'c' letter - throw an exception, because from generation perspective a^ part is not valid.
                         new TestCase("(a^|c)\nx", new PatternDoesNotMatchAnythingException("Before caret only new line is allowed!\n" +
                                                                                                    "'(a^|c)\n" +
                                                                                                    "'\n" +
                                                                                                    "   ^")),// Same as previous
                         new TestCase("(a)^x", new PatternDoesNotMatchAnythingException("Before caret only new line is allowed!\n" +
                                                                                                "'(a)^x'\n" +
                                                                                                "    ^")),
                         new TestCase("x$(a)", new PatternDoesNotMatchAnythingException("After dollar only new line is allowed!\n" +
                                                                                                "'x$(a)'\n" +
                                                                                                "  ^"))
        );
    }


    @ParameterizedTest
    @MethodSource("data")
    public void parseTest(TestCase aTestCase) {
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
