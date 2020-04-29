package com.github.curiousoddman.rgxgen.simplifier;

public class PatternDoesntMatchAnyStringException extends Exception {
    public PatternDoesntMatchAnyStringException(String message) {
        super(message);
    }
}
