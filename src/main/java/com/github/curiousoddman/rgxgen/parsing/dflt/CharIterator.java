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

import com.github.curiousoddman.rgxgen.util.Util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Class incorporates functionality to iterate String char by char
 */
public class CharIterator implements Iterator<Character> {

    private final String aValue;

    private int aBoundIndex;
    private int aCurrentIndex;

    public CharIterator(String value) {
        aValue = value;
        aBoundIndex = aValue.length();
        aCurrentIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return aCurrentIndex < aBoundIndex;
    }

    /**
     * Skip next {@code n} characters
     *
     * @param n number of characters to skip
     */
    public void skip(int n) {
        aCurrentIndex += n;
    }

    /**
     * Skip next character
     */
    public void skip() {
        skip(1);
    }


    /**
     * Return next character, without advancing cursor
     *
     * @return next character
     */
    public char peek() {
        return aValue.charAt(aCurrentIndex);
    }

    /**
     * Return character by offset from the next, without advancing cursor
     *
     * @param offset offset value.
     * @return character by offset from next
     */
    public char peek(int offset) {
        int pos = aCurrentIndex + offset;
        if (pos < 0 || pos >= aBoundIndex) {
            return 0x00;
        }
        return aValue.charAt(pos);
    }

    /**
     * Returns next character and advances the cursor
     */
    @Override
    public Character next() {
        try {
            return aValue.charAt(aCurrentIndex++);
        } catch (StringIndexOutOfBoundsException e) {
            NoSuchElementException noSuchElementException = new NoSuchElementException(e.getMessage());
            noSuchElementException.initCause(e);
            throw noSuchElementException;
        }
    }

    /**
     * Return next {@code length} characters as a substring and advance cursor
     *
     * @param length number of characters to return
     * @return substring start from next of {@code length} characters
     */
    public String next(int length) {
        int upTo = aCurrentIndex + length;
        if (upTo > aBoundIndex) {
            upTo = aBoundIndex;
        }
        String substring = aValue.substring(aCurrentIndex, upTo);
        aCurrentIndex = upTo;
        return substring;
    }

    /**
     * Returns context of cursor (text around the cursor)
     *
     * @return substring [-5,+5) chars from current position
     */
    public String context() {
        // -1 is because when we take character (using next()) we already move aCurrentIndex further!
        // So we need to point to previous index, actually
        return context(aCurrentIndex - 1);
    }

    /**
     * Returns context around passed index
     *
     * @param index center point of context
     * @return substring [-5,+5) chars from index
     */
    public String context(int index) {
        int start = Math.max(0, index - 5);
        int end = Math.min(aBoundIndex, index + 5);
        int offsetOfPointer = start == 0
                              ? index
                              : 5;
        return "\n'" + aValue.substring(start, end) + "'\n" + Util.repeat_char(' ', 1 + offsetOfPointer) + '^';
    }

    /**
     * Calculate number of characters remaining to iterate over
     *
     * @return num of characters
     */
    public int remaining() {
        return aBoundIndex - aCurrentIndex;
    }

    /**
     * Returns substring from 'next' character UP TO first not escaped character {@code c}
     * Cursor is advanced to a position after character {@code c}
     * <p>
     * Example:
     * For text {@code '0123456789'}, {@code nextUntil('8')} will return {@code '01234567'} and put cursor before {@code '9'}
     *
     * @param c character to search for
     * @return substring from next character up to next not escaped character {@code c}
     * @throws NoSuchElementException if no such character present after next character
     */
    public String nextUntil(char c) {
        return nextUntil((str, fromIdx) -> str.indexOf(c, fromIdx), 1, true);
    }

    /**
     * Returns substring from next character up to next occurrence of {@code s}
     * Cursor is advanced to a position after last character in {@code s}
     * <p>
     * Example:
     * For text {@code '0123456789'}, {@code nextUntil("456")} will return {@code '0123'} and put cursor before {@code '7'}
     *
     * @param s string to search for
     * @return substring from next character up to next not escaped occurrence of s.
     * if string not found - returns all remaining characters
     */
    public String nextUntil(String s) {
        return nextUntil((str, fromIdx) -> str.indexOf(s, fromIdx), s.length(), false);
    }

    private String nextUntil(BiFunction<String, Integer, Integer> indexOf, int len, boolean mustExist) {
        int startIndex = aCurrentIndex;
        int substringEnd;
        while (true) {
            // Find ending character
            aCurrentIndex = indexOf.apply(aValue, aCurrentIndex);
            // Found, but outside of the bounds...
            if (aCurrentIndex + len > aBoundIndex) {
                aCurrentIndex = aBoundIndex;
                substringEnd = aCurrentIndex;
                break;
            } else if (aCurrentIndex == -1) {
                // Not present in text
                if (mustExist) {
                    throw new NoSuchElementException("Could not find termination sequence/character in string: '" + aValue.substring(startIndex));
                } else {
                    aCurrentIndex = aBoundIndex;
                    substringEnd = aCurrentIndex;
                    break;
                }
            }
            int cnt = 1;        // One, because we subtract it from aCurrentIndex. Just to avoid extra subtraction
            // Count how much backslashes there are -
            // Even number means that they all are escaped
            // Odd number means that the {@code c} is escaped
            while (aValue.charAt(aCurrentIndex - cnt) == '\\') {
                ++cnt;
            }

            // initially count was 1, not 0 - we do != comparison
            if (cnt % 2 != 0) {
                substringEnd = aCurrentIndex;
                aCurrentIndex += len;
                break;
            }

            // Otherwise we will find the same {@code c} at same position on next iteration
            ++aCurrentIndex;
        }

        return aValue.substring(startIndex, substringEnd);
    }

    /**
     * Create substring starting from next character while {@code condition} is true
     * Cursor is advanced to the first character which does not match condition
     *
     * @param condition condition to test each character with
     * @return substring of characters that matches condition
     */
    public String takeWhile(Predicate<Character> condition) {
        int startIndex = aCurrentIndex;
        while (hasNext()) {
            if (!condition.test(next())) {
                --aCurrentIndex;
                break;
            }
        }

        return aValue.substring(startIndex, aCurrentIndex);
    }

    /**
     * Returns last character that would be iterated over
     *
     * @return last character that would be iterated over
     */
    public char lastChar() {
        return aValue.charAt(aBoundIndex - 1);
    }

    /**
     * Move the bound until which iterator will iterate
     *
     * @param offset offset in respect to current bound
     */
    public void modifyBound(int offset) {
        aBoundIndex += offset;
    }

    /**
     * Return position of last symbol returned by next()
     *
     * @return index
     */
    public int prevPos() {
        return aCurrentIndex - 1;
    }

    public String substringToCurrPos(int pos) {
        return aValue.substring(pos, aCurrentIndex);
    }
}
