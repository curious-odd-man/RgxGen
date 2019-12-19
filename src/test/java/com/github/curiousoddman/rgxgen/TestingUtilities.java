package com.github.curiousoddman.rgxgen;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class TestingUtilities {

    public static <T> List<T> iteratorToList(Iterator<T> it) {
        List<T> lst = new LinkedList<>();

        while (it.hasNext()) {
            lst.add(it.next());
        }

        return lst;
    }

}
