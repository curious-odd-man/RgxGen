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
import com.github.curiousoddman.rgxgen.nodes.Node;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Helper class for lazy initialization and reuse of some constants that are re-used.
 * Use with caution - don't modify values inside those!!!
 */
public class ConstantsProvider {
    public static final SymbolRange SMALL_LATIN_LETTERS         = SymbolRange.range('a', 'z');
    public static final SymbolRange CAPITAL_LATIN_LETTERS       = SymbolRange.range('A', 'Z');
    public static final SymbolRange DIGITS                      = SymbolRange.range('0', '9');
    public static final char[]      ZERO_LENGTH_CHARACTER_ARRAY = new char[0];
    public static final BigInteger  BIG_INTEGER_TWO             = BigInteger.valueOf(2);
    public static final int         SPACE_ASCII_CODE            = 32;     // First printable character in ASCII table
    public static final int         DEL_ASCII_CODE              = 127;    // Bound for printable characters in ASCII table
    public static final int         MAX_UNICODE_CHARACTER       = 0xD800 - 1;       // The start of IN_HIGH_SURROGATES - which cause errors when trying to write those values to file
    public static final SymbolRange ASCII_SYMBOL_RANGE          = SymbolRange.range(SPACE_ASCII_CODE, DEL_ASCII_CODE - 1);  // -1 because we exclude DEL symbol
    public static final SymbolRange UNICODE_SYMBOL_RANGE        = SymbolRange.range(SPACE_ASCII_CODE, MAX_UNICODE_CHARACTER);
    public static final int         HEX_RADIX                   = 16;
    public static final Node[]      EMPTY_NODES_ARR             = new Node[0];

    public static final SymbolRange ASCII_DIGITS = SymbolRange.range('0', '9');

    public static char[] getAsciiWhitespaces() {
        return new char[]{'\t', '\n', '\u000B', '\f', '\r', ' '};
    }

    public static List<SymbolRange> getAsciiWordCharRanges() {
        return Collections.unmodifiableList(Arrays.asList(SMALL_LATIN_LETTERS, CAPITAL_LATIN_LETTERS, DIGITS));
    }

    public static char[] makeAsciiCharacterArray() {
        char[] characters = new char[DEL_ASCII_CODE - SPACE_ASCII_CODE];
        for (int i = SPACE_ASCII_CODE; i < DEL_ASCII_CODE; ++i) {
            characters[i - SPACE_ASCII_CODE] = (char) i;
        }
        return characters;
    }
}
