package com.github.curiousoddman.rgxgen.testutil;


import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class TestingUtilities {
    public static final BigInteger BIG_INTEGER_MINUS_ONE = BigInteger.valueOf(-1);

    public static <T> List<T> iteratorToList(Iterator<T> it) {
        List<T> lst = new LinkedList<>();

        while (it.hasNext()) {
            T next = it.next();
            lst.add(next);
        }

        return lst;
    }

}
