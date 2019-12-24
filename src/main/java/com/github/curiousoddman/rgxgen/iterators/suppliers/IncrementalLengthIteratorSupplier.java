package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.IncrementalLengthIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.function.Supplier;

public class IncrementalLengthIteratorSupplier implements Supplier<StringIterator> {

    private final int                      aMin;
    private final int                      aMax;
    private final Supplier<StringIterator> aIteratorSupplier;

    public IncrementalLengthIteratorSupplier(Supplier<StringIterator> iteratorSupplier, int min, int max) {
        aMin = min;
        aMax = max;
        aIteratorSupplier = iteratorSupplier;
    }


    @Override
    public StringIterator get() {
        return new IncrementalLengthIterator(aIteratorSupplier, aMin, aMax);
    }

    @Override
    public String toString() {
        return "IncrementalLengthIteratorSupplier[" + aMin +
                ':' + aMax +
                "]{" + aIteratorSupplier +
                '}';
    }
}
