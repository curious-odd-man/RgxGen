package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.ReferenceIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GroupIteratorSupplier implements Supplier<StringIterator> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupIteratorSupplier.class);

    private final Supplier<StringIterator>              aIteratorSupplier;
    private final Map<Integer, List<ReferenceIterator>> aReferenceIteratorMap;
    private final Map<Integer, StringIterator>          aGroupIterators;
    private final int                                   aIndex;

    public GroupIteratorSupplier(Supplier<StringIterator> iteratorSupplier, Map<Integer, List<ReferenceIterator>> referenceIteratorMap, Map<Integer, StringIterator> groupIterators, int index) {
        aIteratorSupplier = iteratorSupplier;
        aReferenceIteratorMap = referenceIteratorMap;
        aGroupIterators = groupIterators;
        aIndex = index;
        LOGGER.trace("Creating idx {}\n\tsrc: {}\n\trefs: {}\n\tgrps: {}", index, iteratorSupplier, referenceIteratorMap, groupIterators);
    }

    @Override
    public StringIterator get() {
        LOGGER.trace(".");
        final StringIterator stringIterator = aIteratorSupplier.get();
        aGroupIterators.put(aIndex, stringIterator);
        final List<ReferenceIterator> orDefault = aReferenceIteratorMap.getOrDefault(aIndex, Collections.emptyList());
        for (ReferenceIterator referenceIterator : orDefault) {
            LOGGER.debug("GroupRef[{}] connecting to group {} ", aIndex, stringIterator);
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
