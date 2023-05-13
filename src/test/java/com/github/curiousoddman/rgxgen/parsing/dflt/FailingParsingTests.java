package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.nodes.Node;
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
                             "Unexpected symbol in pattern: \n" +
                                     "'(?<xxx)'\n" +
                                     "    ^"),
                Arguments.of("lookaheadIncorrectPatternTest", "(?xxx)",
                             "Unexpected symbol in pattern: \n" +
                                     "'(?xxx)'\n" +
                                     "   ^"),
                Arguments.of("disallowedGroupReferenceTest", "(asd)[a-\\1]",
                             "Group ref is not expected here. \n" +
                                     "')[a-\\1]'\n" +
                                     "      ^"),
                Arguments.of("malformedUpperBoundNumberTest", "a{1,\t}",
                             "Malformed upper bound number.\n" +
                                     "'a{1,\t}'\n" +
                                     "      ^"),
                Arguments.of("malformedLowerBoundNumberTest", "a{c,3}",
                             "Malformed lower bound number.\n" +
                                     "'a{c,3}'\n" +
                                     "   ^"),
                Arguments.of("unbalancedRepetitionCurvyBracesTest", "a{1,2",
                             "Unbalanced '{' - missing '}' at \n" +
                                     "'a{1,2'\n" +
                                     "  ^"),
                Arguments.of("unbalancedCharacterRepetitionBracesTest", "[asdf",
                             "Unexpected End Of Expression. Didn't find closing ']'\n" +
                                     "'[asdf'\n" +
                                     " ^")
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getData")
    void incorrectPatternTest(String name, String pattern, String expectedExceptionText) {
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        RgxGenParseException exception = assertThrows(RgxGenParseException.class, defaultTreeBuilder::build);
        assertEquals(expectedExceptionText, exception.getMessage());
    }

    @Test
    public void unexpectedRepetitionCharacterTest() throws Throwable {

        Node dummyNode = new FinalSymbol("");
        String pattern = "a{1,2";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        try {
            Field aNodesStartPos = DefaultTreeBuilder.class.getDeclaredField("aNodesStartPos");
            aNodesStartPos.setAccessible(true);
            Map<Node, Integer> o = (Map<Node, Integer>) aNodesStartPos.get(defaultTreeBuilder);
            o.put(dummyNode, 0);
            Method handleRepeat = DefaultTreeBuilder.class.getDeclaredMethod("handleRepeat", char.class, Node.class);
            handleRepeat.setAccessible(true);
            InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> handleRepeat.invoke(defaultTreeBuilder, 'x', dummyNode));
            Throwable cause = exception.getCause();
            assertSame(RgxGenParseException.class, cause.getClass());
            assertEquals("Unknown repetition character 'x'\n" +
                                 "'a{1,'\n" +
                                 "^", cause.getMessage());
          //  handleRepeat.invoke(defaultTreeBuilder, 'x', dummyNode);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void escapeCharacterInCurvyBracesTest() {
        RgxGenParseException exception = assertThrows(RgxGenParseException.class, () -> new RgxGen("a{\\"));
        assertEquals("Escape character inside curvy repetition is not supported. \n" +
                             "'a{\\'\n" +
                             "   ^", exception.getMessage());
    }

    @Test
    public void nothingToRepeatTest() {
        RgxGenParseException exception = assertThrows(RgxGenParseException.class, () -> new RgxGen("+asdfqwer"));
        assertEquals("Cannot repeat nothing at\n" +
                             "'+asdf'\n" +
                             " ^", exception.getMessage());
    }
}
