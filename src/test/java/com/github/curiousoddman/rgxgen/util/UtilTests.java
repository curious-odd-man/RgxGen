package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.ZERO_LENGTH_CHARACTER_ARRAY;
import static com.github.curiousoddman.rgxgen.util.Util.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

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
        invertSymbolsAndRanges(ranges, characters.toArray(ZERO_LENGTH_CHARACTER_ARRAY), allCharacters, actualRanges, actualCharacters);
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
}

