package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Helper class for lazy initialization and reuse of some constants that are re-used.
 * Use with caution - don't modify values inside those!!!
 */
@UtilityClass
public class ConstantsProvider {
    public static final  SymbolRange SMALL_LATIN_LETTERS   = SymbolRange.range('a', 'z');
    public static final  SymbolRange CAPITAL_LATIN_LETTERS = SymbolRange.range('A', 'Z');
    public static final  SymbolRange DIGITS                = SymbolRange.range('0', '9');
    private static final int         SPACE_ASCII_CODE      = 32;     // First printable character in ASCII table
    private static final int         DEL_ASCII_CODE        = 127;    // Bound for printable characters in ASCII table

    public static Character[] getDigits() {
        return IntStream.rangeClosed('0', '9')
                        .mapToObj(i -> (char) i)
                        .toArray(Character[]::new);
    }

    public static Character[] getWhitespaces() {
        return new Character[]{'\r', '\f', '\u000B', ' ', '\t', '\n'};
    }

    public static List<SymbolRange> getWordCharRanges() {
        return Collections.unmodifiableList(Arrays.asList(SMALL_LATIN_LETTERS, CAPITAL_LATIN_LETTERS, DIGITS));
    }

    public static Character[] makeAsciiCharacterArray() {
        Character[] characters = new Character[DEL_ASCII_CODE - SPACE_ASCII_CODE];
        for (int i = SPACE_ASCII_CODE; i < DEL_ASCII_CODE; ++i) {
            characters[i - SPACE_ASCII_CODE] = (char) i;
        }
        return characters;
    }
}
