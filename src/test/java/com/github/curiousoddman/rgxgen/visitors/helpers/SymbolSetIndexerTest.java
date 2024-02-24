package com.github.curiousoddman.rgxgen.visitors.helpers;

import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.nodes.SymbolSet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SymbolSetIndexerTest {

    public static final SymbolRange RANGE_OUTSIDE_OF_SYMBOLS = range(0x1000, 0x2000);

    public static Stream<Arguments> getSymbolSets() {
        return Stream.of(
                arguments("Single Range", symbolSet(range('a', 'd')), 4, asList('a', 'b', 'c', 'd')),
                arguments("Several Ranges", symbolSet(range('a', 'd'), range('k', 'l'), range('x', 'z')), 9, asList('a', 'b', 'c', 'd', 'k', 'l', 'x', 'y', 'z')),
                arguments("Several characters", symbolSet('a', 'd', 'k', 't', 'x', 'z'), 6, asList('a', 'd', 'k', 't', 'x', 'z')),
                arguments("Characters and ranges", symbolSet(asList(range('a', 'd'), range('x', 'z')), '1', '2', '3', '4'), 11, asList('1', '2', '3', '4', 'a', 'b', 'c', 'd', 'x', 'y', 'z')),
                arguments("Negative Ranges", negativeSymbolSet(asList(range(SPACE_ASCII_CODE, DEL_ASCII_CODE - 2), RANGE_OUTSIDE_OF_SYMBOLS)), 1, Collections.singletonList((char) (DEL_ASCII_CODE - 1)))
        );
    }

    private static SymbolSet symbolSet(SymbolRange... symbolRanges) {
        return SymbolSet.ofAscii("", asList(symbolRanges), ZERO_LENGTH_CHARACTER_ARRAY, MatchType.POSITIVE);
    }

    private static SymbolSet symbolSet(Character... characters) {
        return SymbolSet.ofAscii("", emptyList(), characters, MatchType.POSITIVE);
    }

    private static SymbolSet symbolSet(List<SymbolRange> ranges, Character... characters) {
        return SymbolSet.ofAscii("", ranges, characters, MatchType.POSITIVE);
    }

    private static SymbolSet negativeSymbolSet(List<SymbolRange> ranges) {
        return SymbolSet.ofAscii("", ranges, ZERO_LENGTH_CHARACTER_ARRAY, MatchType.NEGATIVE);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getSymbolSets")
    void sizeTest(String name, SymbolSet symbolSet, int expectedSize, List<Character> expectedCharacters) {
        assertEquals(expectedSize, symbolSet.getSymbolSetIndexer().size());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getSymbolSets")
    void getTest(String name, SymbolSet symbolSet, int expectedSize, List<Character> expectedCharacters) {
        SymbolSetIndexer symbolSetIndexer = symbolSet.getSymbolSetIndexer();
        Boolean[] matched = new Boolean[symbolSetIndexer.size()];
        char[] actualCharacters = new char[symbolSetIndexer.size()];
        for (int i = 0; i < symbolSetIndexer.size(); i++) {
            actualCharacters[i] = symbolSetIndexer.get(i);
            matched[i] = expectedCharacters.get(i).equals(actualCharacters[i]);
        }

        if (!Arrays.stream(matched).allMatch(x -> x)) {
            System.out.println(expectedCharacters);
            System.out.println(Arrays.stream(matched).map(b -> b ? " " : "!").collect(Collectors.toList()));
            System.out.println(Arrays.toString(actualCharacters));
            fail("There were mismatched between generated and expected characters");
        }
    }
}