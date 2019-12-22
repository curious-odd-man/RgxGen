package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.ReferenceIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ReferenceIteratorSupplier implements Supplier<StringIterator> {
    private final Map<Integer, List<ReferenceIterator>> aReferenceIteratorMap;
    private final Map<Integer, StringIterator>          aGroupIteratorsMap;
    private final int                                   aIndex;

    public ReferenceIteratorSupplier(Map<Integer, List<ReferenceIterator>> referenceIteratorMap, Map<Integer, StringIterator> groupIteratorsMap, int index) {
        aReferenceIteratorMap = referenceIteratorMap;
        aGroupIteratorsMap = groupIteratorsMap;
        aIndex = index;
    }

    @Override
    public StringIterator get() {
        ReferenceIterator referenceIterator = new ReferenceIterator();
        final StringIterator stringIterator = aGroupIteratorsMap.get(aIndex);
        if (stringIterator == null) {
            aReferenceIteratorMap.computeIfAbsent(aIndex, i -> new ArrayList<>())
                                 .add(referenceIterator);
        } else {
            referenceIterator.setOther(stringIterator);
        }
        return referenceIterator;
    }

    @Override
    public String toString() {
        return "ReferenceIteratorSupplier{" +
                "aReferenceIteratorMap=" + aReferenceIteratorMap +
                ", aGroupIteratorsMap=" + aGroupIteratorsMap +
                ", aIndex=" + aIndex +
                '}';
    }
}
