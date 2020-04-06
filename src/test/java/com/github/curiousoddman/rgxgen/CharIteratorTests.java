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

    @Test
    public void contextFromStartOfStringTest() {
        assertEquals("Sampl at 0", new CharIterator("Sample text").context());
    }

    @Test
    public void contextInTheMiddleOfStringTest() {
        CharIterator charIterator = new CharIterator("Sample textSample text");
        charIterator.skip(8);
        assertEquals("ple textSa at 8", charIterator.context());
    }

    @Test
    public void contextInTheEndOfStringTest() {
        CharIterator charIterator = new CharIterator("Sample text");
        charIterator.skip(10);
        assertEquals("e text at 10", charIterator.context());
    }

    @Test(expected = RuntimeException.class)
    public void nextUntilThrowsWhenNoSuchCharacterTest() {
        new CharIterator("aaaaaaaaaaaaa").nextUntil('x');
    }

    @Test(expected = RuntimeException.class)
    public void nextUntilThrowsWhenNoSuchCharacter2Test() {
        CharIterator iterator = new CharIterator("aaaaaxaaaaaaa");
        assertEquals("aaaaa", iterator.nextUntil('x'));
        iterator.next();    // Skip 'x' character
        iterator.nextUntil('x');
    }
}
