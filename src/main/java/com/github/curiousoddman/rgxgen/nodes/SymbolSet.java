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
import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.RgxGenCharsDefinition;
import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import com.github.curiousoddman.rgxgen.util.Util;
import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;
import com.github.curiousoddman.rgxgen.visitors.helpers.SymbolSetIndexer;

import java.util.ArrayList;
import java.util.List;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Generate Any printable character.
 */

public class SymbolSet extends Node {
    private final   MatchType             originalMatchType;
    private final   RgxGenCharsDefinition positiveGenerationChars;
    private final   RgxGenCharsDefinition negativeMatchExclusion;
    private final   boolean           isAscii;
    protected final SymbolRange       universeCharacters;
    private final   List<SymbolRange> symbolRanges;
    private final   List<Character>       symbols;
    private         SymbolSetIndexer      symbolSetIndexer;
    private         SymbolSetIndexer      caseInsensitiveSymbolSetIndexer;

    public static SymbolSet ofDotPattern(RgxGenProperties properties) {
        RgxGenCharsDefinition charsDefinition = RgxGenOption.DOT_MATCHES_ONLY.getFromProperties(properties);
        if (charsDefinition != null) {
            boolean isAscii = charsDefinition.isAsciiOnly();
            if (isAscii) {
                return ofAscii(".", charsDefinition.getRangeList(), charsDefinition.getCharacters().toArray(ZERO_LENGTH_CHARACTER_ARRAY), MatchType.POSITIVE);
            } else {
                return ofUnicode(".", charsDefinition.getRangeList(), charsDefinition.getCharacters().toArray(ZERO_LENGTH_CHARACTER_ARRAY), MatchType.POSITIVE);
            }
        } else {
            return ofAscii(".", singletonList(ASCII_SYMBOL_RANGE), ZERO_LENGTH_CHARACTER_ARRAY, MatchType.POSITIVE);
        }
    }

