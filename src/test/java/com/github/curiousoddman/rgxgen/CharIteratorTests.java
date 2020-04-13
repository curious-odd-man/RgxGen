package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.parsing.dflt.CharIterator;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

public class CharIteratorTests {

    @Test(expected = NoSuchElementException.class)
    public void nextThrowsWhenNoMoreCharsTest() {
        new CharIterator("").next();
    }

    @Test(expected = NoSuchElementException.class)
    public void nextUntilThrowsWhenNoSuchCharacterTest() {
        new CharIterator("aaaaaaaaaaaaa").nextUntil('x');
    }

    @Test(expected = NoSuchElementException.class)
    public void nextUntilThrowsWhenNoSuchCharacter2Test() {
        CharIterator iterator = new CharIterator("aaaaaxaaaaaaa");
        assertEquals("aaaaa", iterator.nextUntil('x'));
        iterator.next();    // Skip 'x' character
        iterator.nextUntil('x');
    }
}
