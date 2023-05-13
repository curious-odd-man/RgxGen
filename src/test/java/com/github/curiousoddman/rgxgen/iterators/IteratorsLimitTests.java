package com.github.curiousoddman.rgxgen.iterators;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class IteratorsLimitTests {

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("ArrayIterator", new ArrayIterator(new Character[]{'a'})),
                Arguments.of("CaseVariationIterator", new CaseVariationIterator("a")),
                Arguments.of("ChoiceIterator", new ChoiceIterator(new StringIterator[]{new SingleValueIterator("a"), new SingleValueIterator("a")})),
                Arguments.of("IncrementalLength", new IncrementalLengthIterator(() -> new SingleValueIterator("a"), 0, 1)),
                Arguments.of("PermutationIterator", new PermutationsIterator(Arrays.asList(() -> new SingleValueIterator("a"), () -> new SingleValueIterator("a")))),
                Arguments.of("SingleValueIterator", new SingleValueIterator("a"))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void whenDrainedAllElementsThrowsException(String aName, StringIterator aIterator) {
        while (aIterator.hasNext()) {
            aIterator.next();
        }

        assertThrows(NoSuchElementException.class, aIterator::next);
    }

}
