package com.github.curiousoddman.rgxgen.nodes;

import com.github.curiousoddman.rgxgen.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class SymbolSetCaseInsensitiveTests {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.stream(new String[][]{
                {"a", "Aa"},
                {"abc", "AaBbCc"},
                {"123", "123"},
                {"123ab", "1Aa2Bb3"},
                {"A", "aA"},
                })
                     .map(arr -> new Object[]{arr[0], new SymbolSet(arr[0], Util.stringToCharsSubstrings(arr[0]), SymbolSet.TYPE.POSITIVE), Util.stringToCharsSubstrings(arr[1])})
                     .collect(Collectors.toList());
    }

    @Parameterized.Parameter
    public String aOriginalString;

    @Parameterized.Parameter(1)
    public SymbolSet aSymbolSet;

    @Parameterized.Parameter(2)
    public String[] aExpectedCaseInsensitive;


    @Test
    public void test() {
        String[] actual = aSymbolSet.getSymbolsCaseInsensitive();
        assertArrayEquals("\n" + Arrays.asList(aExpectedCaseInsensitive) + "\nvs\n" + Arrays.asList(actual) + "\n",
                          aExpectedCaseInsensitive, actual);
    }
}
