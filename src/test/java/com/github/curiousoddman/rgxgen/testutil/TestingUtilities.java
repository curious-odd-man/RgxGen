package com.github.curiousoddman.rgxgen.testutil;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.MAX_UNICODE_CHARACTER;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.SPACE_ASCII_CODE;

public final class TestingUtilities {
    public static final BigInteger BIG_INTEGER_MINUS_ONE = BigInteger.valueOf(-1);

    public static <T> List<T> iteratorToList(Iterator<T> it) {
        List<T> lst = new ArrayList<>(100);

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

    public static Character[] makeUnicodeCharacterArray() {
        Character[] characters = new Character[MAX_UNICODE_CHARACTER - SPACE_ASCII_CODE];
        for (int i = SPACE_ASCII_CODE; i < MAX_UNICODE_CHARACTER; ++i) {
            characters[i - SPACE_ASCII_CODE] = (char) i;
        }
        return characters;
    }
}
