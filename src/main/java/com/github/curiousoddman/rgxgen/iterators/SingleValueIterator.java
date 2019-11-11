package com.github.curiousoddman.rgxgen.iterators;

import java.util.Iterator;

public class SingleValueIterator implements Iterator<String> {
    private String aV;

    public SingleValueIterator(String v) {
        aV = v;
    }

    @Override
    public boolean hasNext() {
        return aV != null;
    }

    @Override
    public String next() {
        String tmp = aV;
        aV = null;
        return tmp;
    }

    @Override
    public String toString() {
        return "SingleValueIterator{" +
                "aValue='" + aV + '\'' +
                '}';
    }
}
