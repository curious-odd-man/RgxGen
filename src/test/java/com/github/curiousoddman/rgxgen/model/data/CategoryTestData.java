package com.github.curiousoddman.rgxgen.model.data;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.UnicodeCategoryGenerateTestBase.wrapInCurvy;

public class CategoryTestData {
    private final String          key;
    private final UnicodeCategory category;
    private final Pattern         inCategoryPattern;
    private final Pattern         notInCategoryPattern;

    public static CategoryTestData create(UnicodeCategory category) {
        List<String> keys = category
                .getKeys()
                .stream()
                .flatMap(k -> k.length() == 1 ? Stream.of(k, wrapInCurvy(k)) : Stream.of(wrapInCurvy(k)))
                .collect(Collectors.toList());
        for (String key : keys) {
            try {
                return new CategoryTestData(
                        key,
                        category,
                        Pattern.compile("\\p" + key + "{30}"),
                        Pattern.compile("\\P" + key + "{30}"));
            } catch (Exception ignored) {
            }
        }
        throw new IllegalArgumentException("Couldn't compile pattern for category: " + category);
    }

    public CategoryTestData(String key, UnicodeCategory category, Pattern inCategoryPattern, Pattern notInCategoryPattern) {
        this.key = key;
        this.category = category;
        this.inCategoryPattern = inCategoryPattern;
        this.notInCategoryPattern = notInCategoryPattern;
    }

    public String getKey() {
        return key;
    }

    public UnicodeCategory getCategory() {
        return category;
    }

    public Pattern getInCategoryPattern() {
        return inCategoryPattern;
    }

    public Pattern getNotInCategoryPattern() {
        return notInCategoryPattern;
    }

    public Stream<Character> getCategoryCharacters() {
        return Stream.concat(
                Arrays.stream(category.getSymbols()),
                category.getSymbolRanges().stream().flatMap(SymbolRange::chars)
        );
    }

    @Override
    public String toString() {
        return key;
    }
}
