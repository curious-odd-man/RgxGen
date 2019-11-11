package com.github.curiousoddman.rgxgen.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ZeroLengthIterator implements Iterator<String> {
    private boolean aHasNext = true;

    @Override
    public boolean hasNext() {
        return aHasNext;
    }

    @Override
    public String next() {
        if (!aHasNext) {
            throw new NoSuchElementException("Zero length iterator has been exhausted.");
        }
        aHasNext = false;
        return "";
    }

    @Override
    public String toString() {
        return "ZeroLengthIterator{" +
                "aHasNext=" + aHasNext +
                '}';
    }
}
