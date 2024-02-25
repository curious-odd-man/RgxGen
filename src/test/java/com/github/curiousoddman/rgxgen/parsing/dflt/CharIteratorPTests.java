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


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


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

    public static Stream<Arguments> data() {
        return Stream.of(
                // Mod function, hasNext, remaining, last, prevPos, substrToPos
                Arguments.of(new TestConsumer("Initial"), true, 16, 'F', -1, ""),  // Initial
                Arguments.of(new TestConsumer("Skip", CharIterator::skip), true, 15, 'F', 0, "0"), // Skip 1
                Arguments.of(new TestConsumer("Skip 10", ci -> ci.skip(10)), true, 6, 'F', 9, "0123456789"), // Skip n
                Arguments.of(new TestConsumer("Peek", CharIterator::peek), true, 16, 'F', -1, ""), // peek 0
                Arguments.of(new TestConsumer("Peek 10", ci -> ci.peek(10)), true, 16, 'F', -1, ""), // peek n
                Arguments.of(new TestConsumer("Next", CharIterator::next), true, 15, 'F', 0, "0"), // next
                Arguments.of(new TestConsumer("Next 5", ci -> ci.next(5)), true, 11, 'F', 4, "01234"), // next n
                Arguments.of(new TestConsumer("Next 16", ci -> ci.next(16)), false, 0, 'F', 15, TEST_STRING), // next n
                Arguments.of(new TestConsumer("Context", CharIterator::context), true, 16, 'F', -1, ""),// context
                Arguments.of(new TestConsumer("Context 10", ci -> ci.context(10)), true, 16, 'F', -1, ""),// context n
                Arguments.of(new TestConsumer("Next Until A", ci -> ci.nextUntil('A')), true, 5, 'F', 10, "0123456789A"), // next until char
                Arguments.of(new TestConsumer("Next Until 89A", ci -> ci.nextUntil("89A")), true, 5, 'F', 10, "0123456789A"),  // next until string
                Arguments.of(new TestConsumer("Take While Digit", ci -> ci.takeWhile(Character::isDigit)), true, 6, 'F', 9, "0123456789"), // take while

                // With ModifyBound
                // Mod function, hasNext, remaining, last, prevPos, substrToPos
                Arguments.of(new TestConsumer("Initial", -6), true, 10, '9', -1, ""),  // Initial
                Arguments.of(new TestConsumer("[-6]Skip", CharIterator::skip, -6), true, 9, '9', 0, "0"), // Skip 1
                Arguments.of(new TestConsumer("[-6]Skip 10", ci -> ci.skip(10), -6), false, 0, '9', 9, "0123456789"), // Skip n
                Arguments.of(new TestConsumer("[-6]Peek", CharIterator::peek, -6), true, 10, '9', -1, ""), // peek 0
                Arguments.of(new TestConsumer("[-6]Peek 10", ci -> ci.peek(10), -6), true, 10, '9', -1, ""), // peek n
                Arguments.of(new TestConsumer("[-6]Next", CharIterator::next, -6), true, 9, '9', 0, "0"), // next
                Arguments.of(new TestConsumer("[-6]Next 5", ci -> ci.next(5), -6), true, 5, '9', 4, "01234"), // next n
                Arguments.of(new TestConsumer("[-6]Next 16", ci -> ci.next(16), -6), false, 0, '9', 9, "0123456789"), // next n
                Arguments.of(new TestConsumer("[-6]Context", CharIterator::context, -6), true, 10, '9', -1, ""),// context
                Arguments.of(new TestConsumer("[-6]Context 10", ci -> ci.context(10), -6), true, 10, '9', -1, ""),// context n
                Arguments.of(new TestConsumer("[-6]Next Until A", ci -> ci.nextUntil('A'), -6), false, 0, '9', 9, "0123456789"), // next until char
                Arguments.of(new TestConsumer("[-6]Next Until 89", ci -> ci.nextUntil("89"), -6), false, 0, '9', 9, "0123456789"),  // next until string
                Arguments.of(new TestConsumer("[-6]Next Until 89A", ci -> ci.nextUntil("89A"), -6), false, 0, '9', 9, "0123456789"),  // next until string
                Arguments.of(new TestConsumer("[-6]Take While Digit", ci -> ci.takeWhile(Character::isDigit), -6), false, 0, '9', 9, "0123456789") // take while
        );
    }


    private CharIterator aCharIterator;

    @BeforeEach
    public void setUp() {
        aCharIterator = new CharIterator(TEST_STRING);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void hasNextTest(Consumer<CharIterator> aModFunction,
                            boolean aExpectedHasNext,
                            int aExpectedRemaining,
                            char aExpectedLast,
                            int aExpectedPos,
                            String aExpectedSubstringToCurrPos) {
        aModFunction.accept(aCharIterator);
        assertEquals(aExpectedHasNext, aCharIterator.hasNext());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void expectedRemainingTest(Consumer<CharIterator> aModFunction,
                                      boolean aExpectedHasNext,
                                      int aExpectedRemaining,
                                      char aExpectedLast,
                                      int aExpectedPos,
                                      String aExpectedSubstringToCurrPos) {
        aModFunction.accept(aCharIterator);
        assertEquals(aExpectedRemaining, aCharIterator.remaining());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void lastTest(Consumer<CharIterator> aModFunction,
                         boolean aExpectedHasNext,
                         int aExpectedRemaining,
                         char aExpectedLast,
                         int aExpectedPos,
                         String aExpectedSubstringToCurrPos) {
        aModFunction.accept(aCharIterator);
        assertEquals(aExpectedLast, aCharIterator.lastChar());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void prevPosTest(Consumer<CharIterator> aModFunction,
                            boolean aExpectedHasNext,
                            int aExpectedRemaining,
                            char aExpectedLast,
                            int aExpectedPos,
                            String aExpectedSubstringToCurrPos) {
        aModFunction.accept(aCharIterator);
        assertEquals(aExpectedPos, aCharIterator.prevPos());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void substringToCurrPosTest(Consumer<CharIterator> aModFunction,
                                       boolean aExpectedHasNext,
                                       int aExpectedRemaining,
                                       char aExpectedLast,
                                       int aExpectedPos,
                                       String aExpectedSubstringToCurrPos) {
        aModFunction.accept(aCharIterator);
        assertEquals(aExpectedSubstringToCurrPos, aCharIterator.substringToCurrPos(0));
    }
}
