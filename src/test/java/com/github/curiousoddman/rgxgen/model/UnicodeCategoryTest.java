package com.github.curiousoddman.rgxgen.model;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.UnicodeCategory.*;
import static org.junit.jupiter.api.Assertions.*;

class UnicodeCategoryTest {
    public static final List<UnicodeCategory> UNCOMPILABLE_KEYS = Arrays.asList(
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
    );

    public static final int GENERATE_ITERATIONS = 1000;

    @Test
        // FIXME
    void tmpTest() {
        Pattern pattern = Pattern.compile("\\P{L}+");
        System.out.println(pattern.matcher(".}~+.|^9- 6(3&|").matches());
    }

    @ParameterizedTest
    @EnumSource(UnicodeCategory.class)
    void keysArePresentTest(UnicodeCategory unicodeCategory) {
        assertNotNull(unicodeCategory.getKeys());
        assertFalse(unicodeCategory.getKeys().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(UnicodeCategory.class)
    void descriptionPresentTest(UnicodeCategory unicodeCategory) {
        assertNotNull(unicodeCategory.getDescription());
    }

    @ParameterizedTest
    @EnumSource(UnicodeCategory.class)
    void symbolsOrSymbolRangesPresentTest(UnicodeCategory unicodeCategory) {
        boolean rangesPresent = unicodeCategory.getSymbolRanges() != null && !unicodeCategory.getSymbolRanges().isEmpty();
        boolean symbolsPresent = unicodeCategory.getSymbols() != null && unicodeCategory.getSymbols().length != 0;
        assertTrue(rangesPresent || symbolsPresent);
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public static class GenerateTestBase {

        public static Stream<Arguments> getKeyAndCategory() {
            return Arrays.stream(values())
                         .flatMap(uc -> uc.getKeys().stream().map(k -> Arguments.of(k, uc)));

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

        Path makePathFromCategory(UnicodeCategory category) {
            return Paths.get("testdata/" + category.name() + ".txt");
        }

        @SneakyThrows
        Optional<Pattern> compile(String pattern, UnicodeCategory category) {
            Files.deleteIfExists(makePathFromCategory(category));
            try {
                Optional<Pattern> compile = Optional.of(Pattern.compile(pattern));
                testedCategories.add(category);
                return compile;
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        @SneakyThrows
        void validatePattern(Optional<Pattern> compiled, Supplier<String> generateFunction, UnicodeCategory category, boolean expectMatch) {
            String s = assertDoesNotThrow(generateFunction::get);
            //Files.write(makePathFromCategory(category), Collections.singletonList(s), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Set<Character> characterSet = generatedCharacters.computeIfAbsent(category, k -> new HashSet<>());
            for (char c : s.toCharArray()) {
                characterSet.add(c);
            }
            compiled.ifPresent(p -> {
                if (p.matcher(s).matches() == expectMatch) {
                    return;
                }

                boolean[] matches = new boolean[s.length()];
                char[] charArray = s.toCharArray();
                for (int i = 0; i < charArray.length; i++) {
                    char c = charArray[i];
                    matches[i] = p.matcher("" + c).matches();
                }
                System.out.println("Match debug:");
                System.out.println("\t" + s);
                System.out.print("\t");
                for (int i = 0; i < charArray.length; i++) {
                    System.out.print(matches[i] == expectMatch ? " " : '!');
                }
                System.out.println();
                fail("Failed for text '" + s + '\'');
            });
        }

        @AfterEach
        void afterEach() {
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
        void afterAll() {
            List<UnicodeCategory> notTestedCategories = Arrays.stream(values()).filter(category -> !testedCategories.contains(category)).collect(Collectors.toList());
            notTestedCategories.removeAll(UNCOMPILABLE_KEYS);
            if (!notTestedCategories.isEmpty()) {
                fail("Pattern.compile() failed for - " + notTestedCategories);
            }
        }
    }

    @Nested
    public class GenerateInCategoryTest extends GenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateInCategoryTest(String key, UnicodeCategory category) {
            String pattern = "\\p{" + key + "}{5,20}";
            RgxGen rgxGen = new RgxGen(pattern);
            Random random = new Random(pattern.hashCode());
            Optional<Pattern> compiled = compile(pattern, category);
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validatePattern(compiled, () -> rgxGen.generate(random), category, true);
            }
        }
    }

    @Nested
    public class GenerateInCategoryNotMatchingTest extends GenerateTestBase {

        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateInCategoryNotMatchingTest(String key, UnicodeCategory category) {
            String pattern = "\\p{" + key + "}{5,20}";
            RgxGen rgxGen = new RgxGen(pattern);
            Random random = new Random(pattern.hashCode());
            Optional<Pattern> compiled = compile(pattern, category);
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validatePattern(compiled, () -> rgxGen.generateNotMatching(random), category, false);
            }
        }
    }

    @Nested
    public class GenerateUniqueTest extends GenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateUniqueTest(String key, UnicodeCategory category) {
            String pattern = "\\p{" + key + "}{5,20}";
            RgxGen rgxGen = new RgxGen(pattern);
            Random random = new Random(pattern.hashCode());
            StringIterator stringIterator = rgxGen.iterateUnique();
            Optional<Pattern> compiled = compile(pattern, category);
            for (int i = 0; i < GENERATE_ITERATIONS && stringIterator.hasNext(); i++) {
                validatePattern(compiled, stringIterator::next, category, true);
            }
        }
    }

    @Nested
    public class GenerateNotInCategoryTest extends GenerateTestBase {
        @ParameterizedTest(name = "{index}: {0}")
        @MethodSource("getKeyAndCategory")
        void generateNotInCategoryTest(String key, UnicodeCategory category) {
            String pattern = "\\P{" + key + "}{5,20}";
            RgxGen rgxGen = new RgxGen(pattern);
            Optional<Pattern> compiled = compile(pattern, category);
            Random random = new Random(pattern.hashCode());
            for (int i = 0; i < GENERATE_ITERATIONS; i++) {
                validatePattern(compiled, () -> rgxGen.generate(random), category, true);
            }
        }
    }
}