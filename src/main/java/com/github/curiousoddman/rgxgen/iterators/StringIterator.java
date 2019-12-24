package com.github.curiousoddman.rgxgen.iterators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public abstract class StringIterator implements Iterator<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringIterator.class);

    private String aCurrent;

    @SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
    @Override
    public String next() {
        aCurrent = nextImpl();
        LOGGER.trace("Produced value: '{}' using '{}'", aCurrent, this);
        return aCurrent;
    }

    protected abstract String nextImpl();

    public abstract void reset();

    public String current() {
        return aCurrent;
    }

    /*
        Expected :[aaaa, aabb,       abbb, baaa, babb,       bbbb]
        Actual   :[aaaa, aabb, abaa, abbb, baaa, babb, bbaa, bbbb]

     */
}
