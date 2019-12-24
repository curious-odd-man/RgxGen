package com.github.curiousoddman.rgxgen.iterators;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class ArrayIterator extends StringIterator {

    private final int      aMaxIndex;
    private final String[] aStrings;

    private int aIndex = -1;

    public ArrayIterator(String[] strings) {
        aStrings = strings;
        aMaxIndex = aStrings.length - 1;        // Because of prefix increment in nextImpl()
    }

    @Override
    public boolean hasNext() {
        return aIndex < aMaxIndex;
    }

    @Override
    public String nextImpl() {
        try {
            return aStrings[++aIndex];
        } catch (ArrayIndexOutOfBoundsException ignore) {
            throw new NoSuchElementException("Not enough elements in arrays");
        }
    }

    @Override
    public void reset() {
        aIndex = -1;
    }

    @Override
    public String current() {
        return aStrings[aIndex];
    }

    @Override
    public String toString() {
        return "ArrayIterator[" + aIndex + "]{" + Arrays.toString(aStrings) +
                '}';
    }
}
