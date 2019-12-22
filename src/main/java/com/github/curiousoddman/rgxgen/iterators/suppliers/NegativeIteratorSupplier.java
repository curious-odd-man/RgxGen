package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.NegativeStringIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.function.Supplier;
import java.util.regex.Pattern;

public class NegativeIteratorSupplier implements Supplier<StringIterator> {
    private final Pattern                  aPattern;
    private final Supplier<StringIterator> aIteratorSupplier;

    public NegativeIteratorSupplier(Pattern pattern, Supplier<StringIterator> iteratorSupplier) {
        aPattern = pattern;
        aIteratorSupplier = iteratorSupplier;
    }


    @Override
    public StringIterator get() {
        return new NegativeStringIterator(aIteratorSupplier.get(), aPattern);
    }

    @Override
    public String toString() {
        return "NegativeIteratorSupplier{" +
                aPattern +
                ',' + aIteratorSupplier +
                '}';
    }
}
