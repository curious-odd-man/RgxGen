package com.github.curiousoddman.rgxgen.iterators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReferenceIterator extends StringIterator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceIterator.class);

    private StringIterator aOther  = null;
    private boolean        hasNext = true;
    private String         aLast   = null;

    public void setOther(StringIterator other) {
        aOther = other;
    }

    @Override
    protected String nextImpl() {
        hasNext = false;
        return aOther.current();
    }

    @Override
    public String current() {
        aLast = aOther.current();
        return aOther.current();
    }

    @Override
    public void reset() {
        hasNext = true;
    }

    @Override
    public boolean hasNext() {
        LOGGER.trace("hasNext = {}, aOther.current() = {}, aLast = {}", hasNext, aOther.current(), aLast);
        return hasNext || !aOther.current()
                                 .equals(aLast);
    }

    @Override
    public String toString() {
        return "ReferenceIterator[" +
                aOther +
                "]{" + aLast +
                "} ";
    }
}
