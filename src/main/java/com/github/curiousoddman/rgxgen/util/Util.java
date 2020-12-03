package com.github.curiousoddman.rgxgen.util;

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

import com.github.curiousoddman.rgxgen.nodes.SymbolSet;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Utility methods collection
 */
public final class Util {
    private static final String SYMBOLS = Arrays.stream(SymbolSet.getAllSymbols())
                                                .reduce("", String::concat);

    private static final Pattern    EMPTY                    = Pattern.compile("");
    public static final  String[]   ZERO_LENGTH_STRING_ARRAY = new String[0];
    public static final  BigInteger BIG_INTEGER_TWO          = BigInteger.valueOf(2);

    /**
     * Splits string into array of single-character strings
     *
     * @param str string to split
     * @return array of single-character strings
     */
    public static String[] stringToCharsSubstrings(CharSequence str) {
        return EMPTY.split(str);
    }

    /**
     * Repeats text multiple times
     *
     * @param c     character to repeat
     * @param times number of times. Values less or equal to zero will result in empty string
     * @return text repeated multiple times
     */
    public static String repeat_char(char c, int times) {
        if (times < 0) {
            return "";
        }
        char[] result = new char[times];
        Arrays.fill(result, c);

        return new String(result);
    }

    /**
     * Randomly change case for the letters in a string
     *
     * @param rnd   random to be used
     * @param input input string to randomize
     * @return string with random characters changed case.
     */
    public static String randomlyChangeCase(Random rnd, CharSequence input) {
        StringBuilder sb = new StringBuilder(input);
        for (int i = 0; i < sb.length(); i++) {
            char currentChar = sb.charAt(i);
            if (Character.isUpperCase(currentChar) && rnd.nextBoolean()) {
                sb.setCharAt(i, Character.toLowerCase(currentChar));
            } else if (Character.isLowerCase(currentChar) && rnd.nextBoolean()) {
                sb.setCharAt(i, Character.toUpperCase(currentChar));
            }
        }

        return sb.toString();
    }

    /**
     * Count number of variation of words in case insensitive manner.
     * For example for word "a" - there are 2 variation ("a" and "A").
     * For word "1a" - there are also 2 variations ("1a" and "1A")
     * For word "AB" - there are 4 variations: ("ab", "aB", "Ab", "BB")
     *
     * @param value word to calculate variations
     * @return number of variations.
     */
    public static BigInteger countCaseInsensitiveVariations(CharSequence value) {
        int switchableCase = value.chars()
                                  .map(c -> Character.isUpperCase(c) || Character.isLowerCase(c) ? 1 : 0)
                                  .sum();
        return BIG_INTEGER_TWO.pow(switchableCase);
    }

    /**
     * Finds next case sensitive character.
     * Case sensitive character is either lower-case or upper-case character.
     *
     * @param text       text to be analyzed
     * @param startIndex start search from index.
     * @return index of next case sensitive character or {@code empty} if no such character present
     */
    public static OptionalInt indexOfNextCaseSensitiveCharacter(CharSequence text, int startIndex) {
        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Utility class can't be instantiated
     */
    private Util() {
    }
}
