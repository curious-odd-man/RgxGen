package com.github.curiousoddman.rgxgen.model;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.Arguments;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.UnicodeCategory.values;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnicodeCategoryGenerateTestBase {

    public static Stream<Arguments> getKeyAndCategory() {
        return Arrays.stream(values())
                     .flatMap(uc -> uc
                             .getKeys()
                             .stream()
                             .flatMap(k -> k.length() == 1 ? Stream.of(k, wrapInCurvy(k)) : Stream.of(wrapInCurvy(k)))
                             .map(k -> Arguments.of(k, uc)));
    }

    private static String wrapInCurvy(String s) {
        return '{' + s + '}';
    }

    Set<UnicodeCategory>                 testedCategories;
    Map<UnicodeCategory, Set<Character>> generatedCharacters;

    @BeforeAll
    void beforeAll() {
        testedCategories = new HashSet<>();
    }

    @BeforeEach
    void beforeEach() {
        generatedCharacters = new HashMap<>();
    }

    @SneakyThrows
    Optional<Pattern> compile(String pattern, UnicodeCategory category) {
        try {
            Optional<Pattern> compiledPattern = Optional.of(Pattern.compile(pattern));
            testedCategories.add(category);
            return compiledPattern;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @SneakyThrows
    void validatePattern(Optional<Pattern> compiledPattern, Supplier<String> generateFunction, UnicodeCategory category, boolean expectToMatch) {
        String generatedText = assertDoesNotThrow(generateFunction::get);
        Set<Character> generatedCharactersForCategory = generatedCharacters.computeIfAbsent(category, k -> new HashSet<>());
        char[] generatedTextCharArray = generatedText.toCharArray();
        for (char c : generatedTextCharArray) {
            generatedCharactersForCategory.add(c);
        }
        compiledPattern.ifPresent(pattern -> {
            if (pattern.matcher(generatedText).matches() == expectToMatch) {
                return;
            }

            boolean[] matches = new boolean[generatedText.length()];
            for (int i = 0; i < generatedTextCharArray.length; i++) {
                matches[i] = pattern.matcher(String.valueOf(generatedTextCharArray[i])).matches();
            }
            System.out.println("Match debug:");
            System.out.println('\t' + generatedText);
            System.out.print('\t');
            for (int i = 0; i < generatedTextCharArray.length; i++) {
                System.out.print(matches[i] == expectToMatch ? " " : '!');
            }
            System.out.println();
            fail("Failed for text '" + generatedText + '\'');
        });
    }

    @AfterEach
    void printGenerationStatistics() {
        for (Map.Entry<UnicodeCategory, Set<Character>> entry : generatedCharacters.entrySet()) {
            Set<Character> value = entry.getValue();
            IntSummaryStatistics intSummaryStatistics = value.stream().mapToInt(i -> (int) i).summaryStatistics();
            UnicodeCategory category = entry.getKey();
            System.out.println("Category " + category + " with keys " + category.getKeys() + " character stats: ");
            System.out.println("\tMin: " + intSummaryStatistics.getMin() + "; '" + (char) intSummaryStatistics.getMin() + '\'');
            System.out.println("\tMax: " + intSummaryStatistics.getMax() + "; '" + (char) intSummaryStatistics.getMax() + '\'');
            double totalValueCount = category.getSymbols().length + category.getSymbolRanges().stream().mapToInt(range -> range.getTo() - range.getFrom() + 1).sum();
            System.out.println("\tCovered: " + (value.size() / totalValueCount));
        }
    }

    @AfterAll
    void verifyAllCategoriesTestedWithPatternCompile() {
        List<UnicodeCategory> notTestedCategories = Arrays.stream(values()).filter(category -> !testedCategories.contains(category)).collect(Collectors.toList());
        notTestedCategories.removeAll(UnicodeCategoryTest.UNCOMPILABLE_KEYS);
        if (!notTestedCategories.isEmpty()) {
            fail("Pattern.compile() failed for - " + notTestedCategories);
        }
    }
}
