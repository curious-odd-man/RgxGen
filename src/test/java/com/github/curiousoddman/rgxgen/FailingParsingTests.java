package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.generator.nodes.Node;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.fail;

public class FailingParsingTests {

    @Test(expected = RuntimeException.class)
    public void lookbehindIncorrectPattern() {
        String pattern = "(?<xxx)";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }

    @Test(expected = RuntimeException.class)
    public void disallowedGroupReference() {
        String pattern = "(asd)[a-\\1]";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }

    @Test(expected = RuntimeException.class)
    public void escapeCharacterInsideCurvyRepetition() {
        String pattern = "a{1,\t}";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }

    @Test(expected = RuntimeException.class)
    public void unbalancedRepetitionCurvyBraces() {
        String pattern = "a{1,2";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }

    @Test(expected = RuntimeException.class)
    public void unexpectedRepetitionCharacter() throws Throwable {
        final Node dummyNode = new FinalSymbol("");
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

    @Test(expected = RuntimeException.class)
    public void unbalancedCharacterRepetitionBraces() {
        String pattern = "[asdf";
        DefaultTreeBuilder defaultTreeBuilder = new DefaultTreeBuilder(pattern);
        defaultTreeBuilder.build();
    }
}
