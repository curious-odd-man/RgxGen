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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Class incorporates functionality to iterate String char by char
 */
public class CharIterator implements Iterator<Character> {

    private final String aValue;

    private int aLastIndex;
    private int aCurrentIndex = 0;

    public CharIterator(String value) {
        aValue = value;
        aLastIndex = aValue.length();
    }

    @Override
    public boolean hasNext() {
        return aCurrentIndex < aLastIndex;
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
        return aValue.charAt(aCurrentIndex + offset);
    }

    /**
     * Returns next character and advances the cursor
     */
    @Override
    public Character next() {
        try {
            return aValue.charAt(aCurrentIndex++);
        } catch (StringIndexOutOfBoundsException e) {
            final NoSuchElementException noSuchElementException = new NoSuchElementException("");
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
        final String substring = aValue.substring(aCurrentIndex, aCurrentIndex + length);
        aCurrentIndex += length;
        return substring;
    }

    /**
     * Returns context of cursor (text around the cursor)
     *
     * @return substring [-5,+5) chars from current position
     */
    public String context() {
        int start = Math.max(0, aCurrentIndex - 5);
        int end = Math.min(aLastIndex, aCurrentIndex + 5);
        return aValue.substring(start, end) + " at " + aCurrentIndex;
    }

    /**
     * Calculate number of characters remaining to iterate over
     *
     * @return num of characters
     */
    public int remaining() {
        return aLastIndex - aCurrentIndex;
    }

    /**
     * Returns substring from next character up to next not escaped character {@code c}
     * Cursor is advanced to a position of character {@code c}
     *
     * @param c character to search for
     * @return substring from next character up to next not escaped character {@code c}
     * @throws RuntimeException if no such character present after next character
     */
    public String nextUntil(char c) {
        int startIndex = aCurrentIndex;
        while (true) {
            // Find ending character
            aCurrentIndex = aValue.indexOf(c, aCurrentIndex);
            if (aCurrentIndex == -1) {
                throw new RuntimeException("Could not find character '" + c + "' in string: '" + aValue.substring(startIndex));
            }
            int cnt = 1;        // One, because we subtract it later from endIndex. Just to avoid extra subtraction
            // Count how much backslashes there are -
            // Even number means that they all are escaped
            // Odd number means that the {@code c} is escaped
            while (aValue.charAt(aCurrentIndex - cnt) == '\\') {
                ++cnt;
            }

            // because count was 1, not 0 initially we do not equal comparison
            if (cnt % 2 != 0) {
                break;
            }

            // Otherwise we will find the same {@code c} at same position on next iteration
            ++aCurrentIndex;
        }

        return aValue.substring(startIndex, aCurrentIndex);
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
     * @return
     */
    public char last() {
        return aValue.charAt(aLastIndex - 1);
    }

    /**
     * Move the bound until which iterator will iterate
     *
     * @param offset offset in respect to current bound
     */
    public void setBound(int offset) {
        aLastIndex += offset;
    }

}
