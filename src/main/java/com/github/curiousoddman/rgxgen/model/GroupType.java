package com.github.curiousoddman.rgxgen.model;

public enum GroupType {
    POSITIVE_LOOKAHEAD,
    NEGATIVE_LOOKAHEAD,
    POSITIVE_LOOKBEHIND,
    NEGATIVE_LOOKBEHIND,
    CAPTURE_GROUP,
    NON_CAPTURE_GROUP;

    public boolean isNegative() {
        return this == NEGATIVE_LOOKAHEAD || this == NEGATIVE_LOOKBEHIND;
    }
}
