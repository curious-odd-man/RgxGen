package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.model.SymbolRange;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Helper class for lazy initialization and reuse of some constants that are re-used.
 * Use with caution - don't modify values inside those!!!
 */
@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public class ConstantsProvider {
    public static final SymbolRange       SMALL_LATIN_LETTERS   = SymbolRange.of('a', 'z');
    public static final SymbolRange       CAPITAL_LATIN_LETTERS = SymbolRange.of('A', 'Z');
    public static final SymbolRange       DIGITS                = SymbolRange.of('0', '9');
    private             Character[]       aDigits;
    private             Character[]       aWhiteSpaces;     // "\u000B" - is a vertical tab
    private             List<SymbolRange> aWordCharRanges;

    Character[] getDigits() {
        if (aDigits == null) {
            aDigits = IntStream.rangeClosed('0', '9')
                               .mapToObj(i -> (char) i)
                               .toArray(Character[]::new);
        }

        return aDigits;
    }

    Character[] getWhitespaces() {
        if (aWhiteSpaces == null) {
            aWhiteSpaces = new Character[]{'\r', '\f', '\u000B', ' ', '\t', '\n'};
        }
        return aWhiteSpaces;
    }

    List<SymbolRange> getWordCharRanges() {
        if (aWordCharRanges == null) {
            aWordCharRanges = Collections.unmodifiableList(Arrays.asList(SMALL_LATIN_LETTERS, CAPITAL_LATIN_LETTERS, DIGITS));
        }

        return aWordCharRanges;
    }
}
