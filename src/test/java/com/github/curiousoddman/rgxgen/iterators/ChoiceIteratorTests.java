package com.github.curiousoddman.rgxgen.iterators;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ChoiceIteratorTests {

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of(
                        "(A|B)",
                        (Supplier<StringIterator[]>) () -> new StringIterator[]{
                                new SingleValueIterator("A"),
                                new SingleValueIterator("B")
                        },
                        Arrays.asList("A", "B")
                ),
                Arguments.of(
                        "(A|B|C|D|E|F)",
                        (Supplier<StringIterator[]>) () -> new StringIterator[]{
                                new SingleValueIterator("A"),
                                new SingleValueIterator("B"),
                                new SingleValueIterator("C"),
                                new SingleValueIterator("D"),
                                new SingleValueIterator("E"),
                                new SingleValueIterator("F")},
                        Arrays.asList("A", "B", "C", "D", "E", "F")
                )
        );
    }


    @ParameterizedTest
    @MethodSource("data")
    public void countTest(String aExpression,
                          Supplier<StringIterator[]> aIterators,
                          List<String> aExpectedValues) {
        StringIterator stringIterator = new ChoiceIterator(aIterators.get());
        Iterable<String> i = () -> stringIterator;

        Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
        assertEquals(aExpectedValues.size(), stream.count());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void valuesTest(String aExpression,
                           Supplier<StringIterator[]> aIterators,
                           List<String> aExpectedValues) {
        StringIterator stringIterator = new ChoiceIterator(aIterators.get());
        Iterable<String> i = () -> stringIterator;
        Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
        assertEquals(aExpectedValues, stream.collect(Collectors.toList()));
    }
}
