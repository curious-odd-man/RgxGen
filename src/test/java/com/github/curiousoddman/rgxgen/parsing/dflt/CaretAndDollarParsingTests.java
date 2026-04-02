package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.visitors.PrettyPrintVisitor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class CaretAndDollarParsingTests {
    public static Stream<TestCase> data() {
        return Stream.of(
                new TestCase("start_a_end", "^a$"),
                new TestCase("start_a", "^a"),
                new TestCase("a_end", "a$"),
                new TestCase("group_start_end_a", "(^a$)"),
                new TestCase("start_alt_abc", "(^a|^b|^c)"),
                new TestCase("end_alt_abc", "(a$|b$|c$)"),
                new TestCase("start_alt_ab_xyz", "((^a|^b)xyz)"),
                new TestCase("xyz_end_alt_ab", "(xyz(a$|b$))"),
                new TestCase("repeat_start_a", "(^a)+"), // Correctly matches first 'a' in string "aaaa"
                new TestCase("repeat_end_b", "(b$)+"), // Correctly matches last b letter in "bbbb"
                new TestCase("newline_a_end_then_b_start", "a$\n^b"), // Correctly matches a and b on different lines. Note, would not work without newline
                new TestCase("newline_a_then_b_start", "a\n^b"),
                new TestCase("newline_a_end_then_b", "a$\nb"),
                new TestCase("alt_a_newline_start_or_c", "(a\n^|c)"), // This pattern is good to go, since both parts may produce valid result
                new TestCase("alt_a_end_or_c_then_newline_x", "(a$|c)\nx"), // This pattern successfully matches "a\nx" and "c\nx"

                // Error TokenNotQuantifiable for any repetition
                new TestCase("", "^+", new TokenNotQuantifiableException("""
                        ^ at\s
                        '^+'
                          ^""")),
                new TestCase("", "^*", new TokenNotQuantifiableException("""
                        ^ at\s
                        '^*'
                          ^""")),
                new TestCase("", "^{1,2}", new TokenNotQuantifiableException("""
                        ^ at\s
                        '^{1,2}'
                          ^""")),
                new TestCase("", "^?", new TokenNotQuantifiableException("""
                        ^ at\s
                        '^?'
                          ^""")),
                new TestCase("", "$+", new TokenNotQuantifiableException("""
                        $ at\s
                        '$+'
                          ^""")),
                new TestCase("", "$*", new TokenNotQuantifiableException("""
                        $ at\s
                        '$*'
                          ^""")),
                new TestCase("", "${1,2}", new TokenNotQuantifiableException("""
                        $ at\s
                        '${1,2}'
                          ^""")),
                new TestCase("", "$?", new TokenNotQuantifiableException("""
                        $ at\s
                        '$?'
                          ^""")),

                // Error PatternDoesNotMatchAnything
                new TestCase("", "a$^b", new PatternDoesNotMatchAnythingException("""
                        Start and end of line markers cannot be put together.
                        'a$^b'
                          ^""")),
                new TestCase("", "$^", new PatternDoesNotMatchAnythingException("""
                        Start and end of line markers cannot be put together.
                        '$^'
                         ^""")),
                new TestCase("", "^^", new PatternDoesNotMatchAnythingException("""
                        Start and end of line markers cannot be put together.
                        '^^'
                          ^""")),

                new TestCase("", "a$b", new PatternDoesNotMatchAnythingException("""
                        After dollar only new line is allowed!
                        'a$b'
                          ^""")),
                new TestCase("", "a^b", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        'a^b'
                          ^""")),
                new TestCase("", "a^", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        'a^'
                          ^""")),
                new TestCase("", "$b", new PatternDoesNotMatchAnythingException("""
                        After dollar only new line is allowed!
                        '$b'
                         ^""")),
                new TestCase("", "(a^)", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a^)'
                           ^""")),

                new TestCase("", "(a^|c^)", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a^|c^)'
                           ^""")),
                new TestCase("", "(a^|c)", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a^|c)'
                           ^""")),      // Although this pattern will match 'c' letter - throw an exception, because from generation perspective a^ part is not valid.
                new TestCase("", "(a^|c)\nx", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a^|c)
                        '
                           ^""")),// Same as previous
                new TestCase("", "(a)^x", new PatternDoesNotMatchAnythingException("""
                        Before caret only new line is allowed!
                        '(a)^x'
                            ^""")),
                new TestCase("", "x$(a)", new PatternDoesNotMatchAnythingException("""
                        After dollar only new line is allowed!
                        'x$(a)'
                          ^"""))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void parseTest(TestCase aTestCase) throws IOException {
        NodeTreeBuilder builder = new DefaultTreeBuilder(aTestCase.pattern(), new DefaultNodeCreator(), null);
        Exception expectedException = aTestCase.exception();

        if (expectedException == null) {
            Node node = builder.get();
            PrettyPrintVisitor prettyPrintVisitor = new PrettyPrintVisitor();
            node.visit(prettyPrintVisitor);
            String prettyPrintedNodes = prettyPrintVisitor.getResult();
            try {
                assertEquals(aTestCase.getExpectedFromFile(), prettyPrintedNodes);
            } catch (AssertionFailedError | NoSuchFileException e) {
                Files.writeString(aTestCase.getExpectedFilePath(), prettyPrintedNodes);
                throw e;
            }
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
