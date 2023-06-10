package com.github.curiousoddman.rgxgen.util;

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

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider;
import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;

@UtilityClass
public final class Util {
    public static Character[] stringToChars(CharSequence str) {
        return str.chars()
                  .mapToObj(i -> (char) i)
                  .toArray(Character[]::new);
    }

    /**
     * Repeats text multiple times
     *
     * @param c     character to repeat
     * @param times number of times. Values less or equal to zero will result in empty string
     * @return text repeated multiple times
     */
    public static String repeatChar(char c, int times) {
        if (times < 0) {
            return "";
        }
        char[] result = new char[times];
        Arrays.fill(result, c);

        return new String(result);
    }

    /**
     * Randomly change case for the letters in a string
     *
     * @param rnd   random to be used
     * @param input input string to randomize
     * @return string with random characters changed case.
     */
    public static String randomlyChangeCase(Random rnd, CharSequence input) {
        StringBuilder sb = new StringBuilder(input);
        for (int i = 0; i < sb.length(); i++) {
            char currentChar = sb.charAt(i);
            if (Character.isUpperCase(currentChar) && rnd.nextBoolean()) {
                sb.setCharAt(i, Character.toLowerCase(currentChar));
            } else if (Character.isLowerCase(currentChar) && rnd.nextBoolean()) {
                sb.setCharAt(i, Character.toUpperCase(currentChar));
            }
        }

        return sb.toString();
    }

    /**
     * Count number of variation of words in case insensitive manner.
     * For example for word "a" - there are 2 variation ("a" and "A").
     * For word "1a" - there are also 2 variations ("1a" and "1A")
     * For word "AB" - there are 4 variations: ("ab", "aB", "Ab", "BB")
     *
     * @param value word to calculate variations
     * @return number of variations.
     */
    public static BigInteger countCaseInsensitiveVariations(CharSequence value) {
        int switchableCase = value.chars()
                                  .map(c -> Character.isUpperCase(c) || Character.isLowerCase(c) ? 1 : 0)
                                  .sum();
        return ConstantsProvider.BIG_INTEGER_TWO.pow(switchableCase);
    }

    /**
     * Finds next case sensitive character.
     * Case sensitive character is either lower-case or upper-case character.
     *
     * @param text       text to be analyzed
     * @param startIndex start search from index.
     * @return index of next case sensitive character or {@code empty} if no such character present
     */
    public static OptionalInt indexOfNextCaseSensitiveCharacter(CharSequence text, int startIndex) {
        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Method creates variation by replacing characterToReplace with all allowedReplacements (one at a time)
     *
     * @param originalTexts       texts from which variation are to be made
     * @param characterToReplace  make variations by replacing this character in originalText
     * @param allowedReplacements replace characterToReplace with each of these characters
     * @return all unique variations
     */
    public static Set<String> makeVariations(List<String> originalTexts, char characterToReplace, char... allowedReplacements) {
        Set<String> result = new HashSet<>();
        for (String originalText : originalTexts) {
            result.add(originalText);
            for (char replacement : allowedReplacements) {
                result.add(originalText.replace(characterToReplace, replacement));
            }
        }
        return result;
    }

    /**
     * In terms of sets = invertedRanges and invertedCharacters = allCharacterRange - symbols and symbolRanges
     * i.e. in invertedRanges and invertedCharacters are all characters that are not in symbols and symbolRanges
     *
     * @param symbolRanges
     * @param symbols
     * @param allCharactersRange
     * @param invertedRanges
     * @param invertedCharacters
     */
    public static void invertSymbolsAndRanges(List<SymbolRange> symbolRanges,
                                              List<Character> symbols,
                                              SymbolRange allCharactersRange,
                                              List<SymbolRange> invertedRanges,
                                              List<Character> invertedCharacters) {
        int firstCharInRange = allCharactersRange.getFrom();
        int lastCharInRange = allCharactersRange.getTo();

        TreeSet<SymbolRange> sortedRanges = getApplicableSortedUniqueRanges(symbolRanges, symbols, allCharactersRange);

        int start = firstCharInRange;
        for (SymbolRange range : sortedRanges) {
            int from = range.getFrom();
            int to = range.getTo();

            if (start <= from) {
                if (start + 1 == from) {
                    invertedCharacters.add((char) start);
                } else if (start != from) {
                    invertedRanges.add(range(start, from - 1));
                }
            }
            start = to + 1;
            if (start > lastCharInRange) {
                return;
            }
        }

        if (start < lastCharInRange) {
            invertedRanges.add(range(start, lastCharInRange));
        } else if (start == lastCharInRange) {
            invertedCharacters.add((char) start);
        }
    }

    private static TreeSet<SymbolRange> getApplicableSortedUniqueRanges(List<SymbolRange> symbolRanges, List<Character> symbols, SymbolRange allowedRange) {
        int firstCharInRange = allowedRange.getFrom();
        int lastCharInRange = allowedRange.getTo();

        Stream<SymbolRange> matchingRanges = symbolRanges
                .stream()
                .filter(range -> range.getTo() >= firstCharInRange && range.getFrom() <= lastCharInRange);
        Stream<SymbolRange> matchingCharactersAsRanges = symbols
                .stream()
                .filter(c -> firstCharInRange <= c && c <= lastCharInRange).map(symbol -> range(symbol, symbol));
        return Stream
                .concat(matchingRanges, matchingCharactersAsRanges)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SymbolRange::getFrom))));
    }

    public static void compactOverlappingRangesAndSymbols(List<SymbolRange> originalSymbolRanges, List<Character> originalSymbols,
                                                          List<SymbolRange> compactedRanges, List<Character> compactedSymbols) {
        List<SymbolRange> sortedRanges = Stream
                .concat(
                        originalSymbolRanges.stream(),
                        originalSymbols.stream().map(symbol -> range(symbol, symbol))
                )
                .sorted(Comparator.comparing(SymbolRange::getFrom))
                .collect(Collectors.toList());

        if (sortedRanges.size() == 1) {
            compactedSymbols.addAll(originalSymbols);
            compactedRanges.addAll(originalSymbolRanges);
            return;
        }

        for (int i = 1; i < sortedRanges.size(); ) {
            SymbolRange a = sortedRanges.get(i - 1);
            SymbolRange b = sortedRanges.get(i);
            if (isRightWithinLeft(a, b)) {
                sortedRanges.remove(i);
            } else if (isRightWithinLeft(b, a)) {
                sortedRanges.remove(i - 1);
            } else if (isRightCanContinueLeft(a, b)) {
                sortedRanges.remove(i);
                sortedRanges.set(i - 1, range(a.getFrom(), b.getTo()));
            } else {
                ++i;
            }
        }

        for (SymbolRange range : sortedRanges) {
            if (range.getFrom() == range.getTo()) {
                compactedSymbols.add((char) range.getFrom());
            } else {
                compactedRanges.add(range);
            }
        }
    }

    public static boolean isRightCanContinueLeft(SymbolRange left, SymbolRange right) {
        return left.getFrom() <= right.getFrom() && right.getFrom() <= left.getTo() + 1 && left.getTo() < right.getTo();
    }

    public static boolean isRightWithinLeft(SymbolRange left, SymbolRange right) {
        return left.getFrom() <= right.getFrom() && left.getTo() >= right.getTo();
    }
}
