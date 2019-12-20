package com.github.curiousoddman.rgxgen.iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PermutationsIterator extends StringIterator {
    private final StringIterator[] aIterators;
    // FIXME: Generated parts are no longer used since StringIterator has current() method.
    private final String[]         aGeneratedParts;

    public PermutationsIterator(List<Supplier<StringIterator>> iteratorsSuppliers) {
        aIterators = new StringIterator[iteratorsSuppliers.size()];

        aGeneratedParts = new String[aIterators.length];

        for (int i = 0; i < aIterators.length; i++) {
            StringIterator iterator = iteratorsSuppliers.get(i)
                                                        .get();
            aIterators[i] = iterator;
        }

        // Make sure it is null, because it's used for check later
        aGeneratedParts[0] = null;
    }

    public PermutationsIterator(int length, String[] values) {
        this(IntStream.range(0, length)
                      .mapToObj(i -> ((Supplier<StringIterator>) () -> new ArrayIterator(values)))
                      .collect(Collectors.toList()));
    }

    @Override
    public boolean hasNext() {
        return aGeneratedParts[0] == null || Arrays.stream(aIterators)
                                                   .anyMatch(Iterator::hasNext);
    }

    @Override
    public String nextImpl() {
        // Initialize all value
        if (aGeneratedParts[0] == null) {
            for (int i = 0; i < aGeneratedParts.length; i++) {
                aGeneratedParts[i] = aIterators[i].next();
            }
        } else {
            // Advance one of iterators
            for (int i = aGeneratedParts.length - 1; i >= 0; --i) {
                if (aIterators[i].hasNext()) {
                    aGeneratedParts[i] = aIterators[i].next();
                    break;
                } else if (i == 0) {
                    // We can only reset other iterators. Head iterator should use all it's values only once
                    throw new NoSuchElementException("No more unique values");
                } else {
                    aIterators[i].reset();
                    aGeneratedParts[i] = aIterators[i].next();
                }
            }
        }

        return Arrays.stream(aIterators)
                     .map(StringIterator::current)
                     .reduce("", String::concat);
    }

    @Override
    public void reset() {
        aGeneratedParts[0] = null;
        for (StringIterator iterator : aIterators) {
            iterator.reset();
        }
    }

    @Override
    public String toString() {
        return "PermutationsIterator{" +
                ", aIterators=" + Arrays.toString(aIterators) +
                ", aGeneratedParts=" + Arrays.toString(aGeneratedParts) +
                '}';
    }
}
