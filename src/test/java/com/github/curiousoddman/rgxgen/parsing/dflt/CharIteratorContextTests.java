package com.github.curiousoddman.rgxgen.parsing.dflt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

@RunWith(Parameterized.class)
public class CharIteratorContextTests {

    private static Stream<Object[]> shorterContext() {
        String inputText = "01234";
        return IntStream.rangeClosed(1, inputText.length())
                        .mapToObj(i -> new Object[]{inputText, i});
    }

    private static Stream<Object[]> longerContext() {
        String inputText = "0123456789ABCDEF";
        return IntStream.rangeClosed(1, inputText.length())
                        .mapToObj(i -> new Object[]{inputText, i});
    }

    @Parameterized.Parameters(name = "{0}:{1}")
    public static Collection<Object[]> data() {
        return Stream.concat(shorterContext(), longerContext())
                     .collect(Collectors.toList());
    }

    @Parameterized.Parameter
    public String aInitialText;
    @Parameterized.Parameter(1)
    public int    aOffset;

    @Test
    public void verifyContextTest() {
        CharIterator iterator = new CharIterator(aInitialText);
        iterator.skip(aOffset);
        String context = iterator.context();
        String[] split = context.split("\n");
        assertEquals("", split[0]);
        int i = split[2].indexOf('^');
        // -1 because actual context is taken from previous symbol, not from actual at index, but previous.
        assertEquals(String.format("%X", aOffset - 1), split[1].substring(i, i + 1));
    }

    @Test
    public void verifyContextWithPosTest() {
        assumeThat(aOffset, not(aInitialText.length()));

        CharIterator iterator = new CharIterator(aInitialText);
        String context = iterator.context(aOffset);
        String[] split = context.split("\n");
        assertEquals("", split[0]);
        int i = split[2].indexOf('^');        // -1 for the ' at the start of previous line
        assertEquals(String.format("%X", aOffset), split[1].substring(i, i + 1));
    }

    @Test
    public void verifyPos() {
        CharIterator iterator = new CharIterator(aInitialText);
        iterator.skip(aOffset);
        assertEquals(aOffset - 1, iterator.prevPos());
    }
}
