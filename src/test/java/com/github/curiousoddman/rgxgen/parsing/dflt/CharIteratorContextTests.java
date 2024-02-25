package com.github.curiousoddman.rgxgen.parsing.dflt;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class CharIteratorContextTests {

    private static Stream<Arguments> shorterContext() {
        String inputText = "01234";
        return IntStream.rangeClosed(1, inputText.length())
                        .mapToObj(i -> Arguments.of(inputText, i));
    }

    private static Stream<Arguments> longerContext() {
        String inputText = "0123456789ABCDEF";
        return IntStream.rangeClosed(1, inputText.length())
                        .mapToObj(i -> Arguments.of(inputText, i));
    }

    public static Stream<Arguments> data() {
        return Stream.concat(shorterContext(), longerContext());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void verifyContextTest(String aInitialText, int aOffset) {
        CharIterator iterator = new CharIterator(aInitialText);
        iterator.skip(aOffset);
        String context = iterator.context();
        String[] split = context.split("\n");
        assertEquals("", split[0]);
        int i = split[2].indexOf('^');
        // -1 because actual context is taken from previous symbol, not from actual at index, but previous.
        assertEquals(String.format("%X", aOffset - 1), split[1].substring(i, i + 1));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void verifyContextWithPosTest(String aInitialText, int aOffset) {
        assumeFalse(aOffset == aInitialText.length());

        CharIterator iterator = new CharIterator(aInitialText);
        String context = iterator.context(aOffset);
        String[] split = context.split("\n");
        assertEquals("", split[0]);
        int i = split[2].indexOf('^');        // -1 for the ' at the start of previous line
        assertEquals(String.format("%X", aOffset), split[1].substring(i, i + 1));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void verifyPos(String aInitialText, int aOffset) {
        CharIterator iterator = new CharIterator(aInitialText);
        iterator.skip(aOffset);
        assertEquals(aOffset - 1, iterator.prevPos());
    }
}
