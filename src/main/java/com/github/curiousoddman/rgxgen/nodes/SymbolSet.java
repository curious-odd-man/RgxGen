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

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.config.model.RgxGenCharsDefinition;
import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import com.github.curiousoddman.rgxgen.util.Util;
import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;
import com.github.curiousoddman.rgxgen.visitors.helpers.SymbolSetIndexer;

import java.util.ArrayList;
import java.util.List;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Generate Any printable character.
 */

public class SymbolSet extends Node {
    private final   MatchType         originalMatchType;
    private final   boolean           isAscii;
    private final   List<SymbolRange> originalSymbolRanges;
    private final   List<Character>   originalSymbols;
    private final   SymbolRange       allCharactersRange;
    private final   List<SymbolRange> symbolRanges;
    private final   List<Character>   symbols;
    protected final RgxGenProperties  properties;

    private SymbolSetIndexer symbolSetIndexer;
    private SymbolSetIndexer caseInsensitiveSymbolSetIndexer;

    public static SymbolSet ofAsciiDotPattern(RgxGenProperties properties) {
        return new DotSymbolSet(properties);
    }

    public static SymbolSet ofAsciiCharacters(String pattern, RgxGenProperties properties, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, emptyList(), symbols, type, ASCII_SYMBOL_RANGE, properties);
    }

    public static SymbolSet ofAsciiRanges(String pattern, RgxGenProperties properties, List<SymbolRange> symbolRanges, MatchType type) {
        return new SymbolSet(pattern, symbolRanges, ZERO_LENGTH_CHARACTER_ARRAY, type, ASCII_SYMBOL_RANGE, properties);
    }

    public static SymbolSet ofUnicodeCharacterClass(String pattern, RgxGenProperties properties, UnicodeCategory unicodeCategory, MatchType type) {
        return new SymbolSet(pattern, unicodeCategory.getSymbolRanges(), unicodeCategory.getSymbols(), type, UNICODE_SYMBOL_RANGE, properties);
    }

    public static SymbolSet ofUnicode(String pattern, RgxGenProperties properties, List<SymbolRange> symbolRanges, Character[] characters, MatchType matchType) {
        return new SymbolSet(pattern, symbolRanges, characters, matchType, UNICODE_SYMBOL_RANGE, properties);
    }

    public static SymbolSet ofAscii(String pattern, RgxGenProperties properties, List<SymbolRange> symbolRanges, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, symbolRanges, symbols, type, ASCII_SYMBOL_RANGE, properties);
    }

    public static class DotSymbolSet extends SymbolSet {

        public DotSymbolSet(RgxGenProperties properties) {
            super(".", emptyList(), ZERO_LENGTH_CHARACTER_ARRAY, MatchType.POSITIVE, SymbolRange.range(0, 0), properties);
        }

        @Override
        public List<SymbolRange> getSymbolRanges() {
            RgxGenCharsDefinition charsDefinition = RgxGenOption.DOT_MATCHES_ONLY.getFromProperties(properties);
            if (charsDefinition != null) {
                return charsDefinition.getRangeList();
            } else {
                return singletonList(ASCII_SYMBOL_RANGE);
            }
        }

        @Override
        public List<Character> getSymbols() {
            RgxGenCharsDefinition charsDefinition = RgxGenOption.DOT_MATCHES_ONLY.getFromProperties(properties);
            if (charsDefinition != null) {
                return charsDefinition.getCharacters();
            } else {
                return emptyList();
            }
        }
    }

    /**
     * Create SymbolSet from ranges and symbols according to type
     *
     * @param pattern      original pattern for the reference
     * @param symbolRanges ranges of symbols to include/exclude
     * @param symbols      symbols to include/exclude
     * @param type         POSITIVE - include, NEGATIVE - exclude
     */
    public SymbolSet(String pattern, List<SymbolRange> symbolRanges, Character[] symbols, MatchType type, SymbolRange allCharactersRange, RgxGenProperties properties) {
        super(pattern);

        isAscii = allCharactersRange == ASCII_SYMBOL_RANGE;
        originalSymbolRanges = symbolRanges;
        originalSymbols = asList(symbols);
        this.allCharactersRange = allCharactersRange;

        List<SymbolRange> compactedRanges = new ArrayList<>(originalSymbolRanges.size());
        List<Character> compactedCharacters = new ArrayList<>(originalSymbols.size());
        Util.compactOverlappingRangesAndSymbols(originalSymbolRanges, originalSymbols, compactedRanges, compactedCharacters);
        if (type == MatchType.POSITIVE) {
            this.symbolRanges = compactedRanges;
            this.symbols = compactedCharacters;
        } else {
            this.symbolRanges = new ArrayList<>();
            this.symbols = new ArrayList<>();
            Util.invertSymbolsAndRanges(compactedRanges, compactedCharacters, allCharactersRange, this.symbolRanges, this.symbols);
        }
        originalMatchType = type;
        this.properties = properties;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public SymbolSetIndexer getSymbolSetIndexer() {
        if (symbolSetIndexer == null) {
            symbolSetIndexer = new SymbolSetIndexer(this);
        }
        return symbolSetIndexer;
    }

    public SymbolSetIndexer getCaseInsensitiveSymbolSetIndexer() {
        if (caseInsensitiveSymbolSetIndexer == null) {
            List<Character> caseInsensitiveSymbols = new ArrayList<>(originalSymbols);
            for (Character c : originalSymbols) {
                addIfChangedCase(caseInsensitiveSymbols, c);
            }
            for (SymbolRange originalSymbolRange : originalSymbolRanges) {
                for (char c = (char) originalSymbolRange.getFrom(); c <= originalSymbolRange.getTo(); ++c) {
                    addIfChangedCase(caseInsensitiveSymbols, c);
                }
            }
            caseInsensitiveSymbolSetIndexer = new SymbolSetIndexer(
                    new SymbolSet(getPattern(), originalSymbolRanges, caseInsensitiveSymbols.toArray(ZERO_LENGTH_CHARACTER_ARRAY), originalMatchType, allCharactersRange, properties)
            );
        }
        return caseInsensitiveSymbolSetIndexer;
    }

    private static void addIfChangedCase(List<Character> caseInsensitiveSymbols, char c) {
        if (Character.isUpperCase(c)) {
            caseInsensitiveSymbols.add(Character.toLowerCase(c));
        } else if (Character.isLowerCase(c)) {
            caseInsensitiveSymbols.add(Character.toUpperCase(c));
        }
    }

    public boolean isAscii() {
        return isAscii;
    }

    public List<SymbolRange> getSymbolRanges() {
        return symbolRanges;
    }

    public List<Character> getSymbols() {
        return symbols;
    }

    @Override
    public String toString() {
        return "SymbolSet{" +
                "originalMatchType=" + originalMatchType +
                ", isAscii=" + isAscii +
                ", symbolRanges=" + symbolRanges +
                ", symbols=" + symbols +
                '}';
    }
}
