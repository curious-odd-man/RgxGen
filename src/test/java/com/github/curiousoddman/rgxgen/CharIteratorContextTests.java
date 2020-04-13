package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.parsing.dflt.CharIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CharIteratorContextTests {

    private static Stream<Object[]> shorterContext() {
        String inputText = "01234";
        return IntStream.range(0, inputText.length())
                        .mapToObj(i -> new Object[]{inputText, i});
    }

    private static Stream<Object[]> longerContext() {
        String inputText = "0123456789ABCDEF";
        return IntStream.range(0, inputText.length())
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
        final String context = iterator.context();
        final String[] split = context.split("\n");
        assertEquals("", split[0]);
        final int i = split[2].indexOf('^');        // -1 for the ' at the start of previous line
        System.out.println(context);
        assertEquals(String.format("%X", aOffset), split[1].substring(i, i + 1));
    }
}
