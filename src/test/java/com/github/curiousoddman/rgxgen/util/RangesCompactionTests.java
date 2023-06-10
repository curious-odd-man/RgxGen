package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;
import static com.github.curiousoddman.rgxgen.util.Util.compactOverlappingRangesAndSymbols;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class RangesCompactionTests {

    public static Stream<Arguments> getCompactionTestData() {
        return Stream.of(
                arguments("single range", ranges("a-z"), emptyList(), ranges("a-z"), emptyList()),
                arguments("single character", ranges(), chars("a"), emptyList(), chars("a")),
                // ranges tests
                arguments("non overlapping unordered ranges", ranges("x-z", "a-c"), emptyList(), ranges("a-c", "x-z"), emptyList()),
                arguments("start/end matching ranges", ranges("a-c", "c-e"), emptyList(), ranges("a-e"), emptyList()),
                arguments("start on next after end ranges", ranges("a-c", "d-e"), emptyList(), ranges("a-e"), emptyList()),
                arguments("partially overlapping ranges", ranges("a-c", "b-e"), emptyList(), ranges("a-e"), emptyList()),
                arguments("matching ranges", ranges("a-c", "a-c"), emptyList(), ranges("a-c"), emptyList()),
                arguments("one within another", ranges("a-e", "c-d"), emptyList(), ranges("a-e"), emptyList()),
                arguments("one within another - matching boundary", ranges("a-e", "b-e"), emptyList(), ranges("a-e"), emptyList()),
                // characters test
                arguments("independent characters", ranges(), chars("ac"), ranges(), chars("ac")),
                arguments("matching characters", ranges(), chars("aaaacaa"), ranges(), chars("ac")),
                arguments("continuous characters", ranges(), chars("abc"), ranges("a-c"), emptyList()),
                // mixed tests
                arguments("range that should consume character and another range", ranges("a-z", "c-d"), chars("fls"), ranges("a-z"), emptyList()),
                arguments("range and independent character", ranges("a-c"), chars("x"), ranges("a-c"), chars("x")),
                arguments("range that consumes character (within)", ranges("a-z"), chars("x"), ranges("a-z"), emptyList()),
                arguments("range that consumes character (on start edge)", ranges("a-c"), chars("a"), ranges("a-c"), emptyList()),
                arguments("range that consumes character (on end edge)", ranges("a-c"), chars("c"), ranges("a-c"), emptyList()),
                arguments("character follows range", ranges("a-c"), chars("d"), ranges("a-d"), emptyList()),
                arguments("range follows character", ranges("b-c"), chars("a"), ranges("a-c"), emptyList()),
                arguments("several characters makes up a range that is then joined to another range", ranges("b-d"), chars("aefg"), ranges("a-g"), emptyList()),
                arguments("a-cA-C + A,B,C", ranges("a-c", "A-C"), chars("ABCabc"), ranges("A-C", "a-c"), emptyList())
        );
    }

    @ParameterizedTest
    @MethodSource("getCompactionTestData")
    void compactOverlappingRangesAndSymbolsTest(String name,
                                                List<SymbolRange> originalSymbolRanges, List<Character> originalSymbols,
                                                List<SymbolRange> expectedCompactedRanges, List<Character> expectedCompactedSymbols) {
        List<SymbolRange> actualRanges = new ArrayList<>();
        List<Character> actualSymbols = new ArrayList<>();
        compactOverlappingRangesAndSymbols(originalSymbolRanges, originalSymbols, actualRanges, actualSymbols);

        assertEquals(expectedCompactedRanges, actualRanges);
        assertEquals(expectedCompactedSymbols, actualSymbols);
    }

    private static List<SymbolRange> ranges(String... texts) {
        return Arrays
                .stream(texts)
                .map(t -> t.split("-"))
                .map(s -> range(s[0].charAt(0), s[1].charAt(0)))
                .collect(Collectors.toList());
    }

    private static List<Character> chars(String text) {
        return text.chars().boxed().map(i -> (char) i.intValue()).collect(Collectors.toList());
    }

}
