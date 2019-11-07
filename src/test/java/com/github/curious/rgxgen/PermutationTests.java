package com.github.curious.rgxgen;

import com.github.curious.rgxgen.util.Permutations;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PermutationTests {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("a"), 1, Arrays.asList("a")},
                {Arrays.asList("a"), 2, Arrays.asList("aa")},
                {Arrays.asList("a"), 60000, Arrays.asList(Stream.generate(() -> "a")
                                                                .limit(60000)
                                                                .reduce("", String::concat))},
                {Arrays.asList("z", "c"), 0, Arrays.asList("")},
                {Arrays.asList("x", "y"), 1, Arrays.asList("x", "y")},
                {Arrays.asList("c", "d"), 2, Arrays.asList("cc", "cd", "dc", "dd")},
                {Arrays.asList("a", "b"), 3, Arrays.asList("aaa", "aab", "aba", "abb", "baa", "bab", "bba", "bbb")},
                });
    }

    @Parameterized.Parameter
    public List<String> aValues;
    @Parameterized.Parameter(1)
    public int          aRepeateTimes;
    @Parameterized.Parameter(2)
    public List<String> aExpectedPermutations;

    @Test
    public void countTest() {
        Stream<String> resultStream = Permutations.withRepetitions(aRepeateTimes, aValues.toArray(new String[0]));
        assertEquals(aExpectedPermutations.size(), resultStream.count());
    }

    @Test
    public void valuesTest() {
        Stream<String> resultStream = Permutations.withRepetitions(aRepeateTimes, aValues.toArray(new String[0]));
        assertEquals(aExpectedPermutations, resultStream.collect(Collectors.toList()));
    }

}
