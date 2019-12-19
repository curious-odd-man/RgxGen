package com.github.curiousoddman.rgxgen.iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class IncrementalLengthIterator extends StringIterator {
    private final Supplier<StringIterator> aSupplier;
    private final int                      aMin;
    private final int                      aMax;

    private int              aCurrentLength;
    private StringIterator[] aCurrentIterators;
    private String[]         aGeneratedParts;

    public IncrementalLengthIterator(Supplier<StringIterator> supplier, int min, int max) {
        aSupplier = supplier;
        aMin = min;
        aCurrentLength = min;
        aMax = max;
        reset();
    }

    private boolean lengthCanGrow() {
        return aCurrentLength < aMax || aCurrentIterators.length < aCurrentLength || aMax < 0;
    }

    private boolean hasMoreForCurrentLength() {
        return Arrays.stream(aCurrentIterators)
                     .anyMatch(Iterator::hasNext);
    }

    @Override
    public boolean hasNext() {
        return lengthCanGrow() || hasMoreForCurrentLength();
    }

    private void extendIterators() {
        StringIterator[] tmp = new StringIterator[aCurrentLength];
        for (int i = 0; i < aCurrentIterators.length; i++) {
            tmp[i] = aCurrentIterators[i];
            tmp[i].reset();
        }
        tmp[aCurrentLength - 1] = aSupplier.get();
        aCurrentIterators = tmp;
        aGeneratedParts = new String[aCurrentLength];
        for (int i = 0; i < aCurrentLength; i++) {
            aGeneratedParts[i] = aCurrentIterators[i].next();
        }
    }

    @Override
    public String nextImpl() {
        if (aCurrentLength == 0) {
            ++aCurrentLength;
            return "";
        } else {
            if (aGeneratedParts == null) {
                extendIterators();
            } else {
                // Advance one of iterators
                for (int i = aGeneratedParts.length - 1; i >= 0; --i) {
                    if (aCurrentIterators[i].hasNext()) {
                        aGeneratedParts[i] = aCurrentIterators[i].next();
                        break;
                    } else if (i == 0) {
                        if (aCurrentLength < aMax || aMax < 0) {
                            ++aCurrentLength;
                            extendIterators();
                        } else {
                            // We can only increase length up to max
                            throw new NoSuchElementException("No more unique values");
                        }
                    } else {
                        aCurrentIterators[i].reset();
                        aGeneratedParts[i] = aCurrentIterators[i].next();
                    }
                }
            }

            return Arrays.stream(aGeneratedParts.clone())
                         .reduce("", String::concat);
        }
    }

    @Override
    public final void reset() {
        aCurrentLength = aMin;
        aGeneratedParts = null;
        aCurrentIterators = new StringIterator[aCurrentLength];
        for (int i = 0; i < aCurrentLength; i++) {
            aCurrentIterators[i] = aSupplier.get();
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
