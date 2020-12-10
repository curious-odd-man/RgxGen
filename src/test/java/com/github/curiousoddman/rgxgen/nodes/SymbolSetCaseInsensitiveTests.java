package com.github.curiousoddman.rgxgen.nodes;

import com.github.curiousoddman.rgxgen.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class SymbolSetCaseInsensitiveTests {
    public static Character[] excluding(String chars) {
        Character[] allSymbols = SymbolSet.getAllSymbols();
        Character[] result = new Character[allSymbols.length - chars.length()];
        int target = 0;
        for (Character sym : allSymbols) {
            if (chars.indexOf(sym) == -1) {
                result[target] = sym;
                ++target;
            }
        }
        return result;
    }


    @Parameterized.Parameters(name = "{0}")
    public static Collection<String[]> parameters() {
        return Arrays.asList(new String[][]{
                {"a", "Aa"},
                {"abc", "AaBbCc"},
                {"123", "123"},
                {"123ab", "1Aa2Bb3"},
                {"A", "aA"},
                });
    }

    @Parameterized.Parameter
    public String aOriginalString;

    @Parameterized.Parameter(1)
    public String aExpectedCaseInsensitive;


    @Test
    public void positiveSetTest() {
        SymbolSet symbolSet = new SymbolSet(aOriginalString, Util.stringToChars(aOriginalString), SymbolSet.TYPE.POSITIVE);
        Character[] actual = symbolSet.getSymbolsCaseInsensitive();
        Character[] expected = Util.stringToChars(aExpectedCaseInsensitive);
        assertArrayEquals("\n" + Arrays.asList(expected) + "\nexpected vs got\n" + Arrays.asList(actual) + "\n",
                          expected, actual);
    }

    @Test
    public void negativeSetTest() {
        SymbolSet symbolSet = new SymbolSet(aOriginalString, Util.stringToChars(aOriginalString), SymbolSet.TYPE.NEGATIVE);
        Character[] actual = symbolSet.getSymbolsCaseInsensitive();
        Character[] expected = excluding(aExpectedCaseInsensitive);
        assertArrayEquals("\n" + Arrays.asList(expected) + "\nexpected vs got\n" + Arrays.asList(actual) + "\n",
                          expected, actual);

    }
}
