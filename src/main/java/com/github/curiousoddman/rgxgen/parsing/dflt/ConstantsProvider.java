package com.github.curiousoddman.rgxgen.parsing.dflt;

/* **************************************************************************
   Copyright 2019 Vladislavs Varslavans

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
/* **************************************************************************/


import com.github.curiousoddman.rgxgen.model.SymbolRange;
import lombok.experimental.UtilityClass;

import java.math.BigInteger;
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
    public static final  Character[] ZERO_LENGTH_CHARACTER_ARRAY = new Character[0];
    public static final  BigInteger  BIG_INTEGER_TWO             = BigInteger.valueOf(2);
    private static final int         SPACE_ASCII_CODE            = 32;     // First printable character in ASCII table
    private static final int         DEL_ASCII_CODE        = 127;    // Bound for printable characters in ASCII table
    private static final int         MAX_UNICODE_CHARACTER = 0xFFFF;
    public static final  SymbolRange ASCII_SYMBOL_RANGE    = SymbolRange.range(SPACE_ASCII_CODE, DEL_ASCII_CODE);
    public static final  SymbolRange UNICODE_SYMBOL_RANGE  = SymbolRange.range(SPACE_ASCII_CODE, MAX_UNICODE_CHARACTER);

    public static Character[] getDigits() {
        return IntStream.rangeClosed('0', '9')
                        .mapToObj(i -> (char) i)
                        .toArray(Character[]::new);
    }

    public static Character[] getAsciiWhitespaces() {
        return new Character[]{'\r', '\f', '\u000B', ' ', '\t', '\n'};
    }

    public static List<SymbolRange> getAsciiWordCharRanges() {
        return Collections.unmodifiableList(Arrays.asList(SMALL_LATIN_LETTERS, CAPITAL_LATIN_LETTERS, DIGITS));
    }
}
