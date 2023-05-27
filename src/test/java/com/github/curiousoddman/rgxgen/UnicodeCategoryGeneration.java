package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.makeUnicodeCharacterArray;
import static java.lang.Character.*;
import static java.lang.Character.isTitleCase;

public class UnicodeCategoryGeneration {

    public static Stream<Character> getAllUnicodes() {
        return Arrays.stream(makeUnicodeCharacterArray());
    }


    @ParameterizedTest
    @MethodSource("getAllUnicodes")
    void allCharactersTest(Character c) {
        System.out.println((int) c + " ' " + c + "' is letter " + isLetter(c) + " is digit " + isDigit(c) + " is uppercase " + isUpperCase(c) + " is lowercase " + isLowerCase(c) + " is defined " + isDefined(c) + " is title " + isTitleCase(c));
    }

    @Test
    void splitUnicodeSymbolsPerCharacterClasses() {
        Map<UnicodeCategory, Optional<Pattern>> categoryPerPattern = Arrays.stream(UnicodeCategory.values())
                                                                           .filter(unicodeCategory -> unicodeCategory.getSymbols() == null && unicodeCategory.getSymbolRanges() == null)
                                                                           .collect(Collectors.toMap(
                                                                                   Function.identity(),
                                                                                   unicodeCategory -> {
                                                                                       for (String key : unicodeCategory.getKeys()) {
                                                                                           try {
                                                                                               return Optional.of(Pattern.compile("\\p{" + key + "}+"));
                                                                                           } catch (Exception ignore) {
                                                                                           }
                                                                                       }
                                                                                       return Optional.empty();
                                                                                   }
                                                                           ));
        EnumMap<UnicodeCategory, List<Character>> matchedMap = new EnumMap<>(UnicodeCategory.class);
        List<UnicodeCategory> errorCategories = new ArrayList<>();
        for (Map.Entry<UnicodeCategory, Optional<Pattern>> entry : categoryPerPattern.entrySet()) {
            if (!entry.getValue()
                      .isPresent()) {
                errorCategories.add(entry.getKey());
            }
        }

        Character[] characters = makeUnicodeCharacterArray();

        for (Character character : characters) {
            String str = String.valueOf(character);
            for (Map.Entry<UnicodeCategory, Optional<Pattern>> entry : categoryPerPattern.entrySet()) {
                Optional<Pattern> value = entry.getValue();
                UnicodeCategory category = entry.getKey();
                if (value.isPresent() && value.get()
                                              .matcher(str)
                                              .matches()) {
                    matchedMap.computeIfAbsent(category, k -> new ArrayList<>())
                              .add(character);
                }
            }
        }
        System.out.println("Total errors : " + errorCategories.size() + " of " + UnicodeCategory.values().length);

        System.out.println("Sorting all characters in groups");
        for (List<Character> value : matchedMap.values()) {
            value.sort(Comparator.naturalOrder());
        }

        System.out.println("Transforming to symbols and groups");

        Map<UnicodeCategory, UnicodeCategoryDescriptor> descriptorMap = createDescriptorMap(matchedMap);
        for (Map.Entry<UnicodeCategory, UnicodeCategoryDescriptor> entry : descriptorMap.entrySet()) {
            UnicodeCategory key = entry.getKey();
            UnicodeCategoryDescriptor value = entry.getValue();
            System.out.printf(
                    "\t%s(%s, %s, asList(%s), %s),%n",
                    key,
                    makeKeysText(key),
                    makeDescription(key),
                    makeRanges(value),
                    makeCharacters(value)
            );
        }
    }

    private static String makeCharacters(UnicodeCategoryDescriptor value) {
        if (value.getCharacters().isEmpty()) {
            return "null";
        }
        return String.format("new Character[]{%s}",
                             value.getCharacters().stream().map(UnicodeCategoryGeneration::sq).collect(Collectors.joining(","))
        );
    }

