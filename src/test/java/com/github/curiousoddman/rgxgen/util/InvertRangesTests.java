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

    public static Stream<Args> getInvertRangesAndCharactersTestData() {
        SymbolRange A_E = range('a', 'e');
        SymbolRange B_E = range('b', 'e');
        SymbolRange K_M = range('k', 'm');
        SymbolRange A_Z = range('a', 'z');
        return Stream.of(
                args("Whole range, when nothing gets excluded").allCharacters(A_E).expectRange(A_E),
                args("Char in the middle is excluded").character('c').allCharacters(A_E).expectRange("a-b").expectRange("d-e"),
                args("First Char is excluded").character('a').allCharacters(A_E).expectRange("b-e"),
                args("Last Char is excluded").character('e').allCharacters(A_E).expectRange("a-d"),
                args("Pre-First Char is excluded").character('b').allCharacters(A_E).expectRange("c-e").expectCharacter('a'),
                args("Pre-Last Char is excluded").character('d').allCharacters(A_E).expectRange("a-c").expectCharacter('e'),
                args("Char before allCharacters is ignored").allCharacters(B_E).character('a').expectRange(B_E),
                args("Char after allCharacters is ignored").allCharacters(B_E).character('f').expectRange(B_E),

                args("Range in the middle").range("b-d").allCharacters(A_E).expectCharacters("ae"),
                args("First Range").range("a-b").allCharacters(A_E).expectRange("c-e"),
                args("Last Range").range("c-e").allCharacters(A_E).expectRange("a-b"),
                args("Pre-First Range").range("c-d").allCharacters(A_E).expectRange("a-b").expectCharacter('e'),
                args("Pre-Last Range").range("b-c").allCharacters(A_E).expectRange("d-e").expectCharacter('a'),
                args("Range after range").allCharacters(K_M).range("x-z").expectRange(K_M),
                args("Range before range").allCharacters(K_M).range("a-c").range("A-C").expectRange(K_M),
                args("Range that starts before allCharacters 1").allCharacters(K_M).range("h-l").expectCharacter('m'),
                args("Range that starts before allCharacters 2").allCharacters(K_M).range("h-k").expectRange("l-m"),
                args("Range that ends after allCharacters 1").allCharacters(K_M).range("l-o").expectCharacter('k'),
                args("Range that ends after allCharacters 2").allCharacters(K_M).range("m-o").expectRange("k-l"),

                args("Adjacent ranges 1").allCharacters(A_Z).range("a-k").range("l-y").expectCharacter('z'),
                args("Adjacent ranges 2").allCharacters(A_Z).range("a-k").range("l-x").expectRange("y-z"),

                args("Adjacent characters").allCharacters(K_M).character('k').character('l').expectCharacter('m')
        );
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
