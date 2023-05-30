package com.github.curiousoddman.rgxgen.model;

import lombok.Getter;

import static org.junit.jupiter.api.Assertions.fail;

@Getter
public class ValidationResult {
    int countMatched    = 0;
    int countNotMatched = 0;

    public ValidationResult addMatched() {
        ++countMatched;
        return this;
    }

    public ValidationResult addNotMatched() {
        ++countNotMatched;
        return this;
    }

    public void assertPassed() {
        if (countNotMatched > 0) {
            fail("Not matched " + countNotMatched + " vs matched " + countMatched);
        }
    }
}
