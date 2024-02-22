package com.github.curiousoddman.rgxgen.model.data;

public class CategoryLetterTestData {
    private final CategoryTestData categoryTestData;
    private final char             c;

    public CategoryLetterTestData(CategoryTestData categoryTestData, char c) {
        this.categoryTestData = categoryTestData;
        this.c = c;
    }

    public CategoryTestData getCategoryTestData() {
        return categoryTestData;
    }

    public char getChar() {
        return c;
    }

    @Override
    public String toString() {
        return categoryTestData.toString() + ':' + (int) c;
    }
}
