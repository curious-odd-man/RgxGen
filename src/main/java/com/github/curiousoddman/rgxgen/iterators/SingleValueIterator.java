package com.github.curiousoddman.rgxgen.iterators;

public class SingleValueIterator extends StringIterator {
    private final String aInitial;
    private String aCurrent;

    public SingleValueIterator() {
        aCurrent = "";
        aInitial = aCurrent;
    }

    public SingleValueIterator(String s) {
        aCurrent = s;
        aInitial = s;
    }

    @Override
    public boolean hasNext() {
        return aCurrent != null;
    }

    @Override
    public String nextImpl() {
        String tmp = aCurrent;
        aCurrent = null;
        return tmp;
    }

    @Override
    public void reset() {
        aCurrent = aInitial;
    }

    @Override
    public String toString() {
        return "SingleValueIterator{" +
                "aValue='" + aCurrent + '\'' +
                '}';
    }
}
