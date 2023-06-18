package com.github.curiousoddman.rgxgen.model;

public enum WhitespaceChar {
    SPACE(' '),
    TAB('\t'),
    CARRIAGE_RETUR('\r'),
    LINE_FEED('\n'),
    VERTICAL_TAB('\u000B'),
    FORM_FEED('\f');

    private final Character c;

    WhitespaceChar(char c) {
        this.c = c;
    }

    public Character get() {
        return c;
    }
}
