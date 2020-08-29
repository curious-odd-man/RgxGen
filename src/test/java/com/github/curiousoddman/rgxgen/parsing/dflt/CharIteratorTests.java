package com.github.curiousoddman.rgxgen.parsing.dflt;

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

    @Test
    public void substringToCurrPosTest() {
        CharIterator iterator = new CharIterator("0123456789");
        String s = iterator.substringToCurrPos(0);
        assertEquals("", s);
        iterator.next();
        iterator.next();
        iterator.next();
        s = iterator.substringToCurrPos(0);
        assertEquals("012", s);
        iterator.next();
        s = iterator.substringToCurrPos(0);
        assertEquals("0123", s);
    }
}
