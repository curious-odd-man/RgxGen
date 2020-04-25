package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
}
