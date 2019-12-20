package com.github.curiousoddman.rgxgen.iterators;

import java.util.Observer;

public class ObserverIterator extends StringIterator {
    private String aLatest = null;

    private final Observer aObserver = (o, arg) -> {
        aLatest = (String) arg;
        next();
    };

    @Override
    protected String nextImpl() {
        return aLatest;
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean hasNext() {
        return aLatest == null || !aLatest.equals(current());
    }

    public Observer getObserver() {
        return aObserver;
    }
}
