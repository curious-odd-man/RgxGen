package com.github.curiousoddman.rgxgen.iterators;

import java.util.Iterator;
import java.util.Objects;

public abstract class StringIterator implements Iterator<String> {

    private String aCurrent;

    @SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
    @Override
    public String next() {
        aCurrent = nextImpl();
        Objects.requireNonNull(aCurrent);
        return aCurrent;
    }

    protected abstract String nextImpl();

    public abstract void reset();

    public String current() {
        return aCurrent;
    }
}
