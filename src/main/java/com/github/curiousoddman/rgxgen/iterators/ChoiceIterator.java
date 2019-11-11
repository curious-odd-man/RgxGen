package com.github.curiousoddman.rgxgen.iterators;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ChoiceIterator implements Iterator<String> {
    private final List<Supplier<Iterator<String>>> aIterators;

    private Iterator<String> aCurrentIterator;

    public ChoiceIterator(List<List<Supplier<Iterator<String>>>> iterators) {
        aIterators = iterators.stream()
                              .flatMap(v -> v.stream())
                              .collect(Collectors.toList());
        aCurrentIterator = aIterators.remove(0)
                                     .get();
    }

    @Override
    public boolean hasNext() {
        return aCurrentIterator.hasNext() || !aIterators.isEmpty();
    }

    @Override
    public String next() {
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
