package com.github.curiousoddman.rgxgen.iterators;

public class SingleValueIterator extends StringIterator {
    private final String aValue;

    private boolean hasNext;

    public SingleValueIterator() {
        this("");
    }

    public SingleValueIterator(String s) {
        aValue = s;
        hasNext = true;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public String nextImpl() {
        hasNext = false;
        return aValue;
    }

    @Override
    public void reset() {
        hasNext = true;
    }

    @Override
    public String current() {
        return aValue;
    }

    @Override
    public String toString() {
        return "SingleValueIterator{" +
                "aValue='" + aValue + '\'' +
                '}';
    }
}
