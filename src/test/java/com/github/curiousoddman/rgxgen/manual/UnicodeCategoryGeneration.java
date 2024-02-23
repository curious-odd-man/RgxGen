package com.github.curiousoddman.rgxgen.manual;

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
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.curiousoddman.rgxgen.testutil.TestingUtilities.makeUnicodeCharacterArray;

@SuppressWarnings({"TestMethodWithoutAssertion", "NewClassNamingConvention"})
//@Disabled("Generator - not a test")
public class UnicodeCategoryGeneration {

    @Test
    void splitUnicodeSymbolsPerCharacterClasses() throws IOException {
        Map<UnicodeCategory, Pattern> categoryPerPattern = compiledAllPatterns();
        Map<UnicodeCategory, List<Character>> matchedMap = findMatchingSymbolsPerPattern(categoryPerPattern);

        System.out.println("Sorting all characters in groups");
        for (List<Character> value : matchedMap.values()) {
            value.sort(Comparator.naturalOrder());
        }

        System.out.println("Transforming to symbols and groups");

        Map<UnicodeCategory, UnicodeCategoryDescriptor> descriptorMap = createDescriptorMap(matchedMap);
        Map<UnicodeCategory, LineDescriptor> textPerCategory = formatDescriptorsIntoJavaCode(descriptorMap);

        Map<SymbolRange, String> rangesConstantNames = writeConstants(textPerCategory);

        modifySourceJavaFile(textPerCategory, rangesConstantNames);
    }

    private static TreeMap<SymbolRange, String> getNamedRanges() throws IOException {
        TreeMap<SymbolRange, String> ranges = new TreeMap<>(Comparator.comparingInt(SymbolRange::getFrom));
        List<String> allLines = Files.readAllLines(Paths.get("data/ranges/input-ranges-description-refined.txt"));
        String prevName = null;
        int prevRangeStart = 0;
        for (String line : allLines) {
            String[] parts = line.split("\t");
            int rangeStart = Integer.parseInt(parts[0], 16);
            if (prevName != null) {
                ranges.put(SymbolRange.range(prevRangeStart, rangeStart - 1), prevName);
            }
            prevName = parts[1];
            prevRangeStart = rangeStart;
        }

        ranges.put(SymbolRange.range(prevRangeStart, 0xFFFF), prevName);
        return ranges;
    }

    private static Map<SymbolRange, String> writeConstants(Map<UnicodeCategory, LineDescriptor> textPerPattern) throws IOException {

        Set<SymbolRange> allRanges = getAllRanges(textPerPattern);
        Path path = Paths.get("src/main/java/com/github/curiousoddman/rgxgen/model/UnicodeCategoryConstants.java");
        Map<SymbolRange, String> rangesConstantsNames = assignNamesToRanges(allRanges);

        List<String> lines = new ArrayList<>();
        lines.add("package com.github.curiousoddman.rgxgen.model;");
        lines.add("");
        lines.add("public class UnicodeCategoryConstants {");
        for (Map.Entry<SymbolRange, String> entry : rangesConstantsNames.entrySet()) {
            String name = entry.getValue();
            int from = entry.getKey().getFrom();
            int to = entry.getKey().getTo();
            lines.add(String.format("    public static final SymbolRange %s = SymbolRange.range('%s', '%s');  // 0x%x - 0x%x", name, charAsString(from), charAsString(to), from, to));
            createSymbolRangeFile(name, from, to);
        }
        lines.add("}");
        Files.write(path, lines);

        return rangesConstantsNames;
    }

