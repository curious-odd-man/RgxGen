package com.github.curiousoddman.rgxgen.nodes;

import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.util.SymbolSetTestUtils;
import com.github.curiousoddman.rgxgen.util.Util;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.makeAsciiCharacterArray;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class SymbolSetCaseInsensitiveTests {
    public static Character[] excluding(String chars) {
        Character[] allSymbols = makeAsciiCharacterArray();
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


    public static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("a", "Aa"),
                Arguments.of("abc", "ABCabc"),
                Arguments.of("123", "123"),
                Arguments.of("123ab", "123ABab"),
                Arguments.of("A", "Aa")
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void positiveSetTest(String aOriginalString, String aExpectedCaseInsensitive) {
        SymbolSet symbolSet = SymbolSetTestUtils.ofAsciiCharacters(aOriginalString, Util.stringToChars(aOriginalString), MatchType.POSITIVE);
        Character[] actual = symbolSet.getCaseInsensitiveSymbolSetIndexer().getAll();
        Character[] expected = Util.stringToChars(aExpectedCaseInsensitive);
        assertArrayEquals(expected, actual, "\n" + Arrays.asList(expected) + "\nexpected vs got\n" + Arrays.asList(actual) + "\n");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void negativeSetTest(String aOriginalString, String aExpectedCaseInsensitive) {
        SymbolSet symbolSet = SymbolSetTestUtils.ofAsciiCharacters(aOriginalString, Util.stringToChars(aOriginalString), MatchType.NEGATIVE);
        Character[] actual = symbolSet.getCaseInsensitiveSymbolSetIndexer().getAll();
        Character[] expected = excluding(aExpectedCaseInsensitive);
        assertArrayEquals(expected, actual, "\n" + Arrays.asList(expected) + "\nexpected vs got\n" + Arrays.asList(actual) + "\n");

    }
}
