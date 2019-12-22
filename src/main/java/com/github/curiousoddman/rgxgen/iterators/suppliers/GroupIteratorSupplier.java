package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.ReferenceIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GroupIteratorSupplier implements Supplier<StringIterator> {

    private final Supplier<StringIterator>              aIteratorSupplier;
    private final Map<Integer, List<ReferenceIterator>> aReferenceIteratorMap;
    private final Map<Integer, StringIterator>          aGroupIterators;
    private final int                                   aIndex;

    public GroupIteratorSupplier(Supplier<StringIterator> iteratorSupplier, Map<Integer, List<ReferenceIterator>> referenceIteratorMap, Map<Integer, StringIterator> groupIterators, int index) {
        aIteratorSupplier = iteratorSupplier;
        aReferenceIteratorMap = referenceIteratorMap;
        aGroupIterators = groupIterators;
        aIndex = index;
    }

    @Override
    public StringIterator get() {
        final StringIterator stringIterator = aIteratorSupplier.get();
        aGroupIterators.put(aIndex, stringIterator);
        final List<ReferenceIterator> orDefault = aReferenceIteratorMap.getOrDefault(aIndex, Collections.emptyList());
        for (ReferenceIterator referenceIterator : orDefault) {
            System.out.println("[B] Connection group " + aIndex);
            referenceIterator.setOther(stringIterator);
        }
        return stringIterator;
    }

    @Override
    public String toString() {
        return "GroupIteratorSupplier{" +
                "aIteratorSupplier=" + aIteratorSupplier +
                ", aReferenceIteratorMap=" + aReferenceIteratorMap +
                ", aGroupIterators=" + aGroupIterators +
                ", aIndex=" + aIndex +
                '}';
    }
}