    private static String makeRanges(UnicodeCategoryDescriptor value) {
        if (value.getRanges().isEmpty()) {
            return "null";
        }
        return value.getRanges().stream().map(range -> String.format("symbols(%d, %d)", range.getFrom(), range.getTo())).collect(Collectors.joining(","));
    }

    private static String makeDescription(UnicodeCategory key) {
        if (key.getDescription() == null) {
            return "";
        }
        return q(key.getDescription());
    }

    private static String makeKeysText(UnicodeCategory key) {
        List<String> keys = key.getKeys();
        if (keys.size() == 1) {
            return q(keys.get(0));
        }

        return "asList(" + keys.stream().map(UnicodeCategoryGeneration::q).collect(Collectors.joining(",")) + ")";
    }

    private static String q(String text) {
        return '"' + text + '"';
    }

    private static String sq(Character text) {
        return '\'' + text.toString() + '\'';
    }

    @Test
    void testCreateDescriptorMap() {
        EnumMap<UnicodeCategory, List<Character>> matchedMap = new EnumMap<>(UnicodeCategory.class);
        matchedMap.put(UnicodeCategory.ANY_LETTER, Arrays.asList('t', 'f', 'g', 'h', 'k'));
        Map<UnicodeCategory, UnicodeCategoryDescriptor> descriptorMap = createDescriptorMap(matchedMap);
        for (Map.Entry<UnicodeCategory, UnicodeCategoryDescriptor> entry : descriptorMap.entrySet()) {
            System.out.println("\t " + entry.getKey() + " \t " + entry.getValue());
        }
    }

    @Test
    void testCreateDescriptorMap2() {
        EnumMap<UnicodeCategory, List<Character>> matchedMap = new EnumMap<>(UnicodeCategory.class);
        matchedMap.put(UnicodeCategory.ANY_LETTER, Arrays.asList('a', 'b', 'c', 'x', 'y', 'z'));
        Map<UnicodeCategory, UnicodeCategoryDescriptor> descriptorMap = createDescriptorMap(matchedMap);
        for (Map.Entry<UnicodeCategory, UnicodeCategoryDescriptor> entry : descriptorMap.entrySet()) {
            System.out.println("\t " + entry.getKey() + " \t " + entry.getValue());
        }
    }

    private static Map<UnicodeCategory, UnicodeCategoryDescriptor> createDescriptorMap(Map<UnicodeCategory, List<Character>> matchedMap) {
        Map<UnicodeCategory, UnicodeCategoryDescriptor> descriptorMap = new EnumMap<>(UnicodeCategory.class);
        for (Map.Entry<UnicodeCategory, List<Character>> entry : matchedMap.entrySet()) {
            UnicodeCategoryDescriptor descriptor = descriptorMap.computeIfAbsent(entry.getKey(), k -> new UnicodeCategoryDescriptor(new ArrayList<>(), new ArrayList<>()));

            Character lastCharacter = null;
            SymbolRange lastSymbolRange = null;

            for (Character next : entry.getValue()) {
                if (lastCharacter != null) {
                    if (lastSymbolRange == null) {
                        if (lastCharacter == next - 1) {
                            lastSymbolRange = SymbolRange.symbols(lastCharacter, next);
                        } else {
                            descriptor.characters.add(lastCharacter);
                        }
                    } else {
                        if (lastCharacter == next - 1) {
                            lastSymbolRange = SymbolRange.symbols(lastSymbolRange.getFrom(), next);
                        } else {
                            descriptor.ranges.add(lastSymbolRange);
                            lastSymbolRange = null;
                        }
                    }
                }
                lastCharacter = next;
            }

            if (lastSymbolRange == null) {
                descriptor.characters.add(lastCharacter);
            } else {
                descriptor.ranges.add(lastSymbolRange);
            }

        }
        return descriptorMap;
    }

    @Data
    @AllArgsConstructor
    private static class UnicodeCategoryDescriptor {
        private List<SymbolRange> ranges;
        private List<Character>   characters;
    }
}
