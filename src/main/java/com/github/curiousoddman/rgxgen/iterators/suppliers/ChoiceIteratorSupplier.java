package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.ChoiceIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ChoiceIteratorSupplier implements Supplier<StringIterator> {

    private final List<List<Supplier<StringIterator>>> aStringIteratorsSuppliers;

    public ChoiceIteratorSupplier(List<List<Supplier<StringIterator>>> suppliers) {
        aStringIteratorsSuppliers = suppliers;
    }

    @Override
    public StringIterator get() {
        final StringIterator[] stringIterators = aStringIteratorsSuppliers.stream()
                                                                          .flatMap(Collection::stream)
                                                                          .map(Supplier::get)
                                                                          .toArray(StringIterator[]::new);
        return new ChoiceIterator(stringIterators);
    }

    @Override
    public String toString() {
        return "ChoiceIteratorSupplier{" +
                aStringIteratorsSuppliers +
                '}';
    }
}
