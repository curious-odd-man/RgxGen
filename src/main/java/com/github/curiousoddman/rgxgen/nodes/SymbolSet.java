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

import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;
import com.github.curiousoddman.rgxgen.util.Util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.curiousoddman.rgxgen.util.Util.ZERO_LENGTH_STRING_ARRAY;

/**
 * Generate Any printable character.
 */
public class SymbolSet extends Node {
    private static final int SPACE_ASCII_CODE = 32;     // First printable character in ASCII table
    private static final int DEL_ASCII_CODE   = 127;    // Bound for printable characters in ASCII table

    private static final String[] ALL_SYMBOLS = new String[DEL_ASCII_CODE - SPACE_ASCII_CODE];

    public static String[] getAllSymbols() {
        return ALL_SYMBOLS.clone();
    }

    static {
        for (int i = SPACE_ASCII_CODE; i < DEL_ASCII_CODE; ++i) {
            Character character = (char) i;
            ALL_SYMBOLS[i - SPACE_ASCII_CODE] = character.toString();
        }
    }

    /**
     * POSITIVE - add characters and ranges
     * NEGATIVE - all but characters and ranges
     */
    public enum TYPE {
        POSITIVE,
        NEGATIVE
    }

    /**
     * Range of symbols
     */
    public static class SymbolRange {
        public static final SymbolRange SMALL_LETTERS   = new SymbolSet.SymbolRange('a', 'z');
        public static final SymbolRange CAPITAL_LETTERS = new SymbolSet.SymbolRange('A', 'Z');
        public static final SymbolRange DIGITS          = new SymbolSet.SymbolRange('0', '9');

        private final int aFrom;
        private final int aTo;

        /**
         * Create range of symbols.
         *
         * @param from min character shall be less than {@code to}
         * @param to   max character, shall be greater than {@code from}
         * @apiNote No verifications are done!
         */
        public SymbolRange(char from, char to) {
            aFrom = from;
            aTo = to;
        }

        int getFrom() {
            return aFrom;
        }

        int getTo() {
            return aTo;
        }
    }

    private final String[] aSymbols;
    private final String[] aSymbolsCaseInsensitive;

    /**
     * Symbol set containing all symbols
     */
    public SymbolSet() {
        this(".", ALL_SYMBOLS.clone(), TYPE.POSITIVE);
    }

    public SymbolSet(String pattern, String[] symbols, TYPE type) {
        this(pattern, Collections.emptyList(), symbols, type);
    }

    public SymbolSet(String pattern, Collection<SymbolRange> symbolRanges, TYPE type) {
        this(pattern, symbolRanges, ZERO_LENGTH_STRING_ARRAY, type);
    }

    /**
     * Create SymbolSet from ranges and symbols according to type
     *
     * @param pattern      original pattern for the reference
     * @param symbolRanges ranges of symbols to include/exclude
     * @param symbols      symbols to include/exclude
     * @param type         POSITIVE - include, NEGATIVE - exclude
     */
    public SymbolSet(String pattern, Collection<SymbolRange> symbolRanges, String[] symbols, TYPE type) {
        super(pattern);
        Set<String> initial = type == TYPE.NEGATIVE
                              ? new HashSet<>(Arrays.asList(ALL_SYMBOLS))   // First we need to add all, later we remove unnecessary
                              : new HashSet<>(ALL_SYMBOLS.length);          // Most probably it will be enough.

        Set<String> caseInsensitive = new HashSet<>(initial);
        filterOrPut(initial, caseInsensitive, Arrays.asList(symbols), type);
        filterOrPut(initial, caseInsensitive, symbolRanges.stream()
                                                          .flatMapToInt(r -> IntStream.rangeClosed(r.getFrom(), r.getTo()))
                                                          .mapToObj(i -> (char) i)
                                                          .map(Object::toString)
                                                          .collect(Collectors.toList()), type);

        aSymbolsCaseInsensitive = caseInsensitive.toArray(ZERO_LENGTH_STRING_ARRAY);
        aSymbols = initial.toArray(ZERO_LENGTH_STRING_ARRAY);
    }

    /**
     * Depending on TYPE either add or remove characters
     *
     * @param initial collection to modify
     * @param symbols add or remove these symbols
     * @param type    add or remove
     */
    private static void filterOrPut(Collection<String> initial, Collection<String> caseInsensitive, List<String> symbols, TYPE type) {
        if (type == TYPE.POSITIVE) {
            initial.addAll(symbols);
            for (String s : symbols) {
                char stringAsChar = s.charAt(0);
                if (Character.isUpperCase(stringAsChar)) {
                    caseInsensitive.add(s.toLowerCase());
                } else if (Character.isLowerCase(stringAsChar)) {
                    caseInsensitive.add(s.toUpperCase());
                }
                caseInsensitive.add(s);
            }
        } else {
            initial.removeIf(symbols::contains);
            for (String s : symbols) {
                char stringAsChar = s.charAt(0);
                if (Character.isUpperCase(stringAsChar)) {
                    caseInsensitive.remove(s.toLowerCase());
                } else if (Character.isLowerCase(stringAsChar)) {
                    caseInsensitive.remove(s.toUpperCase());
                }
                caseInsensitive.remove(s);
            }
        }
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public String[] getSymbols() {
        return aSymbols;
    }

    public String[] getSymbolsCaseInsensitive() {
        return aSymbolsCaseInsensitive;
    }

    @Override
    public String toString() {
        return "SymbolSet{" + Arrays.toString(aSymbols) + '}';
    }

    public boolean isEmpty() {
        return aSymbols.length == 0;
    }
}