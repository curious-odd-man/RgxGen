package com.github.curiousoddman.rgxgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class TestingUtilities {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestingUtilities.class);

    public static <T> List<T> iteratorToList(Iterator<T> it) {
        List<T> lst = new LinkedList<>();

        while (it.hasNext()) {
            final T next = it.next();
            LOGGER.trace("{}", next);
            lst.add(next);
        }

        return lst;
    }

}
