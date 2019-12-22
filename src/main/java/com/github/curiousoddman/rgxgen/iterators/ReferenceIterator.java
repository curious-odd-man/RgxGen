package com.github.curiousoddman.rgxgen.iterators;

public class ReferenceIterator extends StringIterator {
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
        return hasNext || !aOther.equals(aLast);
    }
}
