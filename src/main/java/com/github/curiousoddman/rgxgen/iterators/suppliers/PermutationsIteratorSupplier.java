package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.PermutationsIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.List;
import java.util.function.Supplier;

public class PermutationsIteratorSupplier implements Supplier<StringIterator> {

    private final List<Supplier<StringIterator>> aSuppliers;

    public PermutationsIteratorSupplier(List<Supplier<StringIterator>> suppliers) {
        aSuppliers = suppliers;
    }

    @Override
    public StringIterator get() {
        if (aSuppliers.size() == 1) {
            return aSuppliers.get(0)
                             .get();
        } else {
            return new PermutationsIterator(aSuppliers);
        }
    }

    @Override
    public String toString() {
        return "PermutationsIteratorSupplier{" +
                aSuppliers +
                '}';
    }
}
