package com.github.curiousoddman.rgxgen.iterators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public abstract class StringIterator implements Iterator<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringIterator.class);

    @SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
    @Override
    public String next() {
        String current = nextImpl();
        LOGGER.trace("Produced value: '{}' using '{}'", current, this);
        return current;
    }

    /**
     * This method returns correct value only on top level iterator.
     * For other iterators 2 steps are requied - next() and then current().
     *
     * @return
     */
    protected abstract String nextImpl();

    public abstract void reset();

    public abstract String current();
}
