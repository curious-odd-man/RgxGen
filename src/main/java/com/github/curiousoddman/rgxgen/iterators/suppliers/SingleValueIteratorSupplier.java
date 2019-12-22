package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.SingleValueIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.function.Supplier;

public class SingleValueIteratorSupplier implements Supplier<StringIterator> {

    private final String aValue;

    public SingleValueIteratorSupplier(String value) {
        aValue = value;
    }

    @Override
    public StringIterator get() {
        return new SingleValueIterator(aValue);
    }

    @Override
    public String toString() {
        return "SingleValueIteratorSupplier{" + aValue + '}';
    }
}
