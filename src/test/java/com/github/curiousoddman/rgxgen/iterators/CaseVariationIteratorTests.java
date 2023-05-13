package com.github.curiousoddman.rgxgen.iterators;

import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CaseVariationIteratorTests {
    public static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of("A", Arrays.asList("a", "A")),
                Arguments.of("a", Arrays.asList("a", "A")),
                Arguments.of("1", Arrays.asList("1")),
                Arguments.of("1a", Arrays.asList("1a", "1A")),
                Arguments.of("1ab", Arrays.asList("1ab", "1Ab", "1aB", "1AB")),
                Arguments.of("a1b", Arrays.asList("a1b", "A1b", "a1B", "A1B")),
                Arguments.of("A1B", Arrays.asList("a1b", "A1b", "a1B", "A1B")),
                Arguments.of("abc", Arrays.asList("abc", "Abc", "aBc", "ABc", "abC", "AbC", "aBC", "ABC")),
                Arguments.of("AbC", Arrays.asList("abc", "Abc", "aBc", "ABc", "abC", "AbC", "aBC", "ABC")),
                Arguments.of("ABC", Arrays.asList("abc", "Abc", "aBc", "ABc", "abC", "AbC", "aBC", "ABC"))
        );
    }


    @ParameterizedTest
    @MethodSource("parameters")
    public void test(String aInput, List<String> aExpected) {
        CaseVariationIterator caseVariationIterator = new CaseVariationIterator(aInput);
        List<String> strings = TestingUtilities.iteratorToList(caseVariationIterator);
        assertEquals(aExpected, strings);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testThrows(String aInput, List<String> aExpected) {
        CaseVariationIterator caseVariationIterator = new CaseVariationIterator(aInput);
        List<String> strings = TestingUtilities.iteratorToList(caseVariationIterator);
        assertFalse(caseVariationIterator.hasNext());
        assertThrows(NoSuchElementException.class, caseVariationIterator::next);
    }
}
