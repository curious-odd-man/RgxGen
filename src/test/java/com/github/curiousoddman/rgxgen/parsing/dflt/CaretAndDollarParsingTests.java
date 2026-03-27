package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.flags.ParsingFlags;
import com.github.curiousoddman.rgxgen.parsing.dflt.flags.WritableParsingFlags;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.parsing.dflt.flags.WritableParsingFlags.parsingFlags;
import static org.junit.jupiter.api.Assertions.*;


public class CaretAndDollarParsingTests {
    public static Stream<TestCase> data() {
        return Stream.of(new TestCase("^a$", new FinalSymbol("a", WritableParsingFlags.caretAndDollar())),
                new TestCase("^a", new FinalSymbol("a", parsingFlags().withCaret())),
                new TestCase("a$", new FinalSymbol("a", parsingFlags().withDollar())),
                new TestCase("(^a$)", new Group("(^a$)", 1, new FinalSymbol("a", WritableParsingFlags.caretAndDollar()))),
                new TestCase("(^a|^b|^c)", new Group("(^a|^b|^c)", 1, new Choice("(^a|^b|^c)",
                        new FinalSymbol("a", parsingFlags().withCaret().withChoice()),
                        new FinalSymbol("b", parsingFlags().withCaret().withChoice()),
                        new FinalSymbol("c", parsingFlags().withCaret().withChoice())))),
                new TestCase("(a$|b$|c$)", new Group("(a$|b$|c$)", 1,
                        new Choice("(a$|b$|c$)",
                                new FinalSymbol("a", parsingFlags().withDollar().withChoice()),
                                new FinalSymbol("b", parsingFlags().withDollar().withChoice()),
                                new FinalSymbol("c", parsingFlags().withDollar().withChoice())))),
                new TestCase("((^a|^b)xyz)", new Group("((^a|^b)xyz)", 1,
                        new Sequence("((^a|^b)xyz)",
                                new Group("(^a|^b)", 2,
                                        new Choice("(^a|^b)",
                                                new FinalSymbol("a", parsingFlags().withCaret().withChoice()),
                                                new FinalSymbol("b", parsingFlags().withCaret().withChoice()))),
                                new FinalSymbol("xyz", ParsingFlags.EMPTY)))),
                new TestCase("(xyz(a$|b$))", new Group("(xyz(a$|b$))", 1,
                        new Sequence("((a$|b$)xyz)",
                                new FinalSymbol("xyz", ParsingFlags.EMPTY),
                                new Group("(a$|b$)", 2,
                                        new Choice("(a$|b$)",
                                                new FinalSymbol("a", parsingFlags().withDollar().withChoice()),
                                                new FinalSymbol("b", parsingFlags().withDollar().withChoice())))))),
                new TestCase("(^a)+", new Repeat("(^a)+", new Group("(^a)", 1, new FinalSymbol("a", parsingFlags().withCaret())), 1, -1)), // Correctly matches first 'a' in string "aaaa"
                new TestCase("(b$)+", new Repeat("(b$)+", new Group("(b$)", 1, new FinalSymbol("b", parsingFlags().withDollar())), 1, -1)), // Correctly matches last b letter in "bbbb"
                new TestCase("a$\n^b", new FinalSymbol("a\nb", WritableParsingFlags.caretAndDollar())), // Correctly matches a and b on different lines. Note, would not work without newline
                new TestCase("a\n^b", new FinalSymbol("a\nb", parsingFlags().withCaret())),
                new TestCase("a$\nb", new FinalSymbol("a\nb", parsingFlags().withDollar())),
                new TestCase("(a\n^|c)", new Group("(a\n^|c)", 1,
                        new Choice("(a\n^|c)",
                                new FinalSymbol("a\n", parsingFlags().withChoice().withCaret()), new FinalSymbol("c", parsingFlags().withChoice())))), // This pattern is good to go, since both parts may produce valid result
                new TestCase("(a$|c)\nx", new Sequence("(a$|c)\nx",
                        new Group("(a$|c)", 1,
                                new Choice("(a$|c)",
                                        new FinalSymbol("a", parsingFlags().withDollar().withChoice()),
                                        new FinalSymbol("c", parsingFlags().withChoice()))),
                        new FinalSymbol("\nx", ParsingFlags.EMPTY))), // This pattern successfully matches "a\nx" and "c\nx"

                // Error TokenNotQuantifiable for any repetition
                new TestCase("^+", new TokenNotQuantifiableException("""
                        ^ at\s
                        '^+'
                          ^""")),
                new TestCase("^*", new TokenNotQuantifiableException("""
                        ^ at\s
                        '^*'
                          ^""")),
                new TestCase("^{1,2}", new TokenNotQuantifiableException("""
                        ^ at\s
                        '^{1,2}'
                          ^""")),
                new TestCase("^?", new TokenNotQuantifiableException("""
                        ^ at\s
                        '^?'
                          ^""")),
                new TestCase("$+", new TokenNotQuantifiableException("""
                        $ at\s
                        '$+'
                          ^""")),
                new TestCase("$*", new TokenNotQuantifiableException("""
                        $ at\s
                        '$*'
                          ^""")),
                new TestCase("${1,2}", new TokenNotQuantifiableException("""
                        $ at\s
                        '${1,2}'
                          ^""")),
                new TestCase("$?", new TokenNotQuantifiableException("""
                        $ at\s
                        '$?'
                          ^""")),

                // Error PatternDoesNotMatchAnything
                new TestCase("a$^b", new PatternDoesNotMatchAnythingException("""
                        Start and end of line markers cannot be put together.
                        'a$^b'
                          ^""")),
                new TestCase("$^", new PatternDoesNotMatchAnythingException("""
                        Start and end of line markers cannot be put together.
                        '$^'
                         ^""")),
                new TestCase("^^", new PatternDoesNotMatchAnythingException("""
                        Start and end of line markers cannot be put together.
                        '^^'
                          ^""")),

                new TestCase("a$b", new PatternDoesNotMatchAnythingException("""
                        After dollar only new line is allowed!
                        'a$b'
                          ^""")),
                new TestCase("a^b", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        'a^b'
                          ^""")),
                new TestCase("a^", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        'a^'
                          ^""")),
                new TestCase("$b", new PatternDoesNotMatchAnythingException("""
                        After dollar only new line is allowed!
                        '$b'
                         ^""")),
                new TestCase("(a^)", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a^)'
                           ^""")),

                new TestCase("(a^|c^)", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a^|c^)'
                           ^""")),
                new TestCase("(a^|c)", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a^|c)'
                           ^""")),      // Although this pattern will match 'c' letter - throw an exception, because from generation perspective a^ part is not valid.
                new TestCase("(a^|c)\nx", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a^|c)
                        '
                           ^""")),// Same as previous
                new TestCase("(a)^x", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a)^x'
                            ^""")),
                new TestCase("x$(a)", new PatternDoesNotMatchAnythingException("""
                        After dollar only new line is allowed!
                        'x$(a)'
                          ^"""))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void parseTest(TestCase aTestCase) {
        NodeTreeBuilder builder = new DefaultTreeBuilder(aTestCase.pattern(), new DefaultNodeCreator(), null);
        Exception expectedException = aTestCase.exception();

        if (expectedException == null) {
            assertEquals(aTestCase.result()
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
