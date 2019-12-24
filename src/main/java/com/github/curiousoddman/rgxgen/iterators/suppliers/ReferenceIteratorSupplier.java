package com.github.curiousoddman.rgxgen.iterators.suppliers;

import com.github.curiousoddman.rgxgen.iterators.ReferenceIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ReferenceIteratorSupplier implements Supplier<StringIterator> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceIteratorSupplier.class);

    private final Map<Integer, List<ReferenceIterator>> aReferenceIteratorMap;
    private final Map<Integer, StringIterator>          aGroupIteratorsMap;
    private final int                                   aIndex;

    public ReferenceIteratorSupplier(Map<Integer, List<ReferenceIterator>> referenceIteratorMap, Map<Integer, StringIterator> groupIteratorsMap, int index) {
        aReferenceIteratorMap = referenceIteratorMap;
        aGroupIteratorsMap = groupIteratorsMap;
        aIndex = index;
        LOGGER.trace("Creating idx {}\n\trefs: {}\n\tgrps: {}", index, referenceIteratorMap, groupIteratorsMap);
    }

    @Override
    public StringIterator get() {
        LOGGER.trace("Getting idx {}\n\trefs: {}\n\tgrps: {}", aIndex, aReferenceIteratorMap, aGroupIteratorsMap);
        ReferenceIterator referenceIterator = new ReferenceIterator();
        final StringIterator stringIterator = aGroupIteratorsMap.get(aIndex);
        if (stringIterator != null) {
            LOGGER.debug("GroupRef[{}] connecting to group {} ", aIndex, stringIterator);
            referenceIterator.setOther(stringIterator);
        }

        LOGGER.debug("GroupRef[{}] adding to connection queue group ", aIndex);
        aReferenceIteratorMap.computeIfAbsent(aIndex, i -> new ArrayList<>())
                             .add(referenceIterator);

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
