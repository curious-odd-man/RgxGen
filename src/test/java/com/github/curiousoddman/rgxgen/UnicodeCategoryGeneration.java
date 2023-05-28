package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.makeUnicodeCharacterArray;
import static java.lang.Character.*;
import static java.lang.Character.isTitleCase;

@Disabled("Generator - not a test")
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
    @SneakyThrows
    void splitUnicodeSymbolsPerCharacterClasses() {
        Map<UnicodeCategory, Optional<Pattern>> categoryPerPattern = compiledAllPatterns();
        Map<UnicodeCategory, List<Character>> matchedMap = findMatchingSymbolsPerPattern(categoryPerPattern);
        Set<UnicodeCategory> failedToCompile = categoryPerPattern.entrySet().stream().filter(entry -> !entry.getValue().isPresent()).map(Map.Entry::getKey).collect(Collectors.toSet());

        System.out.println("Sorting all characters in groups");
        for (List<Character> value : matchedMap.values()) {
            value.sort(Comparator.naturalOrder());
        }

        System.out.println("Transforming to symbols and groups");

        Map<UnicodeCategory, UnicodeCategoryDescriptor> descriptorMap = createDescriptorMap(matchedMap);
        Map<UnicodeCategory, LineDescriptor> textPerPattern = formatDescriptorsIntoJavaCode(descriptorMap);

        Map<SymbolRange, String> rangesConstantNames = writeConstants(textPerPattern);

        modifySourceJavaFile(failedToCompile, textPerPattern, rangesConstantNames);
    }

    @SneakyThrows
    private static Map<SymbolRange, String> writeConstants(Map<UnicodeCategory, LineDescriptor> textPerPattern) {
        Map<SymbolRange, Long> countPerRange = textPerPattern.values().stream().map(LineDescriptor::getRanges).flatMap(Collection::stream)
                                                             .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Path path = Paths.get("src/main/java/com/github/curiousoddman/rgxgen/model/UnicodeCategoryConstants.java");
        Map<SymbolRange, String> rangesConstantsNames = new LinkedHashMap<>();
        int constantIndex = 0;
        for (Map.Entry<SymbolRange, Long> entry : countPerRange.entrySet()) {
            if (entry.getValue() >= 2) {
                String name = "RANGE_" + constantIndex++;
                rangesConstantsNames.put(entry.getKey(), name);
            }
        }

        List<String> lines = new ArrayList<>();
        lines.add("package com.github.curiousoddman.rgxgen.model;");
        lines.add("");
        lines.add("public class UnicodeCategoryConstants {");
        for (Map.Entry<SymbolRange, String> entry : rangesConstantsNames.entrySet()) {
            String name = entry.getValue();
            int from = entry.getKey().getFrom();
            int to = entry.getKey().getTo();
            lines.add(String.format("    public static final SymbolRange %s = SymbolRange.symbols('%s', '%s');  // 0x%x - 0x%x", name, charAsString(from), charAsString(to), from, to));
        }
        lines.add("}");
        Files.write(path, lines);

        return rangesConstantsNames;
    }

    @SneakyThrows
    private static void modifySourceJavaFile(Set<UnicodeCategory> failedToCompile, Map<UnicodeCategory, LineDescriptor> textPerPattern, Map<SymbolRange, String> constantNames) {
        Path path = Paths.get("src/main/java/com/github/curiousoddman/rgxgen/model/UnicodeCategory.java").toAbsolutePath();
        List<String> lines = Files.readAllLines(path);
        List<String> transformedLines = new ArrayList<>(lines.size());

        for (String line : lines) {
            Map.Entry<UnicodeCategory, LineDescriptor> found = null;
            for (Map.Entry<UnicodeCategory, LineDescriptor> entry : textPerPattern.entrySet()) {
                String name = entry.getKey().name();
                String trim = getKeyFromLine(line);
                if (trim.equals(name)) {
                    found = entry;
                }
            }

            if (found != null) {
                transformedLines.add(found.getValue().formatToText(constantNames));
            } else {
                try {
                    UnicodeCategory unicodeCategory = UnicodeCategory.valueOf(getKeyFromLine(line));

                    if (failedToCompile.contains(unicodeCategory)) {
                        transformedLines.add("/*COMPILED_ERR*/" + line);
                    } else {
                        transformedLines.add("/*NO_CHANGE*/" + line);
                    }
                } catch (Exception e) {
                    transformedLines.add(line);
                }
            }
        }

        Files.write(path, transformedLines, StandardCharsets.UTF_8);
    }

    private static String getKeyFromLine(String line) {
        return line.split("\\(")[0].trim();
    }

    private static Map<UnicodeCategory, LineDescriptor> formatDescriptorsIntoJavaCode(Map<UnicodeCategory, UnicodeCategoryDescriptor> descriptorMap) {
        Map<UnicodeCategory, LineDescriptor> textPerPattern = new EnumMap<>(UnicodeCategory.class);
        for (Map.Entry<UnicodeCategory, UnicodeCategoryDescriptor> entry : descriptorMap.entrySet()) {
            UnicodeCategory key = entry.getKey();
            UnicodeCategoryDescriptor value = entry.getValue();
            textPerPattern.put(key, new LineDescriptor(
                    key,
                    key.getKeys(),
                    key.getDescription(),
                    value.getRanges(),
                    value.getCharacters()
            ));
        }
        return textPerPattern;
    }

    @Data
    @AllArgsConstructor
    private static class LineDescriptor {
        private static final String pattern = "    %s(%s, %s, %s, %s),";
        UnicodeCategory   unicodeCategory;
        List<String>      keys;
        String            description;
        List<SymbolRange> ranges;
        List<Character>   characters;

        public String formatToText(Map<SymbolRange, String> constantNames) {
            return String.format(pattern, unicodeCategory.name(), makeKeysText(unicodeCategory), makeDescription(unicodeCategory), makeRanges(ranges, constantNames), makeCharacters(characters));
        }

        private static String makeCharacters(List<Character> characters) {
            if (characters.isEmpty()) {
                return "null";
            }
            return String.format("new Character[]{%s}",
                                 characters.stream().map(UnicodeCategoryGeneration::charAsString).map(UnicodeCategoryGeneration::sq).collect(Collectors.joining(","))
            );
        }

        private static String makeRanges(List<SymbolRange> ranges, Map<SymbolRange, String> constantNames) {
            if (ranges.isEmpty()) {
                return "null";
            }
            if (ranges.size() == 1) {
                SymbolRange range = ranges.get(0);
                return "singletonList(" + rangeOrConstant(constantNames, range) + ')';
            }
            return "asList(" + ranges
                    .stream()
                    .map(range -> rangeOrConstant(constantNames, range))
                    .collect(Collectors.joining(",")) + ')';
        }

        private static String rangeOrConstant(Map<SymbolRange, String> constantNames, SymbolRange range) {
            return constantNames.getOrDefault(range, String.format("symbols('%s', '%s')", charAsString(range.getFrom()), charAsString(range.getTo())));
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

            return "keys(" + keys.stream().map(UnicodeCategoryGeneration::q).collect(Collectors.joining(",")) + ")";
        }

    }

    private static EnumMap<UnicodeCategory, List<Character>> findMatchingSymbolsPerPattern(Map<UnicodeCategory, Optional<Pattern>> categoryPerPattern) {
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
                if (value.isPresent() && value.get().matcher(str).matches()) {
                    matchedMap.computeIfAbsent(category, k -> new ArrayList<>())
                              .add(character);
                }
            }
        }
        System.out.println("Total errors : " + errorCategories.size() + " of " + UnicodeCategory.values().length);
        return matchedMap;
    }

    private static String charAsString(int c) {
        if (c == '\\' || c == '\'') {
            return "\\" + (char) c;
        }
        return String.valueOf((char) c);
    }

    private static Map<UnicodeCategory, Optional<Pattern>> compiledAllPatterns() {
        return Arrays.stream(UnicodeCategory.values())
                     .filter(unicodeCategory -> !unicodeCategory.isConfigured())
                     .collect(Collectors.toMap(
                             Function.identity(),
                             unicodeCategory -> {
                                 for (String key : unicodeCategory.getKeys()) {
                                     try {
                                         return Optional.of(Pattern.compile("\\p{" + key + "}+", Pattern.UNICODE_CHARACTER_CLASS));      // , Pattern.UNICODE_CHARACTER_CLASS
                                     } catch (Exception ignore) {
                                     }
                                 }
                                 return Optional.empty();
                             }
                     ));
    }


    private static String q(String text) {
        return '"' + text + '"';
    }

    private static String sq(String text) {
        return '\'' + text + '\'';
    }

    @Test
    void testCreateDescriptorMap() {
        Map<UnicodeCategory, List<Character>> matchedMap = new EnumMap<>(UnicodeCategory.class);
        matchedMap.put(UnicodeCategory.ANY_LETTER, Arrays.asList('t', 'f', 'g', 'h', 'k'));
        Map<UnicodeCategory, UnicodeCategoryDescriptor> descriptorMap = createDescriptorMap(matchedMap);
        for (Map.Entry<UnicodeCategory, UnicodeCategoryDescriptor> entry : descriptorMap.entrySet()) {
            System.out.println("\t " + entry.getKey() + " \t " + entry.getValue());
        }
    }

    @Test
    void testCreateDescriptorMap2() {
        Map<UnicodeCategory, List<Character>> matchedMap = new EnumMap<>(UnicodeCategory.class);
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
                            lastSymbolRange = SymbolRange.range(lastCharacter, next);
                        } else {
                            descriptor.characters.add(lastCharacter);
                        }
                    } else {
                        if (lastCharacter == next - 1) {
                            lastSymbolRange = SymbolRange.range(lastSymbolRange.getFrom(), next);
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
