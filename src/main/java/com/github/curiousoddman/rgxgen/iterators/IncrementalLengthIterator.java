package com.github.curiousoddman.rgxgen.iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class IncrementalLengthIterator extends StringIterator {
    private final Supplier<StringIterator> aSupplier;
    private final int                      aMax;

    private int                aCurrentLength;
    private Iterator<String>[] aCurrentIterators;
    private String[]           aGeneratedParts;

    public IncrementalLengthIterator(Supplier<StringIterator> supplier, int min, int max) {
        aSupplier = supplier;
        aCurrentLength = min;
        aMax = max;
    }

    private boolean lengthCanGrow() {
        return aCurrentLength < aMax || aMax < 0;
    }

    private boolean hasMoreForCurrentLength() {
        return aCurrentIterators == null || Arrays.stream(aCurrentIterators)
                                                  .anyMatch(Iterator::hasNext);
    }

    @Override
    public boolean hasNext() {
        return lengthCanGrow() || hasMoreForCurrentLength();
    }

    private void allocateNewLength() {
        aCurrentIterators = new Iterator[aCurrentLength];
        aGeneratedParts = new String[aCurrentLength];
        for (int i = 0; i < aCurrentLength; i++) {
            aCurrentIterators[i] = aSupplier.get();
            aGeneratedParts[i] = aCurrentIterators[i].next();
        }
    }

    @Override
    public String nextImpl() {
        if (aCurrentLength == 0) {
            ++aCurrentLength;
            return "";
        } else {
            if (aCurrentIterators == null) {
                allocateNewLength();
            } else {
                // Advance one of iterators
                for (int i = aGeneratedParts.length - 1; i >= 0; --i) {
                    if (aCurrentIterators[i].hasNext()) {
                        aGeneratedParts[i] = aCurrentIterators[i].next();
                        break;
                    } else if (i == 0) {
                        if (aCurrentLength < aMax || aMax < 0) {
                            ++aCurrentLength;
                            allocateNewLength();
                        } else {
                            // We can only increase length up to max
                            throw new NoSuchElementException("No more unique values");
                        }
                    } else {
                        Iterator<String> iterator = aSupplier.get();
                        aCurrentIterators[i] = iterator;
                        aGeneratedParts[i] = iterator.next();
                    }
                }
            }

            return Arrays.stream(aGeneratedParts.clone())
                         .reduce("", String::concat);
        }
    }

    @Override
    public String toString() {
        return "IncrementalLengthIterator{" +
                "aSupplier=" + aSupplier +
                ", aMax=" + aMax +
                ", aCurrentLength=" + aCurrentLength +
                ", aCurrentIterators=" + Arrays.toString(aCurrentIterators) +
                ", aGeneratedParts=" + Arrays.toString(aGeneratedParts) +
                '}';
    }
}
