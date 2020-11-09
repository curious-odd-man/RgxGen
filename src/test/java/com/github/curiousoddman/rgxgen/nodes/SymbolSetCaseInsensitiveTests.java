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
    public static String[] excluding(String chars) {
        String[] allSymbols = SymbolSet.getAllSymbols();
        String[] result = new String[allSymbols.length - chars.length()];
        int target = 0;
        for (String sym : allSymbols) {
            if (!chars.contains(sym)) {
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
        SymbolSet symbolSet = new SymbolSet(aOriginalString, Util.stringToCharsSubstrings(aOriginalString), SymbolSet.TYPE.POSITIVE);
        String[] actual = symbolSet.getSymbolsCaseInsensitive();
        String[] expected = Util.stringToCharsSubstrings(aExpectedCaseInsensitive);
        assertArrayEquals("\n" + Arrays.asList(expected) + "\nvs\n" + Arrays.asList(actual) + "\n",
                          expected, actual);
    }

    @Test
    public void negativeSetTest() {
        SymbolSet symbolSet = new SymbolSet(aOriginalString, Util.stringToCharsSubstrings(aOriginalString), SymbolSet.TYPE.NEGATIVE);
        String[] actual = symbolSet.getSymbolsCaseInsensitive();
        String[] expected = excluding(aExpectedCaseInsensitive);
        assertArrayEquals("\n" + Arrays.asList(expected) + "\nvs\n" + Arrays.asList(actual) + "\n",
                          expected, actual);

    }
}
