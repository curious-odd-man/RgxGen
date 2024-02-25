package com.github.curiousoddman.rgxgen.util.chars;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.makeAsciiCharacterArray;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CharArrayListTest {
    @Nested
    public class CreateFromSymgolRangesAndSymbols {
        @Test
        void createFromRangesTest() {
            CharList charList = CharList.charList(Arrays.asList(SymbolRange.range('A', 'C'), SymbolRange.range('E', 'F')));
            assertEquals(5, charList.size());
            assertEquals('A', charList.get(0));
            assertEquals('B', charList.get(1));
            assertEquals('C', charList.get(2));
            assertEquals('E', charList.get(3));
            assertEquals('F', charList.get(4));
        }

        @Test
        void createFromSymbolsTest() {
            CharList charList = CharList.charList(emptyList(), 'A', 'B');
            assertEquals(2, charList.size());
            assertEquals('A', charList.get(0));
            assertEquals('B', charList.get(1));
        }

        @Test
        void createFromRangesAndSymbolsTest() {
            CharList charList = CharList.charList(Collections.singletonList(SymbolRange.range('A', 'C')), 'E', 'F');
            assertEquals(5, charList.size());
            assertEquals('E', charList.get(0));
            assertEquals('F', charList.get(1));
            assertEquals('A', charList.get(2));
            assertEquals('B', charList.get(3));
            assertEquals('C', charList.get(4));

        }
    }

    @Test
    void createFromRangeTest() {
        CharList charList = CharList.rangeClosed('A', 'C');
        assertEquals(3, charList.size());
        assertEquals('A', charList.get(0));
        assertEquals('B', charList.get(1));
        assertEquals('C', charList.get(2));
    }

    @Test
    void copyIsDeepTest() {
        CharList charList = CharList.rangeClosed('A', 'C');
        CharList copy = charList.copy();
        copy.add('X');

        assertEquals(3, charList.size());
        assertEquals('A', charList.get(0));
        assertEquals('B', charList.get(1));
        assertEquals('C', charList.get(2));

        assertEquals(4, copy.size());
        assertEquals('A', copy.get(0));
        assertEquals('B', copy.get(1));
        assertEquals('C', copy.get(2));
        assertEquals('X', copy.get(3));
    }

    @Test
    void addTest() {
        CharList charList = CharList.empty();
        char[] chars = makeAsciiCharacterArray();
        for (int i = 0; i < chars.length; i++) {
            charList.add(chars[i]);
            assertEquals(i + 1, charList.size());
            for (int j = 0; j < i; j++) {
                assertEquals(chars[i], charList.get(i));
            }
        }
    }

    private static final char[] INPUT       = {'A', 'B', 'C'};
    private static final char[] LARGE_INPUT = makeAsciiCharacterArray();

    public static Stream<Arguments> addAllTestData() {
        return Stream.of(
                arguments("empty + arr", CharList.empty(), INPUT, INPUT.length, new char[]{'A', 'B', 'C'}),
                arguments("empty + arr[1]", CharList.empty(), INPUT, 1, new char[]{'A'}),
                arguments("non empty + arr", CharList.charList('X', 'Y', 'Z'), INPUT, 1, new char[]{'X', 'Y', 'Z', 'A'}),
                arguments("non empty + arr[2]", CharList.charList('X', 'Y', 'Z'), INPUT, 2, new char[]{'X', 'Y', 'Z', 'A', 'B'}),
                arguments("non empty + large", CharList.charList('X', 'Y', 'Z'), LARGE_INPUT, LARGE_INPUT.length, getLargeExpected())
        );
    }

    private static char[] getLargeExpected() {
        char[] ascii = makeAsciiCharacterArray();
        char[] result = new char[ascii.length + 3];
        result[0] = 'X';
        result[1] = 'Y';
        result[2] = 'Z';
        System.arraycopy(ascii, 0, result, 3, ascii.length);
        return result;
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("addAllTestData")
    void addAllTest(String name, CharList init, char[] srcArr, int srcCount, char[] expected) {
        init.addAll(srcArr, srcCount);
        assertEquals(expected.length, init.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], init.get(i));
        }
    }

    @Test
    void sortTest() {
        CharList charList = CharList.charList('C', 'B', 'A');
        charList.sort();
        assertEquals(3, charList.size());
        assertEquals('A', charList.get(0));
        assertEquals('B', charList.get(1));
        assertEquals('C', charList.get(2));
    }

    @Test
    void appendToTest() {
        CharList src = CharList.charList('C', 'B', 'A');
        CharList dst = CharList.charList('X', 'Y', 'Z');

        src.appendTo(dst);

        assertEquals('C', src.get(0));
        assertEquals('B', src.get(1));
        assertEquals('A', src.get(2));

        assertEquals('X', dst.get(0));
        assertEquals('Y', dst.get(1));
        assertEquals('Z', dst.get(2));
        assertEquals('C', dst.get(3));
        assertEquals('B', dst.get(4));
        assertEquals('A', dst.get(5));
    }

    @Test
    void exceptTest() {
        CharList charList = CharList.rangeClosed('A', 'F');
        CharList except = charList.except(ch -> Arrays.asList('B', 'C', 'D').contains(ch));
        assertEquals(6, charList.size());
        assertEquals(3, except.size());
        assertEquals('A', except.get(0));
        assertEquals('E', except.get(1));
        assertEquals('F', except.get(2));
    }
}