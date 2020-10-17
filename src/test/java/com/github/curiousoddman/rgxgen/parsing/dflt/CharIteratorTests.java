package com.github.curiousoddman.rgxgen.parsing.dflt;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
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

    @Test
    public void substringUntilTest() {
        List<Object[]> data = Arrays.asList(
                new Object[]{"()", 1, ')', ""},
                new Object[]{"(a)", 1, ')', "a"},
                new Object[]{"(a\\)b)", 1, ')', "a\\)b"},
                new Object[]{"(ac\\\\)", 1, ')', "ac\\\\"}
        );

        for (Object[] datum : data) {
            CharIterator charIterator = new CharIterator(datum[0].toString());
            charIterator.next((int) datum[1]);
            assertEquals(Arrays.toString(datum), datum[3], charIterator.nextUntil((char) datum[2]));
        }
    }

    @Test
    public void takeWhileTest() {
        List<Object[]> data = Arrays.asList(
                new Object[]{"asdf1234fdsa", 4, "1234"},
                new Object[]{"asdf1234", 4, "1234"}
        );

        for (Object[] datum : data) {
            CharIterator charIterator = new CharIterator(datum[0].toString());
            charIterator.next((int) datum[1]);
            assertEquals(Arrays.toString(datum), datum[2], charIterator.takeWhile(Character::isDigit));
        }
    }

    @Test
    public void nextUntilStringMinTest() {
        CharIterator charIterator = new CharIterator("m\\E");
        assertEquals("m", charIterator.nextUntil("\\E"));
        assertEquals(3, charIterator.pos());
    }

    @Test
    public void nextUntilStringEscapedTest() {
        CharIterator charIterator = new CharIterator("m\\\\Ezxc");
        assertEquals("m\\\\Ezxc", charIterator.nextUntil("\\E"));
        assertEquals(7, charIterator.pos());
    }

    @Test
    public void nextUntilStringEscaped2Test() {
        CharIterator charIterator = new CharIterator("m\\Ezxc");
        assertEquals("m\\Ezxc", charIterator.nextUntil("E"));
        assertEquals(3, charIterator.pos());
    }

    @Test
    public void nextUntilStringWithSuffixTest() {
        CharIterator charIterator = new CharIterator("m\\Exa");
        assertEquals("m", charIterator.nextUntil("\\E"));
        assertEquals(3, charIterator.pos());
    }

    @Test
    public void nextUntilStringMissingTest() {
        CharIterator charIterator = new CharIterator("masd");
        assertEquals("masd", charIterator.nextUntil("\\E"));
        assertEquals(3, charIterator.pos());
    }

    @Test
    public void nextUntilStringBoundIsRespectedTest() {
        CharIterator charIterator = new CharIterator("masd$xxx");
        charIterator.setBound(4);
        assertEquals("masd", charIterator.nextUntil("E"));
        assertEquals(3, charIterator.pos());
    }
}
