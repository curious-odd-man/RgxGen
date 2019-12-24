package com.github.curiousoddman.rgxgen.iterators;

import java.util.regex.Pattern;

public class NegativeStringIterator extends StringIterator {
    private final StringIterator aIterator;
    private final Pattern        aPattern;

    private String aValue;

    public NegativeStringIterator(StringIterator iterator, Pattern pattern) {
        aIterator = iterator;
        aPattern = pattern;
    }

    @Override
    protected String nextImpl() {
        do {
            aValue = aIterator.next();
        } while (aPattern.matcher(aValue)
                         .find());
        return aValue;
    }

    @Override
    public void reset() {
        // Nothing to do
    }

    @Override
    public String current() {
        return aValue;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public String toString() {
        return "NegativeStringIterator[" + aValue +
                '\'' + aPattern +
                "']{" + aIterator +
                "} ";
    }
}
