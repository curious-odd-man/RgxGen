package com.github.curiousoddman.rgxgen.parsing.dflt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

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

    public void move(int n) {
        aCurrentIndex += n;
    }

    public void move() {
        move(1);
    }


    public char peek() {
        return aValue.charAt(aCurrentIndex);
    }

    public char peek(int offset) {
        return aValue.charAt(aCurrentIndex + offset);
    }

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

    public String next(int length) {
        final String substring = aValue.substring(aCurrentIndex, aCurrentIndex + length);
        aCurrentIndex += length;
        return substring;
    }

    public String context() {
        int start = Math.max(0, aCurrentIndex - 5);
        int end = Math.min(aLastIndex, aCurrentIndex + 5);
        return aValue.substring(start, end) + " at " + aCurrentIndex;
    }

    public int remaining() {
        return aLastIndex - aCurrentIndex;
    }

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

            // Otherwise we will find the same {@code c} at same position
            ++aCurrentIndex;
        }

        return aValue.substring(startIndex, aCurrentIndex);
    }

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

    public char last() {
        return aValue.charAt(aLastIndex - 1);
    }

    public void setBound(int offset) {
        aLastIndex += offset;
    }

}
