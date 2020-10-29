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


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CharIteratorPTests {
    private static class TestConsumer implements Consumer<CharIterator> {
        private final Consumer<CharIterator> aConsumer;
        private final String                 aName;
        private final int                    aBoundOffset;


        public TestConsumer(String name) {
            this(name, x -> {
            });
        }

        public TestConsumer(String name, int boundOffset) {
            this(name, x -> {
            }, boundOffset);
        }

        public TestConsumer(String name, Consumer<CharIterator> consumer) {
            this(name, consumer, 0);
        }

        public TestConsumer(String name, Consumer<CharIterator> consumer, int boundOffset) {
            aConsumer = consumer;
            aName = name;
            aBoundOffset = boundOffset;
        }

        @Override
        public void accept(CharIterator charIterator) {
            if (aBoundOffset != 0) {
                charIterator.modifyBound(aBoundOffset);
            }
            aConsumer.accept(charIterator);
        }

        @Override
        public String toString() {
            return aName;
        }
    }

    private static final String TEST_STRING = "0123456789ABCDEF";

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // Mod function, hasNext, remaining, last, prevPos, substrToPos
                {new TestConsumer("Initial"), true, 16, 'F', -1, ""},  // Initial
                {new TestConsumer("Skip", CharIterator::skip), true, 15, 'F', 0, "0"}, // Skip 1
                {new TestConsumer("Skip 10", ci -> ci.skip(10)), true, 6, 'F', 9, "0123456789"}, // Skip n
                {new TestConsumer("Peek", CharIterator::peek), true, 16, 'F', -1, ""}, // peek 0
                {new TestConsumer("Peek 10", ci -> ci.peek(10)), true, 16, 'F', -1, ""}, // peek n
                {new TestConsumer("Next", CharIterator::next), true, 15, 'F', 0, "0"}, // next
                {new TestConsumer("Next 5", ci -> ci.next(5)), true, 11, 'F', 4, "01234"}, // next n
                {new TestConsumer("Next 16", ci -> ci.next(16)), false, 0, 'F', 15, TEST_STRING}, // next n
                {new TestConsumer("Context", CharIterator::context), true, 16, 'F', -1, ""},// context
                {new TestConsumer("Context 10", ci -> ci.context(10)), true, 16, 'F', -1, ""},// context n
                {new TestConsumer("Next Until A", ci -> ci.nextUntil('A')), true, 5, 'F', 10, "0123456789A"}, // next until char
                {new TestConsumer("Next Until 89A", ci -> ci.nextUntil("89A")), true, 5, 'F', 10, "0123456789A"},  // next until string
                {new TestConsumer("Take While Digit", ci -> ci.takeWhile(Character::isDigit)), true, 6, 'F', 9, "0123456789"}, // take while

                // With ModifyBound
                // Mod function, hasNext, remaining, last, prevPos, substrToPos
                {new TestConsumer("Initial", -6), true, 10, '9', -1, ""},  // Initial
                {new TestConsumer("[-6]Skip", CharIterator::skip, -6), true, 9, '9', 0, "0"}, // Skip 1
                {new TestConsumer("[-6]Skip 10", ci -> ci.skip(10), -6), false, 0, '9', 9, "0123456789"}, // Skip n
                {new TestConsumer("[-6]Peek", CharIterator::peek, -6), true, 10, '9', -1, ""}, // peek 0
                {new TestConsumer("[-6]Peek 10", ci -> ci.peek(10), -6), true, 10, '9', -1, ""}, // peek n
                {new TestConsumer("[-6]Next", CharIterator::next, -6), true, 9, '9', 0, "0"}, // next
                {new TestConsumer("[-6]Next 5", ci -> ci.next(5), -6), true, 5, '9', 4, "01234"}, // next n
                {new TestConsumer("[-6]Next 16", ci -> ci.next(16), -6), false, 0, '9', 9, "0123456789"}, // next n
                {new TestConsumer("[-6]Context", CharIterator::context, -6), true, 10, '9', -1, ""},// context
                {new TestConsumer("[-6]Context 10", ci -> ci.context(10), -6), true, 10, '9', -1, ""},// context n
                {new TestConsumer("[-6]Next Until A", ci -> ci.nextUntil('A'), -6), false, 0, '9', 9, "0123456789"}, // next until char
                {new TestConsumer("[-6]Next Until 89", ci -> ci.nextUntil("89"), -6), false, 0, '9', 9, "0123456789"},  // next until string
                {new TestConsumer("[-6]Next Until 89A", ci -> ci.nextUntil("89A"), -6), false, 0, '9', 9, "0123456789"},  // next until string
                {new TestConsumer("[-6]Take While Digit", ci -> ci.takeWhile(Character::isDigit), -6), false, 0, '9', 9, "0123456789"}, // take while
        });
    }

    @Parameterized.Parameter(0)
    public Consumer<CharIterator> aModFunction;

    @Parameterized.Parameter(1)
    public boolean aExpectedHasNext;

    @Parameterized.Parameter(2)
    public int aExpectedRemaining;

    @Parameterized.Parameter(3)
    public char aExpectedLast;

    @Parameterized.Parameter(4)
    public int aExpectedPos;

    @Parameterized.Parameter(5)
    public String aExpectedSubstringToCurrPos;

    private CharIterator aCharIterator;

    @Before
    public void setUp() {
        aCharIterator = new CharIterator(TEST_STRING);
        aModFunction.accept(aCharIterator);
    }

    @Test
    public void hasNextTest() {
        assertEquals(aExpectedHasNext, aCharIterator.hasNext());
    }

    @Test
    public void expectedRemainingTest() {
        assertEquals(aExpectedRemaining, aCharIterator.remaining());
    }

    @Test
    public void lastTest() {
        assertEquals(aExpectedLast, aCharIterator.lastChar());
    }

    @Test
    public void prevPosTest() {
        assertEquals(aExpectedPos, aCharIterator.prevPos());
    }

    @Test
    public void substringToCurrPosTest() {
        assertEquals(aExpectedSubstringToCurrPos, aCharIterator.substringToCurrPos(0));
    }
}
