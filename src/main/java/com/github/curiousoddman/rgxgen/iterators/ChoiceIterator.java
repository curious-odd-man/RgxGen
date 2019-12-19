package com.github.curiousoddman.rgxgen.iterators;

import java.util.Collection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class ChoiceIterator extends StringIterator {
    private final StringIterator[] aIterators;

    private int aCurrentIteratorIndex = 0;

    public ChoiceIterator(List<List<Supplier<StringIterator>>> iterators) {
        aIterators = iterators.stream()
                              .flatMap(Collection::stream)
                              .map(Supplier::get)
                              .toArray(StringIterator[]::new);
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
