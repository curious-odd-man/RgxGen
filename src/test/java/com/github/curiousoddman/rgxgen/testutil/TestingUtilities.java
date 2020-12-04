package com.github.curiousoddman.rgxgen.testutil;


import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

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

    public static Character[] getAllDigits() {
        return IntStream.rangeClosed('0', '9')
                        .mapToObj(i -> (char) i)
                        .toArray(Character[]::new);
    }
}
