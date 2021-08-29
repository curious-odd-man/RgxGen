package com.github.curiousoddman.rgxgen.testutil;


import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public final class TestingUtilities {
    public static final BigInteger BIG_INTEGER_MINUS_ONE = BigInteger.valueOf(-1);

    public static <T> List<T> iteratorToList(Iterator<T> it) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, 0), false)
                            .collect(Collectors.toList());
    }

    public static Character[] getAllDigits() {
        return IntStream.rangeClosed('0', '9')
                        .mapToObj(i -> (char) i)
                        .toArray(Character[]::new);
    }

    /**
     * This method helps to overcome the issue that is described here: StrangeBehaviourTests::randomIsNotSoRandomTest
     *
     * @param seed seed value
     * @return new Random()
     */
    public static Random newRandom(int seed) {
        Random random = new Random(seed);
        random.nextInt();
        return random;
    }
}
