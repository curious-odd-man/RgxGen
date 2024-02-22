package com.github.curiousoddman.rgxgen.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.Arguments;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.UnicodeCategory.OTHER_LETTER;
import static com.github.curiousoddman.rgxgen.model.UnicodeCategory.values;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.UNICODE_SYMBOL_RANGE;
import static java.util.Arrays.asList;
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

    public static Stream<Arguments> getKeyAndCategoryAndSingleSymbol() {
        return Arrays.stream(values())
                     .flatMap(unicodeCategory -> unicodeCategory
                             .getKeys()
                             .stream()
                             .flatMap(key -> key.length() == 1 ? Stream.of(key, wrapInCurvy(key)) : Stream.of(wrapInCurvy(key)))
                             .flatMap(key -> Stream.concat(
                                     Arrays.stream(unicodeCategory.getSymbols()),
                                     unicodeCategory.getSymbolRanges().stream().flatMap(SymbolRange::chars)
                             ).map(character -> Arguments.of(key + ":" + (int) character, key, unicodeCategory, character))));
    }

    public static Stream<Arguments> getKeyAndCategoryAndSingleSymbolNotInCategory() {
        return Arrays.stream(values())
                     .flatMap(unicodeCategory -> unicodeCategory
                             .getKeys()
                             .stream()
                             .flatMap(key -> key.length() == 1 ? Stream.of(key, wrapInCurvy(key)) : Stream.of(wrapInCurvy(key)))
                             .flatMap(key -> UNICODE_SYMBOL_RANGE.chars().filter(c -> !unicodeCategory.contains(c))
                                                                 .map(character -> Arguments.of(key + ":" + (int) character, key, unicodeCategory, character))));
    }

    public static String wrapInCurvy(String s) {
        return '{' + s + '}';
    }

    Set<UnicodeCategory>                 testedCategories;
    Map<UnicodeCategory, Set<Character>> generatedCharacters;

    @BeforeAll
    void beforeAll() {
        testedCategories = EnumSet.noneOf(UnicodeCategory.class);
    }

    @BeforeEach
    void beforeEach() {
        generatedCharacters = new EnumMap<>(UnicodeCategory.class);
    }

    Optional<Pattern> compile(String pattern, UnicodeCategory category) {
        if (testedCategories.contains(category)) {
            return Optional.empty();
        }

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
            System.out.println("Failed for text '" + generatedText + '\'');
            System.out.println("Match debug:");
            System.out.println('\t' + generatedText + "\t length = " + generatedText.length());
            StringBuilder lettersBuilder = new StringBuilder("\t");
            StringBuilder matchesBuilder = new StringBuilder("\t");
            StringBuilder unmatchedCodes = new StringBuilder("\t");
            for (int i = 0; i < generatedText.length(); i++) {
                lettersBuilder.append('\'').append(generatedText.charAt(i)).append("' ");
                boolean isOk = matches[i] == testPattern.isExpectToMatch();
                matchesBuilder.append(' ').append(isOk ? "." : '!').append("  ");
                unmatchedCodes.append(' ').append(isOk ? " " : ((int) generatedText.charAt(i))).append("  ");
            }
            System.out.println(lettersBuilder);
            System.out.println(matchesBuilder);
            System.out.println(unmatchedCodes);
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
        //notTestedCategories.removeAll(UNCOMPILABLE_KEYS);
        if (!notTestedCategories.isEmpty()) {
            fail("Pattern.compile() failed for - " + notTestedCategories);
        }
    }

    public static class CategoryFinder {
        public static void main(String[] args) {
            int minChar = 4341;
            int maxChar = 4341;
            List<Character> characters = asList(OTHER_LETTER.getSymbols());
            for (int i = minChar; i <= maxChar; i++) {
                if (characters.contains((char) i)) {
                    System.out.println(i + " found in individual characters");
                }

                for (SymbolRange symbolRange : OTHER_LETTER.getSymbolRanges()) {
                    if (symbolRange.getFrom() <= i && symbolRange.getTo() >= i) {
                        System.out.println(i + " found in a range: " + symbolRange);
                    }
                }
            }
        }
    }
}
