package com.github.curiousoddman.rgxgen.iterators;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class ChoiceIterator extends StringIterator {
    private final StringIterator[] aIterators;

    private int aCurrentIteratorIndex = 0;

    public ChoiceIterator(StringIterator[] iterators) {
        aIterators = iterators;
    }

    @Override
    public boolean hasNext() {
        return aIterators[aCurrentIteratorIndex].hasNext() || aCurrentIteratorIndex + 1 < aIterators.length;
    }

    @Override
    public String nextImpl() {
        if (!aIterators[aCurrentIteratorIndex].hasNext()) {
            ++aCurrentIteratorIndex;
            if (aCurrentIteratorIndex >= aIterators.length) {
                throw new NoSuchElementException("No more values");
            }
        }

        return aIterators[aCurrentIteratorIndex].next();
    }

    @Override
    public String current() {
        return aIterators[aCurrentIteratorIndex].current();
    }

    @Override
    public void reset() {
        aCurrentIteratorIndex = 0;
        for (StringIterator iterator : aIterators) {
            iterator.reset();
        }
    }

    @Override
    public String toString() {
        return "ChoiceIterator{" +
                "aIterators=" + Arrays.toString(aIterators) +
                ", aCurrentIteratorIndex=" + aCurrentIteratorIndex +
                '}';
    }
}
