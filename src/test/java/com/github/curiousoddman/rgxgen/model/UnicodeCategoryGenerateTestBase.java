package com.github.curiousoddman.rgxgen.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.Arguments;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.UnicodeCategory.*;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnicodeCategoryGenerateTestBase {

    protected static final List<UnicodeCategory> UNCOMPILABLE_KEYS = unmodifiableList(asList(
            CASED_LETTER,
            IN_LATIN_1_SUPPLEMENT,
            IN_LATIN_EXTENDED_A,
            IN_LATIN_EXTENDED_B,
            IN_GREEK_AND_COPTIC,
            IN_COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS,
            IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A,
            IN_SUPPLEMENTAL_ARROWS_A,
            IN_SUPPLEMENTAL_ARROWS_B,
            IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B,
            IN_ARABIC_PRESENTATION_FORMS_A,
            IN_ARABIC_PRESENTATION_FORMS_B
    ));

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

    Optional<Pattern> compile(String pattern, UnicodeCategory category) {
        try {
            Optional<Pattern> compiledPattern = Optional.of(Pattern.compile(pattern));
            testedCategories.add(category);
            return compiledPattern;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    ValidationResult validateGeneratedText(RgxGenTestPattern testPattern, Supplier<String> generateFunction, ValidationResult validationResult) {
        String generatedText = assertDoesNotThrow(generateFunction::get);
        Set<Character> generatedCharactersForCategory = generatedCharacters.computeIfAbsent(testPattern.getUnicodeCategory(), k -> new HashSet<>());
        char[] generatedTextCharArray = generatedText.toCharArray();
        for (char c : generatedTextCharArray) {
            generatedCharactersForCategory.add(c);
        }

        if (testPattern.getCompiled().isPresent()) {
            if (testPattern.getCompiled().get().matcher(generatedText).matches() == testPattern.isExpectToMatch()) {
                return validationResult.addMatched();
            }

            Pattern singleLetterPattern = Pattern.compile(testPattern.getPatternWithoutLength() + "*");
            boolean[] matches = new boolean[generatedText.length()];
            for (int i = 0; i < generatedTextCharArray.length; i++) {
                matches[i] = singleLetterPattern.matcher(String.valueOf(generatedTextCharArray[i])).matches();
            }
            System.out.println("Match debug:");
            System.out.println('\t' + generatedText + "\t length = " + generatedText.length());
            for (int i = 0; i < generatedText.length(); i++) {
                System.out.print("'" + generatedText.charAt(i) + "' ");
            }
            System.out.println();
            System.out.print('\t');
            for (int i = 0; i < generatedTextCharArray.length; i++) {
                System.out.print(matches[i] == testPattern.isExpectToMatch() ? " " : '!');
            }
            System.out.println("Failed for text '" + generatedText + '\'');
            return validationResult.addNotMatched();
        }

        return validationResult.addMatched();
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
        notTestedCategories.removeAll(UNCOMPILABLE_KEYS);
        if (!notTestedCategories.isEmpty()) {
            fail("Pattern.compile() failed for - " + notTestedCategories);
        }
    }
}
