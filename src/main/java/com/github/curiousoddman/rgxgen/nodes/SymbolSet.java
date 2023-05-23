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
import com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider;
import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;

import java.util.*;
import java.util.function.Consumer;

import static com.github.curiousoddman.rgxgen.util.Util.ZERO_LENGTH_CHARACTER_ARRAY;

/**
 * Generate Any printable character.
 */
public class SymbolSet extends Node {

    private final Collection<Character> aInitial;
    private final Collection<Character> aModification;
    private final MatchType             aType;

    private Character[] aSymbols;
    private Character[] aSymbolsCaseInsensitive;


    public static SymbolSet ofAsciiDotPattern() {
        return ofAsciiCharacters(".", ConstantsProvider.makeAsciiCharacterArray(), MatchType.POSITIVE);
    }

    public static SymbolSet ofAsciiCharacters(String pattern, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, Collections.emptyList(), symbols, type, ConstantsProvider.makeAsciiCharacterArray());
    }

    public static SymbolSet ofAsciiRanges(String pattern, Collection<SymbolRange> symbolRanges, MatchType type) {
        return new SymbolSet(pattern, symbolRanges, ZERO_LENGTH_CHARACTER_ARRAY, type, ConstantsProvider.makeAsciiCharacterArray());
    }

    public static SymbolSet ofUnicodeCharacterClass(String pattern, UnicodeCategory unicodeCategory, MatchType type) {
        return new SymbolSet(pattern, unicodeCategory.getSymbolRanges(), unicodeCategory.getSymbols(), type, ConstantsProvider.makeUnicodeCharacterArray());
    }

    public static SymbolSet ofAscii(String pattern, Collection<SymbolRange> symbolRanges, Character[] symbols, MatchType type) {
        return new SymbolSet(pattern, symbolRanges, symbols, type, ConstantsProvider.makeAsciiCharacterArray());
    }

    /**
     * Create SymbolSet from ranges and symbols according to type
     *
     * @param pattern      original pattern for the reference
     * @param symbolRanges ranges of symbols to include/exclude
     * @param symbols      symbols to include/exclude
     * @param type         POSITIVE - include, NEGATIVE - exclude
     */
    private SymbolSet(String pattern, Collection<SymbolRange> symbolRanges, Character[] symbols, MatchType type, Character[] allAvailableCharacters) {
        super(pattern);
        aType = type;
        if (aType == MatchType.POSITIVE) {
            aInitial = new HashSet<>(allAvailableCharacters.length);
            aModification = new ArrayList<>(Arrays.asList(symbols));
        } else {
            aInitial = new HashSet<>(Arrays.asList(allAvailableCharacters));
            aModification = new HashSet<>(Arrays.asList(symbols));
        }

        symbolRanges.stream()
                    .map(SymbolSet::rangeToList)
                    .forEach(aModification::addAll);
    }

    private static Collection<Character> rangeToList(SymbolRange range) {
        Collection<Character> chars = new ArrayList<>(range.getTo() - range.getFrom() + 1);
        for (int i = range.getFrom(); i <= range.getTo(); i++) {
            chars.add((char) i);
        }

        return chars;
    }

    private Character[] getOrInitSymbols() {
        if (aSymbols == null) {
            if (aType == MatchType.POSITIVE) {
                aInitial.addAll(aModification);
            } else {
                aInitial.removeIf(aModification::contains);
            }
            aSymbols = aInitial.toArray(ZERO_LENGTH_CHARACTER_ARRAY);
        }
        return aSymbols;
    }

    private Character[] getOrInitCaseInsensitiveSymbols() {
        if (aSymbolsCaseInsensitive == null) {
            Set<Character> caseInsensitive = new HashSet<>(aInitial);
            if (aType == MatchType.POSITIVE) {
                handleCaseSensitiveCharacters(aModification, caseInsensitive::add);
            } else {
                handleCaseSensitiveCharacters(aModification, caseInsensitive::remove);
            }
            aSymbolsCaseInsensitive = caseInsensitive.toArray(ZERO_LENGTH_CHARACTER_ARRAY);
        }
        return aSymbolsCaseInsensitive;
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

    public Character[] getSymbols() {
        return getOrInitSymbols();
    }

    public Character[] getSymbolsCaseInsensitive() {
        return getOrInitCaseInsensitiveSymbols();
    }

    @Override
    public String toString() {
        return "SymbolSet{" + Arrays.toString(getOrInitSymbols()) + '}';
    }

    public boolean isEmpty() {
        return getOrInitSymbols().length == 0;
    }
}
