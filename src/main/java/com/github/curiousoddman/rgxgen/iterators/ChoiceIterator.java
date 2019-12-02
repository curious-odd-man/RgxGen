package com.github.curiousoddman.rgxgen.iterators;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ChoiceIterator extends StringIterator {
    private final List<Supplier<StringIterator>> aIterators;

    private StringIterator aCurrentIterator;

    public ChoiceIterator(List<List<Supplier<StringIterator>>> iterators) {
        aIterators = iterators.stream()
                              .flatMap(Collection::stream)
                              .collect(Collectors.toList());
        aCurrentIterator = aIterators.remove(0)
                                     .get();
    }

    @Override
    public boolean hasNext() {
        return aCurrentIterator.hasNext() || !aIterators.isEmpty();
    }

    @Override
    public String nextImpl() {
        if (!aCurrentIterator.hasNext()) {
            if (aIterators.isEmpty()) {
                throw new NoSuchElementException("No more values");
            } else {
                aCurrentIterator = aIterators.remove(0)
                                             .get();
            }
        }

        return aCurrentIterator.next();
    }

    @Override
    public String toString() {
        return "ChoiceIterator{" +
                "aIterators=" + aIterators +
                ", aCurrentIterator=" + aCurrentIterator +
                '}';
    }
}
