package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.parsing.dflt.flags.ParsingFlags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FailingParsingTests {


    public static Stream<Arguments> getData() {
        return Stream.of(
                Arguments.of("lookbehindIncorrectPatternTest", "(?<xxx)",
                        """
                                Incomplete group structure:\s
                                '(?<xxx)'
                                   ^"""),
                Arguments.of("lookaheadIncorrectPatternTest", "(?xxx)",
                        """
                                Incomplete group structure:\s
                                '(?xxx)'
                                   ^"""),
                Arguments.of("disallowedGroupReferenceTest", "(asd)[a-\\1]",
                        """
                                Group ref is not expected here.\s
                                ')[a-\\1]'
                                      ^"""),
                Arguments.of("malformedUpperBoundNumberTest", "a{1,\t}",
                        """
                                Malformed upper bound number.
                                'a{1,\t}'
                                      ^"""),
                Arguments.of("malformedLowerBoundNumberTest", "a{c,3}",
                        """
                                Malformed lower bound number.
                                'a{c,3}'
                                   ^"""),
                Arguments.of("unbalancedRepetitionCurvyBracesTest", "a{1,2",
                        """
                                Unbalanced '{' - missing '}' at\s
                                'a{1,2'
                                  ^"""),
                Arguments.of("unbalancedCharacterRepetitionBracesTest", "[asdf",
                        """
                                Unexpected End Of Expression. Didn't find closing ']'
                                '[asdf'
                                 ^""")
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getData")
    void incorrectPatternTest(String name, String pattern, String expectedExceptionText) {
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern, new DefaultNodeCreator(), null);
        RgxGenParseException exception = assertThrows(RgxGenParseException.class, defaultTreeBuilder::build);
        assertEquals(expectedExceptionText, exception.getMessage());
    }

    @Test
    public void unexpectedRepetitionCharacterTest() throws Throwable {
        Node dummyNode = new FinalSymbol("", ParsingFlags.EMPTY);
        String pattern = "a{1,2";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern, new DefaultNodeCreator(), null);
        try {
            Field nodesStartPos = DefaultTreeBuilder.class.getDeclaredField("aNodesStartPos");
            nodesStartPos.setAccessible(true);
            Map<Node, Integer> o = (Map<Node, Integer>) nodesStartPos.get(defaultTreeBuilder);
            o.put(dummyNode, 0);
            Method handleRepeat = DefaultTreeBuilder.class.getDeclaredMethod("handleRepeat", char.class, Node.class);
            handleRepeat.setAccessible(true);
            InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> handleRepeat.invoke(defaultTreeBuilder, 'x', dummyNode));
            Throwable cause = exception.getCause();
            assertSame(RgxGenParseException.class, cause.getClass());
            assertEquals("""
                    Unknown repetition character 'x'
                    'a{1,'
                    ^""", cause.getMessage());
            //  handleRepeat.invoke(defaultTreeBuilder, 'x', dummyNode);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            fail("Unexpected exception: ", e);
        }
    }

    @Test
    public void escapeCharacterInCurvyBracesTest() {
        RgxGenParseException exception = assertThrows(RgxGenParseException.class, () -> RgxGen.parse("a{\\"));
        assertEquals("""
                Escape character inside curvy repetition is not supported.\s
                'a{\\'
                   ^""", exception.getMessage());
    }

    @Test
    public void nothingToRepeatTest() {
        RgxGenParseException exception = assertThrows(RgxGenParseException.class, () -> RgxGen.parse("+asdfqwer"));
        assertEquals("""
                Cannot repeat nothing at
                '+asdf'
                 ^""", exception.getMessage());
    }
}
