package com.github.curiousoddman.rgxgen.iterators;

import java.util.Iterator;
import java.util.Observable;

public abstract class StringIterator extends Observable implements Iterator<String> {
    private String aCurrent;

    @SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
    @Override
    public String next() {
        aCurrent = nextImpl();
        setChanged();
        notifyObservers(aCurrent);
        return aCurrent;
    }

    protected abstract String nextImpl();

    public abstract void reset();

    public String current() {
        return aCurrent;
    }
}
