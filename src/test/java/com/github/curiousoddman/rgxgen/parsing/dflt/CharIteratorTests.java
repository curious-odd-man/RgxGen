package com.github.curiousoddman.rgxgen.parsing.dflt;

/* **************************************************************************
   Copyright 2019 Vladislavs Varslavans

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
/* **************************************************************************/


import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

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
        CharIterator charIterator = new CharIterator("aaaaaxbbbbbbb");
        assertEquals("aaaaa", charIterator.nextUntil('x'));
        assertEquals(7, charIterator.remaining());
        charIterator.next();    // Skip 'x' character
        assertEquals("bbbbbbb", charIterator.nextUntil('x'));
    }

    @Test
    public void substringToCurrPosTest() {
        CharIterator charIterator = new CharIterator("0123456789");
        String s = charIterator.substringToCurrPos(0);
        assertEquals(10, charIterator.remaining());
        assertEquals("", s);
        charIterator.next();
        charIterator.next();
        charIterator.next();
        s = charIterator.substringToCurrPos(0);
        assertEquals(7, charIterator.remaining());
        assertEquals("012", s);
        charIterator.next();
        s = charIterator.substringToCurrPos(0);
        assertEquals(6, charIterator.remaining());
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
        assertEquals(2, charIterator.prevPos());
    }

    @Test
    public void nextUntilEscapedStringEscapedTest() {
        CharIterator charIterator = new CharIterator("m\\\\Ezxc");
        assertEquals("m\\\\Ezxc", charIterator.nextUntil("\\E"));
        assertEquals(0, charIterator.remaining());
        assertFalse(charIterator.hasNext());
    }

    @Test
    public void nextUntilStringEscapedTest() {
        CharIterator charIterator = new CharIterator("m\\Ezxc");
        assertEquals("m\\Ezxc", charIterator.nextUntil("E"));
        assertEquals(5, charIterator.prevPos());
        assertEquals(0, charIterator.remaining());
        assertFalse(charIterator.hasNext());
    }

    @Test
    public void nextUntilStringWithSuffixTest() {
        CharIterator charIterator = new CharIterator("m\\Exa");
        assertEquals("m", charIterator.nextUntil("\\E"));
        assertEquals(2, charIterator.prevPos());
    }

    @Test
    public void nextUntilStringMissingTest() {
        CharIterator charIterator = new CharIterator("masd");
        assertEquals("masd", charIterator.nextUntil("\\E"));
        assertEquals(3, charIterator.prevPos());
        assertEquals(0, charIterator.remaining());
        assertFalse(charIterator.hasNext());
    }

    @Test
    public void nextUntilStringBoundIsRespectedTest() {
        CharIterator charIterator = new CharIterator("masd$xxx");
        charIterator.modifyBound(-4);
        assertEquals("masd", charIterator.nextUntil("E"));
        assertEquals(3, charIterator.prevPos());
        assertEquals(0, charIterator.remaining());
        assertFalse(charIterator.hasNext());
    }

    @Test
    public void nextUntilStringSpecialCaseTest() {
        CharIterator charIterator = new CharIterator("[a]\\1(a|c).*\\W\\Ezxc");
        assertEquals("[a]\\1(a|c).*\\W", charIterator.nextUntil("\\E"));
        assertEquals(15, charIterator.prevPos());
        assertEquals(3, charIterator.remaining());
    }

    @Test
    public void nextUntilStringSpecialCase2Test() {
        CharIterator charIterator = new CharIterator("[a]\\1(a|c).*\\W\\E");
        assertEquals("[a]\\1(a|c).*\\W", charIterator.nextUntil("\\E"));
        assertEquals(15, charIterator.prevPos());
        assertEquals(0, charIterator.remaining());
    }

    @Test
    public void peekReturnsNullByteWhenOutOfRangeTest() {
        String text = "012345";
        CharIterator charIterator = new CharIterator(text);
        char[] expectedChars = {0x00, '0', '1', '2', '3', '4', '5', 0x00};
        charIterator.skip(3);
        for (int i = 0; i < text.length() + 2; i++) {
            assertEquals(expectedChars[i], charIterator.peek(i - 4));
        }
    }
}
