package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.ArrayIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.Arrays;
import java.util.function.Supplier;

public class ArrayIteratorSupplier implements Supplier<StringIterator> {
    private final String[] aSymbolSet;

    public ArrayIteratorSupplier(String[] symbolSet) {
        aSymbolSet = symbolSet;
    }

    @Override
    public ArrayIterator get() {
        return new ArrayIterator(aSymbolSet);
    }

    @Override
    public String toString() {
        return "ArrayIteratorSupplier{" + Arrays.toString(aSymbolSet) + '}';
    }
}
