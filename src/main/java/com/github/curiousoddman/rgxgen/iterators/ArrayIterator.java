package com.github.curiousoddman.rgxgen.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator implements Iterator<String> {

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
    public String next() {
        try {
            return aStrings[aIndex++];
        } catch (ArrayIndexOutOfBoundsException ignore) {
            throw new NoSuchElementException("Not enough elements in arrays");
        }
    }
}
