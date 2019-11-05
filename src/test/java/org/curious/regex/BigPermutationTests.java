package org.curious.regex;

import org.curious.regex.util.Permutations;
import org.junit.Test;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class BigPermutationTests {

    private static Stream<String[]> getInfStream(int length) {
        Supplier<Stream<String>> supplier = () -> Stream.generate(() -> "a");
        return Permutations.withRepetitions(length, supplier,
                                            String[]::new);
    }

    @Test
    public void infiniteValuesTest() {
//        String first = getInfStream(1).findFirst()
//                                       .get()[0];
//        assertEquals("a", first);
//        String[] strings = getInfStream(2).findFirst()
//                                          .get();
//        String second = strings[0] + strings[1];
//        assertEquals("aa", second);
    }
}
