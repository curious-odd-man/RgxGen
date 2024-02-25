package com.github.curiousoddman.rgxgen.manual.generator.unicode;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import com.github.curiousoddman.rgxgen.util.chars.CharList;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.data.TestPattern.stream;
import static java.util.stream.Collectors.*;

@Disabled("Manual test")
class UnicodeCategoryExclusionOfNonPrintableCharacters {
    @Test
    void tryWritingEachCharacterToFile() throws IOException {
        Map<Character, List<UnicodeCategory>> characterListMap = Arrays.stream(UnicodeCategory
                                                                                       .values())
                                                                       .flatMap(unicodeCategory ->
                                                                                {
                                                                                    char[] symbols = unicodeCategory.getSymbols();
                                                                                    return Stream.concat(
                                                                                            stream(symbols).map(c -> new Pair(unicodeCategory, c)),
                                                                                            unicodeCategory.getSymbolRanges().stream()
                                                                                                           .map(SymbolRange::chars)
                                                                                                           .flatMap(CharList::stream)
                                                                                                           .map(c -> new Pair(unicodeCategory, c)));
                                                                                }
                                                                       )
                                                                       .collect(groupingBy(Pair::getCharacter, mapping(Pair::getCategory, toList())));

        for (Map.Entry<Character, List<UnicodeCategory>> entry : characterListMap.entrySet()) {
            Path tmpFile = Files.createTempFile("rgxgen", String.valueOf((int) entry.getKey()));
            try {
                String o = String.valueOf(entry.getKey());
                Files.write(tmpFile, Collections.singletonList(o), StandardOpenOption.CREATE, StandardOpenOption.DELETE_ON_CLOSE);
            } catch (Exception e) {
                System.out.println("==================================");
                System.out.println("Failed writing file with character code " + (int) entry.getKey());
                System.out.println("Error " + e.getMessage());
                System.out.println("Present in categories: " + entry.getValue());
            }
        }
    }

    private static class Pair {
        private final UnicodeCategory category;
        private final char            character;

        Pair(UnicodeCategory category, char character) {
            this.category = category;
            this.character = character;
        }

        public UnicodeCategory getCategory() {
            return category;
        }

        public Character getCharacter() {
            return character;
        }
    }
}
