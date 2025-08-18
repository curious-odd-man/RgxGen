package com.github.curiousoddman.rgxgen.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SymbolRangeTest {

    @Test
    void testEquals() {
        assertEquals(SymbolRange.range(0, 5), SymbolRange.range(0, 5));
    }

    @Test
    void testHashCode() {
        assertEquals(
                SymbolRange.range('a', 'z').hashCode(),
                SymbolRange.range('a', 'z').hashCode()
        );
    }
}