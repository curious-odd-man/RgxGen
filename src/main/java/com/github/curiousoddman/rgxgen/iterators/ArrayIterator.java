package com.github.curiousoddman.rgxgen.iterators;

import java.util.NoSuchElementException;

public class ArrayIterator extends StringIterator {

    private String[] aStrings;
    private int      aIndex = 0;

    public ArrayIterator(String[] strings) {
        aStrings = strings;
    }

    @Override
    public boolean hasNext() {
        return aIndex < aStrings.length;
    }

    @Override
    public String nextImpl() {
        try {
            return aStrings[aIndex++];
        } catch (ArrayIndexOutOfBoundsException ignore) {
            throw new NoSuchElementException("Not enough elements in arrays");
        }
    }
}
