package com.github.curiousoddman.rgxgen.iterators;

import java.util.Iterator;

public abstract class StringIterator implements Iterator<String> {

    private String aCurrent;

    @SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
    @Override
    public String next() {
        aCurrent = nextImpl();
        return aCurrent;
    }

    protected abstract String nextImpl();

    public String current() {
        return aCurrent;
    }
}
