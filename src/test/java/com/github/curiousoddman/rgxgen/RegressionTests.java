package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import com.github.curiousoddman.rgxgen.simplifier.PatternDoesntMatchAnyStringException;
import org.junit.Test;

import java.math.BigInteger;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class RegressionTests {

    @Test
    public void bug23_parseEscapeCharacterInSquareBrackets() {
        String pattern = "[A-Z0-9'\\-/\\.\\s]{0,2}";
        final Pattern compile = Pattern.compile(pattern);
        final RgxGen rgxGen = new RgxGen(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        final StringIterator stringIterator = rgxGen.iterateUnique();
        while (stringIterator.hasNext()) {
            assertTrue(compile.matcher(stringIterator.next())
                              .matches());
        }
    }

    @Test
    public void bug31_topLevelChoiceIsNotRecognized() {
        String pattern = "1|2";
        final Pattern compile = Pattern.compile(pattern);
        final RgxGen rgxGen = new RgxGen(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        final StringIterator stringIterator = rgxGen.iterateUnique();
        assertEquals(BigInteger.valueOf(2), rgxGen.numUnique());
        assertEquals("1", stringIterator.next());
        assertEquals("2", stringIterator.next());
        assertFalse(stringIterator.hasNext());
    }

    @Test
    public void bug32_capAndDollarInTheMiddleAreNotHandled() {
        String pattern = "(^x|y$)";
        final Pattern compile = Pattern.compile(pattern);
        final RgxGen rgxGen = new RgxGen(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        final StringIterator stringIterator = rgxGen.iterateUnique();
        assertEquals(BigInteger.valueOf(2), rgxGen.numUnique());
        assertEquals("x", stringIterator.next());
        assertEquals("y", stringIterator.next());
        assertFalse(stringIterator.hasNext());
    }

    @Test(expected = PatternDoesntMatchAnyStringException.class)
    public void bug32_capAndDollarMakesNoMatches() {
        String pattern = "c(^x|y$)a";
        RgxGen rgxGen = new RgxGen(pattern);
    }
}