    private static void createSymbolRangeFile(String name, int from, int to) throws IOException {
        Path rangeSymbolsFilePath = Paths.get("data/symbols").resolve(name + ".txt");
        List<String> symbolFileLines = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            symbolFileLines.add(String.format("%d\t0x%x\t%s", i, i, charAsString(i)));
        }
        Files.write(rangeSymbolsFilePath, symbolFileLines);
    }

    private static Map<SymbolRange, String> assignNamesToRanges(Set<SymbolRange> allRanges) throws IOException {
        Map<SymbolRange, String> rangesConstantsNames = new LinkedHashMap<>();
        int constantIndex = 0;
        TreeMap<SymbolRange, String> namedRanges = getNamedRanges();
        for (SymbolRange range : allRanges) {
            String name;
            String rangePredefinedName = namedRanges.get(range);
            if (rangePredefinedName != null) {
                name = rangePredefinedName.replace(' ', '_').toUpperCase(Locale.ROOT);
            } else {
                name = "RANGE_" + constantIndex++;
            }
            rangesConstantsNames.put(range, name);
        }
        return rangesConstantsNames;
    }

    private static Set<SymbolRange> getAllRanges(Map<UnicodeCategory, LineDescriptor> textPerPattern) {
        return textPerPattern
                .values()
                .stream()
                .map(LineDescriptor::getRanges)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private static void modifySourceJavaFile(Map<UnicodeCategory, LineDescriptor> textPerPattern, Map<SymbolRange, String> constantNames) throws IOException {
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
                transformedLines.add("/*NO_CHANGE*/" + line);
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

    private static class LineDescriptor {
        private static final String pattern = "    %s(%s, %s, %s, %s),";
        UnicodeCategory   unicodeCategory;
        List<String>      keys;
        String            description;
        List<SymbolRange> ranges;
        List<Character>   characters;

        LineDescriptor(UnicodeCategory unicodeCategory, List<String> keys, String description, List<SymbolRange> ranges, List<Character> characters) {
            this.unicodeCategory = unicodeCategory;
            this.keys = keys;
            this.description = description;
            this.ranges = ranges;
            this.characters = characters;
        }

        public String formatToText(Map<SymbolRange, String> constantNames) {
            return String.format(pattern, unicodeCategory.name(), makeKeysText(unicodeCategory), makeDescription(unicodeCategory), makeRanges(ranges, constantNames), makeCharacters(characters));
        }

        private static String makeCharacters(List<Character> characters) {
            if (characters.isEmpty()) {
                return "null";
            }
            return String.format("new Character[]{%s}",
                                 characters.stream().map(UnicodeCategoryGeneration::charAsString).map(LineDescriptor::sq).collect(Collectors.joining(","))
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
            return constantNames.getOrDefault(range, String.format("range('%s', '%s')", charAsString(range.getFrom()), charAsString(range.getTo())));
        }

        private static String makeDescription(UnicodeCategory key) {
            if (key.getDescription() == null) {
                return "";
            }
            return q(key.getDescription());
        }

        private static String makeKeysText(UnicodeCategory key) {
            List<String> keys = key.getKeys();
            return "keys(" + keys.stream().map(LineDescriptor::q).collect(Collectors.joining(",")) + ")";
        }

        private static String q(String text) {
            return '"' + text + '"';
        }

        private static String sq(String text) {
            return '\'' + text + '\'';
        }

        public List<SymbolRange> getRanges() {
            return ranges;
        }
    }

    private static Map<UnicodeCategory, List<Character>> findMatchingSymbolsPerPattern(Map<UnicodeCategory, Pattern> categoryPerPattern) {
        EnumMap<UnicodeCategory, List<Character>> matchedMap = new EnumMap<>(UnicodeCategory.class);
        Character[] characters = makeUnicodeCharacterArray();
        for (Character character : characters) {
            String str = String.valueOf(character);
            for (Map.Entry<UnicodeCategory, Pattern> entry : categoryPerPattern.entrySet()) {
                Pattern value = entry.getValue();
                UnicodeCategory category = entry.getKey();
                if (value.matcher(str).matches()) {
                    matchedMap.computeIfAbsent(category, k -> new ArrayList<>())
                              .add(character);
                }
            }
        }
        return matchedMap;
    }

    private static String charAsString(int c) {
        if (c == '\\' || c == '\'') {
            return "\\" + (char) c;
        }
        return String.valueOf((char) c);
    }

    private static Map<UnicodeCategory, Pattern> compiledAllPatterns() {
        return Arrays
                .stream(UnicodeCategory.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        UnicodeCategoryGeneration::getOptionalPattern
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));

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
            UnicodeCategoryDescriptor descriptor = descriptorMap
                    .computeIfAbsent(entry.getKey(), k -> new UnicodeCategoryDescriptor(new ArrayList<>(), new ArrayList<>()));

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

    private static class UnicodeCategoryDescriptor {
        private final List<SymbolRange> ranges;
        private final List<Character>   characters;

        UnicodeCategoryDescriptor(List<SymbolRange> ranges, List<Character> characters) {
            this.ranges = ranges;
            this.characters = characters;
        }

        public List<SymbolRange> getRanges() {
            return ranges;
        }

        public List<Character> getCharacters() {
            return characters;
        }

        @Override
        public String toString() {
            return "UnicodeCategoryDescriptor{" +
                    "ranges=" + ranges +
                    ", characters=" + characters +
                    '}';
        }
    }

    private static Optional<Pattern> getOptionalPattern(UnicodeCategory unicodeCategory) {
        for (String key : unicodeCategory.getKeys()) {
            try {
                return Optional.of(Pattern.compile("\\p{" + key + "}+"));
            } catch (Exception ignore) {
            }
        }
        return Optional.empty();
    }
}