    public static SymbolSet ofAsciiCharacters(String pattern, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, emptyList(), symbols, type, ASCII_SYMBOL_RANGE);
    }

    public static SymbolSet ofUnicodeCharacterClass(String pattern, UnicodeCategory unicodeCategory, MatchType type) {
        return new SymbolSet(pattern, unicodeCategory.getSymbolRanges(), unicodeCategory.getSymbols(), type, UNICODE_SYMBOL_RANGE);
    }

    public static SymbolSet ofUnicode(String pattern, List<SymbolRange> symbolRanges, Character[] characters, MatchType matchType) {
        return new SymbolSet(pattern, symbolRanges, characters, matchType, UNICODE_SYMBOL_RANGE);
    }

    public static SymbolSet ofUnicode(String pattern, RgxGenCharsDefinition positiveMatchDefinitions, RgxGenCharsDefinition negativeMatchDefinitions, MatchType matchType) {
        return new SymbolSet(pattern, positiveMatchDefinitions, negativeMatchDefinitions, matchType, UNICODE_SYMBOL_RANGE);
    }

    public static SymbolSet ofAscii(String pattern, List<SymbolRange> symbolRanges, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, symbolRanges, symbols, type, ASCII_SYMBOL_RANGE);
    }

    public static SymbolSet ofAsciiRanges(String pattern, List<SymbolRange> symbolRanges, MatchType type) {
        return new SymbolSet(pattern, symbolRanges, ZERO_LENGTH_CHARACTER_ARRAY, type, ASCII_SYMBOL_RANGE);
    }

    public static SymbolSet ofAscii(String pattern, RgxGenCharsDefinition positiveMatchDefinitions, RgxGenCharsDefinition negativeMatchDefinitions, MatchType matchType) {
        return new SymbolSet(pattern, positiveMatchDefinitions, negativeMatchDefinitions, matchType, ASCII_SYMBOL_RANGE);
    }

    public SymbolSet(String pattern, List<SymbolRange> symbolRanges, Character[] symbols, MatchType type, SymbolRange universeCharacters) {
        this(pattern, RgxGenCharsDefinition.of(symbolRanges, symbols), null, type, universeCharacters);
    }

    /**
     * Create SymbolSet from ranges and symbols according to type
     *
     * @param pattern                 original pattern for the reference
     * @param positiveGenerationChars characters to generate when {@code type} is POSITIVE
     * @param negativeMatchExclusion  characters to exclude when {@code type} is NEGATIVE; null = use same as {@code positiveGenerationChars}
     * @param type                    POSITIVE - include, NEGATIVE - exclude
     * @param universeCharacters      characters to exclude from when {@code type} is NEGATIVE
     */
    public SymbolSet(String pattern,
                     RgxGenCharsDefinition positiveGenerationChars, RgxGenCharsDefinition negativeMatchExclusion,
                     MatchType type, SymbolRange universeCharacters) {
        super(pattern);
        this.positiveGenerationChars = positiveGenerationChars;
        this.negativeMatchExclusion = negativeMatchExclusion;

        isAscii = universeCharacters == ASCII_SYMBOL_RANGE;
        this.universeCharacters = universeCharacters;

        if (type == MatchType.POSITIVE) {
            List<SymbolRange> compactedRanges = new ArrayList<>(positiveGenerationChars.getRangeList().size());
            List<Character> compactedCharacters = new ArrayList<>(positiveGenerationChars.getCharacters().size());
            Util.compactOverlappingRangesAndSymbols(positiveGenerationChars.getRangeList(), positiveGenerationChars.getCharacters(), compactedRanges, compactedCharacters);
            symbolRanges = compactedRanges;
            symbols = compactedCharacters;
        } else {
            symbolRanges = new ArrayList<>();
            symbols = new ArrayList<>();
            RgxGenCharsDefinition defsToUse = negativeMatchExclusion == null ? positiveGenerationChars : negativeMatchExclusion;
            List<SymbolRange> compactedRanges = new ArrayList<>(defsToUse.getRangeList().size());
            List<Character> compactedCharacters = new ArrayList<>(defsToUse.getCharacters().size());
            Util.compactOverlappingRangesAndSymbols(defsToUse.getRangeList(), defsToUse.getCharacters(), compactedRanges, compactedCharacters);
            Util.invertSymbolsAndRanges(compactedRanges, compactedCharacters, universeCharacters, symbolRanges, symbols);
        }
        originalMatchType = type;
    }

    public SymbolSet getInvertedNode() {
        if (isAscii) {
            return ofAscii("[^" + getPattern().substring(1), symbolRanges, symbols.toArray(ZERO_LENGTH_CHARACTER_ARRAY), MatchType.NEGATIVE);
        } else {
            return ofUnicode("[^" + getPattern().substring(1), symbolRanges, symbols.toArray(ZERO_LENGTH_CHARACTER_ARRAY), MatchType.NEGATIVE);
        }
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
            List<Character> caseInsensitiveSymbols = new ArrayList<>(positiveGenerationChars.getCharacters());
            for (Character c : positiveGenerationChars.getCharacters()) {
                addIfChangedCase(caseInsensitiveSymbols, c);
            }
            for (SymbolRange originalSymbolRange : positiveGenerationChars.getRangeList()) {
                for (char c = (char) originalSymbolRange.getFrom(); c <= originalSymbolRange.getTo(); ++c) {
                    addIfChangedCase(caseInsensitiveSymbols, c);
                }
            }
            caseInsensitiveSymbolSetIndexer = new SymbolSetIndexer(
                    new SymbolSet(getPattern(), positiveGenerationChars.getRangeList(), caseInsensitiveSymbols.toArray(ZERO_LENGTH_CHARACTER_ARRAY), originalMatchType, universeCharacters)
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
                ", positiveGenerationChars=" + positiveGenerationChars +
                ", negativeMatchExclusion=" + negativeMatchExclusion +
                ", isAscii=" + isAscii +
                ", symbolRanges=" + symbolRanges +
                ", symbols=" + symbols +
                "} ";
    }

    public boolean hasModifiedExclusionChars() {
        return negativeMatchExclusion != null;
    }

    public RgxGenCharsDefinition getNegativeMatchExclusionChars() {
        return negativeMatchExclusion;
    }
}
