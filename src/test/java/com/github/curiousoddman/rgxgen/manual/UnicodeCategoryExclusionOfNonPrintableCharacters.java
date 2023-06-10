package com.github.curiousoddman.rgxgen.manual;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import lombok.SneakyThrows;
import lombok.Value;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Disabled("Manual test")
class UnicodeCategoryExclusionOfNonPrintableCharacters {
    @Test
    @SneakyThrows
    void tryWritingEachCharacterToFile() {
        Map<Character, List<UnicodeCategory>> characterListMap = Arrays.stream(UnicodeCategory
                                                                                       .values())
                                                                       .flatMap(unicodeCategory ->
                                                                                {
                                                                                    Character[] symbols = unicodeCategory.getSymbols();
                                                                                    return Stream.concat(
                                                                                            Arrays.stream(symbols).map(c -> new Pair(unicodeCategory, c)),
                                                                                            unicodeCategory.getSymbolRanges().stream()
                                                                                                           .flatMap(UnicodeCategoryExclusionOfNonPrintableCharacters::getCharacterStream)
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

    private static Stream<Character> getCharacterStream(SymbolRange range) {
        return IntStream.range(range.getFrom(), range.getTo() + 1).mapToObj((int c1) -> Character.valueOf((char) c1));
    }

    @Value
    private static class Pair {
        UnicodeCategory category;
        Character       character;
    }
}
