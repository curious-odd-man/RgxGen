package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.generator.nodes.Node;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.RgxGenParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.fail;

public class FailingParsingTests {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void lookbehindIncorrectPatternTest() {
        expectedEx.expect(RgxGenParseException.class);
        expectedEx.expectMessage("Unexpected symbol in pattern: \n" +
                                         "'(?<xxx)'\n" +
                                         "    ^");

        String pattern = "(?<xxx)";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }

    @Test
    public void disallowedGroupReferenceTest() {
        expectedEx.expect(RgxGenParseException.class);
        expectedEx.expectMessage("Group ref is not expected here. \n" +
                                         "')[a-\\1]'\n" +
                                         "      ^");

        String pattern = "(asd)[a-\\1]";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }

    @Test
    public void escapeCharacterInsideCurvyRepetitionTest() {
        expectedEx.expect(RgxGenParseException.class);
        expectedEx.expectMessage("Escape character inside curvy braces is not allowed. \n" +
                                         "'a{1,\t}'\n" +
                                         "      ^");

        String pattern = "a{1,\t}";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }

    @Test
    public void unbalancedRepetitionCurvyBracesTest() {
        expectedEx.expect(RgxGenParseException.class);
        expectedEx.expectMessage("Unbalanced '{' - missing '}' at \n" +
                                         "'a{1,2'\n" +
                                         "  ^");

        String pattern = "a{1,2";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }

    @Test
    public void unexpectedRepetitionCharacterTest() throws Throwable {
        expectedEx.expect(RgxGenParseException.class);
        expectedEx.expectMessage("Unknown repetition character 'x'\n" +
                                         "'a{1,'\n" +
                                         "^");

        Node dummyNode = new FinalSymbol("");
        String pattern = "a{1,2";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        try {
            Method handleRepeat = DefaultTreeBuilder.class.getDeclaredMethod("handleRepeat", char.class, Node.class);
            handleRepeat.setAccessible(true);
            handleRepeat.invoke(defaultTreeBuilder, 'x', dummyNode);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Test
    public void unbalancedCharacterRepetitionBracesTest() {
        expectedEx.expect(RgxGenParseException.class);
        expectedEx.expectMessage("Unexpected End Of Expression. Didn't find closing ']'\n" +
                                         "'[asdf'\n" +
                                         " ^");

        String pattern = "[asdf";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }

    @Test
    public void unbalancedFigureBracesTest() {
        expectedEx.expect(RgxGenParseException.class);
        expectedEx.expectMessage("Unbalanced '{' - missing '}' at \n" +
                                         "'a{3,'\n" +
                                         "  ^");

        RgxGen gem = new RgxGen("a{3,");
    }
}
