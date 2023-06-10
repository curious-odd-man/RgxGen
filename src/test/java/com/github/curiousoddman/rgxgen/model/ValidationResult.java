package com.github.curiousoddman.rgxgen.model;

import static org.junit.jupiter.api.Assertions.fail;

public class ValidationResult {
    int countMatched;
    int countNotMatched;

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

    public void assertPassed_90_percent() {
        if (((double) countNotMatched) / (countMatched + countNotMatched) > 0.1) {
            fail("Not matched " + countNotMatched + " vs matched " + countMatched);
        }
    }
}
