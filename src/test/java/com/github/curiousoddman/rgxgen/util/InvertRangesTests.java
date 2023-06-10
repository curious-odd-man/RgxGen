package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;
import static com.github.curiousoddman.rgxgen.util.Util.invertSymbolsAndRanges;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvertRangesTests {

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
        SymbolRange A_E = range('a', 'e');
        return Stream.of(
                args("Whole range").allCharacters(A_E).expectRange(A_E),
                args("Char in the middle").character('c').allCharacters(A_E).expectRange("a-b").expectRange("d-e"),
                args("First Char").character('a').allCharacters(A_E).expectRange("b-e"),
                args("Last Char").character('e').allCharacters(A_E).expectRange("a-d"),
                args("Pre-First Char").character('b').allCharacters(A_E).expectRange("c-e").expectCharacter('a'),
                args("Pre-Last Char").character('d').allCharacters(A_E).expectRange("a-c").expectCharacter('e'),

                args("Range in the middle").range("b-d").allCharacters(A_E).expectCharacters("ae"),
                args("First Range").range("a-b").allCharacters(A_E).expectRange("c-e"),
                args("Last Range").range("c-e").allCharacters(A_E).expectRange("a-b"),
                args("Pre-First Range").range("c-d").allCharacters(A_E).expectRange("a-b").expectCharacter('e'),
                args("Pre-Last Range").range("b-c").allCharacters(A_E).expectRange("d-e").expectCharacter('a'),

                args("Range after range").allCharacters(A_E).range("a-c").range("x-z").expectRange("d-e"),
                args("Range before range").allCharacters(A_E).range("a-c").range("A-C").expectRange("d-e")
        );
    }

    private static Args args(String description) {
        return new Args(description);
    }

    @Getter
    @Setter
    public static class Args {
        String            description;
        List<SymbolRange> ranges           = new ArrayList<>();
        List<Character>   characters       = new ArrayList<>();
        SymbolRange       allCharacters;
        List<SymbolRange> expectRanges     = new ArrayList<>();
        List<Character>   expectCharacters = new ArrayList<>();

        public Args(String description) {
            this.description = description;
        }

        public Args allCharacters(SymbolRange allCharacters) {
            this.allCharacters = allCharacters;
            return this;
        }

        public Args range(String range) {
            ranges.add(getSymbolRange(range));
            return this;
        }

        private static SymbolRange getSymbolRange(String range) {
            String[] split = range.split("-");
            char from = split[0].charAt(0);
            char to = split[1].charAt(0);
            return SymbolRange.range(from, to);
        }

        public Args expectRange(SymbolRange expectRange) {
            expectRanges.add(expectRange);
            return this;
        }

        public Args expectRange(String expectRange) {
            expectRanges.add(getSymbolRange(expectRange));
            return this;
        }

        public Args expectCharacters(String characters) {
            characters.chars().forEach(c -> expectCharacters.add((char) c));
            return this;
        }

        public Args expectCharacter(char c) {
            expectCharacters.add(c);
            return this;
        }

        public Args character(char c) {
            characters.add(c);
            return this;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
