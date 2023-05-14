package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.util.SymbolRange;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.curiousoddman.rgxgen.nodes.AsciiSymbolSet.*;

/**
 * Helper class for lazy initialization and reuse of some constants that are re-used.
 * Use with caution - don't modify values inside those!!!
 */
@SuppressWarnings("InstanceVariableMayNotBeInitialized")
class ConstantsProvider {
    private Character[]                 aDigits;
    private Character[]       aWhiteSpaces;     // "\u000B" - is a vertical tab
    private List<SymbolRange> aWordCharRanges;

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
            aWordCharRanges = Collections.unmodifiableList(Arrays.asList(SMALL_LETTERS, CAPITAL_LETTERS, DIGITS));
        }

        return aWordCharRanges;
    }
}
