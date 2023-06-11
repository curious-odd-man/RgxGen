package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import com.github.curiousoddman.rgxgen.nodes.SymbolSet;

import java.util.List;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.*;
import static java.util.Collections.emptyList;

public class SymbolSetTestUtils {

    public static SymbolSet ofAsciiDotPattern() {
        return SymbolSet.ofAsciiDotPattern();
    }

    public static SymbolSet ofAsciiCharacters(String pattern, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, emptyList(), symbols, type, ASCII_SYMBOL_RANGE);
    }

    public static SymbolSet ofAsciiRanges(String pattern, List<SymbolRange> symbolRanges, MatchType type) {
        return new SymbolSet(pattern, symbolRanges, ZERO_LENGTH_CHARACTER_ARRAY, type, ASCII_SYMBOL_RANGE);
    }

    public static SymbolSet ofUnicodeCharacterClass(String pattern, UnicodeCategory unicodeCategory, MatchType type) {
        return new SymbolSet(pattern, unicodeCategory.getSymbolRanges(), unicodeCategory.getSymbols(), type, UNICODE_SYMBOL_RANGE);
    }

    public static SymbolSet ofUnicode(String pattern, List<SymbolRange> symbolRanges, Character[] characters, MatchType matchType) {
        return new SymbolSet(pattern, symbolRanges, characters, matchType, UNICODE_SYMBOL_RANGE);
    }

    public static SymbolSet ofAscii(String pattern, List<SymbolRange> symbolRanges, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, symbolRanges, symbols, type, ASCII_SYMBOL_RANGE);
    }
}
