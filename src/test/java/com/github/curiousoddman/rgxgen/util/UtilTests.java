package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;
import static com.github.curiousoddman.rgxgen.util.Util.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class UtilTests {
    @Test
    public void multiplicateTest() {
        assertEquals("", repeatChar(' ', -1));
        assertEquals("   ", repeatChar(' ', 3));
        assertEquals("XX", repeatChar('X', 2));
    }

    @Test
    public void randomChangeCaseTest() {
        boolean lower = false;
        boolean upper = false;
        for (int i = 0; i < 100; i++) {
            char result = randomlyChangeCase(TestingUtilities.newRandom(i), "a")
                    .charAt(0);
            if (Character.isLowerCase(result)) {
                lower = true;
            } else if (Character.isUpperCase(result)) {
                upper = true;
            }

            if (lower && upper) {
                break;
            }
        }

        assertTrue(lower && upper, "Generated both - upper and lower case letter");
    }

    @Test
    public void randomChangeCaseDigitTest() {
        boolean lower = false;
        boolean upper = false;
        for (int i = 0; i < 100; i++) {
            char result = randomlyChangeCase(TestingUtilities.newRandom(i), "1")
                    .charAt(0);
            assertEquals('1', result);
            if (Character.isLowerCase(result)) {
                lower = true;
            } else if (Character.isUpperCase(result)) {
                upper = true;
            }

            if (lower && upper) {
                break;
            }
        }

        assertFalse(lower || upper, "Digit case did not change.");
    }

    @Test
    public void countCaseInsensitiveVariationsTest() {
        assertEquals(1, countCaseInsensitiveVariations("1234").intValue());
        assertEquals(2, countCaseInsensitiveVariations("a").intValue());
        assertEquals(2, countCaseInsensitiveVariations("a1").intValue());
        assertEquals(2, countCaseInsensitiveVariations("1a").intValue());
        assertEquals(4, countCaseInsensitiveVariations("ab").intValue());
        assertEquals(4, countCaseInsensitiveVariations("AB").intValue());
        assertEquals(8, countCaseInsensitiveVariations("abc").intValue());
    }

    @Test
    public void indexOfNextCaseSensitiveCharacterTest() {
        assertEquals(0, indexOfNextCaseSensitiveCharacter("a123", 0).getAsInt());
        assertEquals(-142536, indexOfNextCaseSensitiveCharacter("a123", 1).orElse(-142536));
        assertEquals(1, indexOfNextCaseSensitiveCharacter("1a123", 0).getAsInt());
        assertEquals(3, indexOfNextCaseSensitiveCharacter("123a", 0).getAsInt());
    }

    @Test
    void textVariationsTest() {

        Set<String> strings = makeVariations(
                asList("L", "Spacing_Combining_Mark"),
                '_',
                '-', ' '
        );

        Set<String> expected = new HashSet<>(asList("Spacing_Combining_Mark", "Spacing-Combining-Mark", "L", "Spacing Combining Mark"));
        assertEquals(expected, strings);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getInvertRangesAndCharactersTestData")
    void invertRangesAndCharactersTest(Args args) {
        List<SymbolRange> ranges = args.getRanges();
        List<Character> characters = args.getCharacters();
        SymbolRange allCharacters = args.getAllCharacters();
        List<SymbolRange> expectedRanges = args.getExpectRanges();
        List<Character> expectedCharacters = args.getExpectCharacters();

        List<SymbolRange> actualRanges = new ArrayList<>();
        List<Character> actualCharacters = new ArrayList<>();
        invertSymbolsAndRanges(ranges, characters, allCharacters, actualRanges, actualCharacters);
        assertEquals(expectedRanges, actualRanges);
        assertEquals(expectedCharacters, actualCharacters);
    }

    public static Stream<Args> getInvertRangesAndCharactersTestData() {
        // TODO: Test for overlapping ranges
        SymbolRange A_E = range('a', 'e');
        return Stream.of(
                args("Whole range").allCharacters(A_E).expectRange(A_E).build(),
                args("Char in the middle").character('c').allCharacters(A_E).expectRanges(asList(range('a', 'b'), range('d', 'e'))).build(),
                args("First Char").character('a').allCharacters(A_E).expectRange(range('b', 'e')).build(),
                args("Last Char").character('e').allCharacters(A_E).expectRange(range('a', 'd')).build(),
                args("Pre-First Char").character('b').allCharacters(A_E).expectRange(range('c', 'e')).expectCharacter('a').build(),
                args("Pre-Last Char").character('d').allCharacters(A_E).expectRange(range('a', 'c')).expectCharacter('e').build(),

                args("Range in the middle").range(range('b', 'd')).allCharacters(A_E).expectCharacters(asList('a', 'e')).build(),
                args("First Range").range(range('a', 'b')).allCharacters(A_E).expectRange(range('c', 'e')).build(),
                args("Last Range").range(range('c', 'e')).allCharacters(A_E).expectRange(range('a', 'b')).build(),
                args("Pre-First Range").range(range('c', 'd')).allCharacters(A_E).expectRange(range('a', 'b')).expectCharacter('e').build(),
                args("Pre-Last Range").range(range('b', 'c')).allCharacters(A_E).expectRange(range('d', 'e')).expectCharacter('a').build()
        );
    }

    private static Args.ArgsBuilder args(String description) {
        return Args.builder().description(description);
    }

    @Getter
    @Builder
    private static class Args {
        String description;
        @Singular
        List<SymbolRange> ranges;
        @Singular
        List<Character>   characters;
        SymbolRange allCharacters;
        @Singular
        List<SymbolRange> expectRanges;
        @Singular
        List<Character>   expectCharacters;

        @Override
        public String toString() {
            return description;
        }
    }

    public static final  int         REFERENCE_START = 10;
    public static final  int         REFERENCE_END   = 20;
    private static final SymbolRange RANGE_10_20     = range(REFERENCE_START, REFERENCE_END);

    public static Stream<Arguments> getRangeComparisonTestData() {
        int startBefore = 0;
        int startOnStart = REFERENCE_START;
        int startInRange = REFERENCE_START + 5;
        int startOnEnd = REFERENCE_END;
        int startAfterEnd = REFERENCE_END + 5;

        int endsBefore = 1;
        int endsOnStart = REFERENCE_START;
        int endsInRange = REFERENCE_START + 6;
        int endsOnEnd = REFERENCE_END;
        int endsAfterEnd = REFERENCE_END + 6;

        BiFunction<SymbolRange, SymbolRange, Boolean> isRightCanContinueLeft = Util::isRightCanContinueLeft;
        BiFunction<SymbolRange, SymbolRange, Boolean> isRightWithinLeft = Util::isRightWithinLeft;

        return Stream.of(
                arguments("isRightCanContinueLeft: starts before ends before", range(startBefore, endsBefore), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts before ends on start", range(startBefore, endsOnStart), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts before ends in range", range(startBefore, endsInRange), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts before ends on end", range(startBefore, endsOnEnd), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts before ends after range", range(startBefore, endsAfterEnd), isRightCanContinueLeft, false),

                arguments("isRightCanContinueLeft: starts on start ends on start", range(startOnStart, endsOnStart), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts on start ends in range", range(startOnStart, endsInRange), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts on start ends on end", range(startOnStart, endsOnEnd), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts on start ends after range", range(startOnStart, endsAfterEnd), isRightCanContinueLeft, true),

                arguments("isRightCanContinueLeft: starts in range ends in range", range(startInRange, endsInRange), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts in range ends on end", range(startInRange, endsOnEnd), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts in range ends after range", range(startInRange, endsAfterEnd), isRightCanContinueLeft, true),

                arguments("isRightCanContinueLeft: starts on end ends on end", range(startOnEnd, endsOnEnd), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts on end ends after range", range(startOnEnd, endsAfterEnd), isRightCanContinueLeft, true),

                arguments("isRightCanContinueLeft: starts right after end ends right after end", range(startOnEnd + 1, endsOnEnd + 1), isRightCanContinueLeft, true),
                arguments("isRightCanContinueLeft: starts right after end ends after range", range(startOnEnd + 1, endsAfterEnd), isRightCanContinueLeft, true),

                arguments("isRightCanContinueLeft: starts after end ends after range", range(startAfterEnd, endsAfterEnd), isRightCanContinueLeft, false),

                // ---------------------------------------------------------------------------------------------------------

                arguments("isRightWithinLeft: starts before ends before", range(startBefore, endsBefore), isRightWithinLeft, false),
                arguments("isRightWithinLeft: starts before ends on start", range(startBefore, endsOnStart), isRightWithinLeft, false),
                arguments("isRightWithinLeft: starts before ends in range", range(startBefore, endsInRange), isRightWithinLeft, false),
                arguments("isRightWithinLeft: starts before ends on end", range(startBefore, endsOnEnd), isRightWithinLeft, false),
                arguments("isRightWithinLeft: starts before ends after range", range(startBefore, endsAfterEnd), isRightWithinLeft, false),

                arguments("isRightWithinLeft: starts on start ends on start", range(startOnStart, endsOnStart), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts on start ends in range", range(startOnStart, endsInRange), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts on start ends on end", range(startOnStart, endsOnEnd), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts on start ends after range", range(startOnStart, endsAfterEnd), isRightWithinLeft, false),

                arguments("isRightWithinLeft: starts in range ends in range", range(startInRange, endsInRange), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts in range ends on end", range(startInRange, endsOnEnd), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts in range ends after range", range(startInRange, endsAfterEnd), isRightWithinLeft, false),

                arguments("isRightWithinLeft: starts on end ends on end", range(startOnEnd, endsOnEnd), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts on end ends after range", range(startOnEnd, endsAfterEnd), isRightWithinLeft, false),

                arguments("isRightWithinLeft: starts after end ends after range", range(startAfterEnd, endsAfterEnd), isRightWithinLeft, false)
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getRangeComparisonTestData")
    void rangesComparisonTests(String name, SymbolRange b, BiFunction<SymbolRange, SymbolRange, Boolean> func, boolean expected) {
        assertEquals(expected, func.apply(RANGE_10_20, b));
    }

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

