package com.github.curiousoddman.rgxgen.iterators;

import java.util.regex.Pattern;

public class NegativeStringIterator extends StringIterator {
    private final StringIterator aIterator;
    private final Pattern        aPattern;

    public NegativeStringIterator(StringIterator iterator, Pattern pattern) {
        aIterator = iterator;
        aPattern = pattern;
    }

    @Override
    protected String nextImpl() {
        String value;
        do {
            value = aIterator.next();
        } while (aPattern.matcher(value)
                         .find());
        return value;
    }

    @Override
    public void reset() {
        // Nothing to do
    }

    @Override
    public boolean hasNext() {
        return true;
    }
}
