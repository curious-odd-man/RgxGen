package com.github.curiousoddman.rgxgen.manual.generator.unicode;

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

    public static final Path SYMBOL_RANGE_DUMP_PATH = Paths.get("data/symbols");

    @Test
    void splitUnicodeSymbolsPerCharacterClasses() throws IOException {
        Map<UnicodeCategory, Pattern> categoryPerPattern = compiledAllPatterns();
        Map<UnicodeCategory, List<Character>> matchedMap = findMatchingSymbolsPerPattern(categoryPerPattern);

        for (List<Character> value : matchedMap.values()) {
            value.sort(Comparator.naturalOrder());
        }

        Map<UnicodeCategory, UnicodeCategoryDescriptor> descriptorMap = createDescriptorMap(matchedMap);
        Map<UnicodeCategory, LineDescriptor> textPerCategory = formatDescriptorsIntoJavaCode(descriptorMap);

        Map<SymbolRange, String> rangesConstantNames = writeConstants(textPerCategory);

        modifySourceJavaFile(textPerCategory, rangesConstantNames);
    }

    private static TreeMap<Integer, NamedSymbolRange> getNamedRanges() throws IOException {
        TreeMap<Integer, NamedSymbolRange> ranges = new TreeMap<>(Comparator.naturalOrder());
        List<String> allLines = Files.readAllLines(Paths.get("data/ranges/input-ranges-description-refined.txt"));
        Set<String> usedNames = new TreeSet<>();
        String sectionName = null;
        String prevName = null;
        int prevRangeStart = -1;
        for (String line : allLines) {
            String[] parts = line.split("\t");
            int rangeStart = Integer.parseInt(parts[0], 16);

            if (rangeStart == prevRangeStart) {
                sectionName = prevName;
            } else if (prevName != null) {
                RangeName upperCaseValidName = new RangeName(sectionName, prevName);
                upperCaseValidName = ensureNameIsUnique(usedNames, upperCaseValidName);
                SymbolRange range = SymbolRange.range(prevRangeStart, rangeStart - 1);
                ranges.put(range.getFrom(), new NamedSymbolRange(range, upperCaseValidName));
            }

            prevName = parts[1];
            prevRangeStart = rangeStart;
        }
        RangeName upperCaseValidName = new RangeName(sectionName, prevName);
        upperCaseValidName = ensureNameIsUnique(usedNames, upperCaseValidName);
        SymbolRange range = SymbolRange.range(prevRangeStart, 0xFFFF);
        ranges.put(range.getFrom(), new NamedSymbolRange(range, upperCaseValidName));
        return ranges;
    }

    private static Map<SymbolRange, String> writeConstants(Map<UnicodeCategory, LineDescriptor> textPerCategory) throws IOException {
        cleanupDirectoryWithRangeTextFiles();
        Set<SymbolRange> allRanges = getAllRanges(textPerCategory);
        Path path = Paths.get("src/main/java/com/github/curiousoddman/rgxgen/model/UnicodeCategoryConstants.java");
        Map<SymbolRange, String> rangesConstantsNames = assignNamesToRanges(allRanges);
        List<SymbolRange> sortedRanges = new ArrayList<>(rangesConstantsNames.keySet());
        sortedRanges.sort(Comparator.comparingInt(SymbolRange::getFrom));

        List<String> lines = new ArrayList<>();
        lines.add("package com.github.curiousoddman.rgxgen.model;");
        lines.add("");
        lines.add("public class UnicodeCategoryConstants {");
        for (SymbolRange range : sortedRanges) {
            String name = rangesConstantsNames.get(range);
            int from = range.getFrom();
            int to = range.getTo();
            lines.add(String.format("    public static final SymbolRange %s = SymbolRange.range('%s', '%s');  // 0x%x - 0x%x", name, Utils.charAsString(from), Utils.charAsString(to), from, to));
            createSymbolRangeFile(name, from, to);
        }
        lines.add("}");
        Files.write(path, lines);

        return rangesConstantsNames;
    }

    private static void cleanupDirectoryWithRangeTextFiles() throws IOException {
        Files.walk(SYMBOL_RANGE_DUMP_PATH)
             .filter(Files::isRegularFile)
             .forEach(Utils::silentDeleteFile);
    }

    private static void createSymbolRangeFile(String name, int from, int to) throws IOException {
        Path rangeSymbolsFilePath = SYMBOL_RANGE_DUMP_PATH.resolve(name + ".txt");
        List<String> symbolFileLines = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            symbolFileLines.add(String.format("%d\t0x%x\t0x%04x\t%s", i, i, i, Utils.charAsString(i)));
        }
        Files.write(rangeSymbolsFilePath, symbolFileLines);
    }

    private static Map<SymbolRange, String> assignNamesToRanges(Set<SymbolRange> allRanges) throws IOException {
        Map<SymbolRange, String> rangesConstantsNames = new HashMap<>();
        Set<String> usedRangeNames = new HashSet<>();
        TreeMap<Integer, NamedSymbolRange> namedRanges = getNamedRanges();
        for (SymbolRange range : allRanges) {
            RangeName name;
            Map.Entry<Integer, NamedSymbolRange> floorEntry = namedRanges.floorEntry(range.getFrom());
            NamedSymbolRange namedSymbolRange = floorEntry.getValue();
            if (namedSymbolRange.range.equals(range)) {
                name = namedSymbolRange.name;
            } else {
                if (namedSymbolRange.range.getTo() >= range.getTo()) {
                    name = new RangeName(namedSymbolRange.name.sectionName, namedSymbolRange.name.subrangeName + "_SUBSET", -1);
                } else {
                    int nextAfterThisRangeEnd = namedRanges.ceilingKey(range.getTo());
                    Map.Entry<Integer, NamedSymbolRange> lastRange = namedRanges.lowerEntry(nextAfterThisRangeEnd);

                    String lastRangeSection = lastRange.getValue().name.sectionName;
                    String floorRangeSection = floorEntry.getValue().name.sectionName;
                    String lastRangeSubrange = lastRange.getValue().name.subrangeName;
                    String floorRangeSubrange = floorEntry.getValue().name.subrangeName;
                    if (lastRangeSection.equals(floorRangeSection)) {
                        name = new RangeName(lastRangeSection, floorRangeSubrange + "_TO_" + lastRangeSubrange, -1);
                    } else {
                        name = new RangeName(floorRangeSection, "TO_" + lastRangeSection, -1);
                    }
                }
            }
            name = ensureNameIsUnique(usedRangeNames, name);
            rangesConstantsNames.put(range, name.combinedName);
        }
        return rangesConstantsNames;
    }

    private static RangeName ensureNameIsUnique(Set<String> usedNames, RangeName name) {
        int index = 1;
        RangeName finalName = name;
        while (usedNames.contains(finalName.combinedName)) {
            finalName = new RangeName(name.sectionName, name.subrangeName, index);
            index++;
        }
        usedNames.add(finalName.combinedName);
        return finalName;
    }

    private static Set<SymbolRange> getAllRanges(Map<UnicodeCategory, LineDescriptor> textPerPattern) {
        return textPerPattern
                .values()
                .stream()
                .map(LineDescriptor::getRanges)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private static void modifySourceJavaFile(Map<UnicodeCategory, LineDescriptor> textPerCategory, Map<SymbolRange, String> constantNames) throws IOException {
        Path path = Paths.get("src/main/java/com/github/curiousoddman/rgxgen/model/UnicodeCategory.java").toAbsolutePath();
        List<String> lines = Files.readAllLines(path);
        List<String> transformedLines = new ArrayList<>(lines.size());

        for (String line : lines) {
            Map.Entry<UnicodeCategory, LineDescriptor> found = null;
            for (Map.Entry<UnicodeCategory, LineDescriptor> entry : textPerCategory.entrySet()) {
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
                    UnicodeCategory.valueOf(getKeyFromLine(line));
                    transformedLines.add("/*NO_CHANGE*/" + line);
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
        Map<UnicodeCategory, LineDescriptor> textPerCategory = new EnumMap<>(UnicodeCategory.class);
        for (Map.Entry<UnicodeCategory, UnicodeCategoryDescriptor> entry : descriptorMap.entrySet()) {
            UnicodeCategory key = entry.getKey();
            UnicodeCategoryDescriptor value = entry.getValue();
            textPerCategory.put(key, new LineDescriptor(
                    key,
                    key.getKeys(),
                    key.getDescription(),
                    value.getRanges(),
                    value.getCharacters()
            ));
        }
        return textPerCategory;
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
                            descriptor.getCharacters().add(lastCharacter);
                        }
                    } else {
                        if (lastCharacter == next - 1) {
                            lastSymbolRange = SymbolRange.range(lastSymbolRange.getFrom(), next);
                        } else {
                            descriptor.getRanges().add(lastSymbolRange);
                            lastSymbolRange = null;
                        }
                    }
                }
                lastCharacter = next;
            }

            if (lastSymbolRange == null) {
                descriptor.getCharacters().add(lastCharacter);
            } else {
                descriptor.getRanges().add(lastSymbolRange);
            }

        }
        return descriptorMap;
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
