package com.github.curiousoddman.rgxgen.nodes;

/* **************************************************************************
   Copyright 2019 Vladislavs Varslavans

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
/* **************************************************************************/

import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import com.github.curiousoddman.rgxgen.util.Util;
import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;

import java.util.*;
import java.util.function.Consumer;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.ASCII_SYMBOL_RANGE;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.UNICODE_SYMBOL_RANGE;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.ZERO_LENGTH_CHARACTER_ARRAY;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Generate Any printable character.
 */
public class SymbolSet extends Node {
    private final MatchType         aType;
    private final List<SymbolRange> symbolRanges;
    private final List<Character>   symbols;

    public static SymbolSet ofAsciiDotPattern() {
        return ofAscii(".", singletonList(ASCII_SYMBOL_RANGE), ZERO_LENGTH_CHARACTER_ARRAY, MatchType.POSITIVE);
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

    public static SymbolSet ofUnicodeCharacterClass(String pattern, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, emptyList(), symbols, type, UNICODE_SYMBOL_RANGE);
    }

    public static SymbolSet ofAscii(String pattern, List<SymbolRange> symbolRanges, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, symbolRanges, symbols, type, ASCII_SYMBOL_RANGE);
    }

    /**
     * Create SymbolSet from ranges and symbols according to type
     *
     * @param pattern      original pattern for the reference
     * @param symbolRanges ranges of symbols to include/exclude
     * @param symbols      symbols to include/exclude
     * @param type         POSITIVE - include, NEGATIVE - exclude
     */
    private SymbolSet(String pattern, List<SymbolRange> symbolRanges, Character[] symbols, MatchType type, SymbolRange allCharactersRange) {
        super(pattern);
        aType = type;

        if (aType == MatchType.POSITIVE) {
            this.symbolRanges = symbolRanges;
            this.symbols = Arrays.asList(symbols);
        } else {
            this.symbolRanges = new ArrayList<>();
            this.symbols = new ArrayList<>();
            Util.invertSymbolsAndRanges(symbolRanges, symbols, allCharactersRange, this.symbolRanges, this.symbols);
        }
    }

    private static void handleCaseSensitiveCharacters(Iterable<Character> symbols, Consumer<Character> consumer) {
        for (Character c : symbols) {
            if (Character.isUpperCase(c)) {
                consumer.accept(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                consumer.accept(Character.toUpperCase(c));
            }
            consumer.accept(c);
        }
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "SymbolSet{" + '}';
    }       // FIXME
}
