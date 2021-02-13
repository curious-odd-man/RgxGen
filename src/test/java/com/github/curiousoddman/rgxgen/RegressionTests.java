package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import org.junit.Test;

import java.math.BigInteger;
import java.util.regex.Pattern;

import static com.github.curiousoddman.rgxgen.util.Util.BIG_INTEGER_TWO;
import static org.junit.Assert.*;

public class RegressionTests {

    @Test
    public void bug23_parseEscapeCharacterInSquareBracketsTest() {
        String pattern = "[A-Z0-9'\\-/\\.\\s]{0,2}";
        Pattern compile = Pattern.compile(pattern);
        RgxGen rgxGen = new RgxGen(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        StringIterator stringIterator = rgxGen.iterateUnique();
        while (stringIterator.hasNext()) {
            assertTrue(compile.matcher(stringIterator.next())
                              .matches());
        }
    }

    @Test
    public void bug31_topLevelChoiceIsNotRecognizedTest() {
        String pattern = "1|2";
        RgxGen rgxGen = new RgxGen(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        StringIterator stringIterator = rgxGen.iterateUnique();
        assertEquals(BIG_INTEGER_TWO, rgxGen.getUniqueEstimation()
                                            .orElse(null));
        assertEquals("1", stringIterator.next());
        assertEquals("2", stringIterator.next());
        assertFalse(stringIterator.hasNext());
    }

    @Test
    public void bug32_capAndDollarInTheMiddleAreNotHandledTest() {
        String pattern = "(^x|y$)";
        final RgxGen rgxGen = new RgxGen(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        final StringIterator stringIterator = rgxGen.iterateUnique();
        assertEquals(BigInteger.valueOf(2), rgxGen.getUniqueEstimation()
                                                  .orElse(null));
        assertEquals("x", stringIterator.next());
        assertEquals("y", stringIterator.next());
        assertFalse(stringIterator.hasNext());
    }

    @Test
    public void bug53_incorrectHandlingOfDashInSquareBracketsTest() {
        String pattern = "^[a-zA-Z0-9-._:]*$";
        Pattern compile = Pattern.compile(pattern);
        RgxGen rgxGen = new RgxGen(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        for (int i = 0; i < 100; i++) {
            String generated = rgxGen.generate();
            assertTrue("'" + generated + "' for pattern '" + pattern + "'", compile.matcher(generated)
                                                                                   .matches());
        }
    }

    @Test
    public void bug53_incorrectHandlingOfDashInSquareBracketsVariation1Test() {
        String pattern = "[\\s-a]";
        Pattern compile = Pattern.compile(pattern);
        RgxGen rgxGen = new RgxGen(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        for (int i = 0; i < 100; i++) {
            String generated = rgxGen.generate();
            assertTrue("'" + generated + "' for pattern '" + pattern + "'", compile.matcher(generated)
                                                                                   .matches());
        }
    }

    @Test
    public void bug61_iterateUniqueProducesIncorrectPatternTest() {
        RgxGen rgxGen = new RgxGen("a?b|c");
        StringIterator noGroupIterator = rgxGen.iterateUnique();
        RgxGen rgxGen1 = new RgxGen("(a?b)|c");
        StringIterator withGroupIterator = rgxGen1.iterateUnique();
        while (noGroupIterator.hasNext()) {
            assertTrue(withGroupIterator.hasNext());
            String next = noGroupIterator.next();
            String next1 = withGroupIterator.next();
            System.out.println("'" + next + "' : '" + next1 + "'");
            assertEquals(next, next1);
        }

        assertFalse(withGroupIterator.hasNext());
    }

    @Test
    public void bug63_negativeLookaheadErrorTest() {
        String pattern = "^((?!(BG|GB|KN|NK|NT|TN|ZZ)|(D|F|I|Q|U|V)[A-Z]|A-Z)[A-Z]{2})[0-9]{6}[A-D]?$";
        Pattern compile = Pattern.compile(pattern);
        RgxGen rgxGen = new RgxGen(pattern);

        for (int i = 0; i < 100; i++) {
            String generated = rgxGen.generate();
            assertTrue("'" + generated + "' for pattern '" + pattern + "'", compile.matcher(generated)
                                                                                   .find());
        }
    }

    @Test
    public void bug63_negativeLookaheadMinimalErrorTest() {
        String pattern = "^(?!B)[AB]$";
        Pattern compile = Pattern.compile(pattern);
        RgxGen rgxGen = new RgxGen(pattern);

        for (int i = 0; i < 100; i++) {
            String generated = rgxGen.generate();
            assertTrue("'" + generated + "' for pattern '" + pattern + "'", compile.matcher(generated)
                                                                                   .find());
            System.out.print('.');
        }
    }
}
