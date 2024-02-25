package com.github.curiousoddman.rgxgen.visitors.helpers;

import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.nodes.SymbolSet;
import com.github.curiousoddman.rgxgen.util.chars.CharList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.DEL_ASCII_CODE;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.SPACE_ASCII_CODE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SymbolSetIndexerTest {

    public static final SymbolRange RANGE_OUTSIDE_OF_SYMBOLS = range(0x1000, 0x2000);

    public static Stream<Arguments> getSymbolSets() {
        return Stream.of(
                arguments("Single Range", symbolSet(range('a', 'd')), 4, CharList.charList('a', 'b', 'c', 'd')),
                arguments("Several Ranges", symbolSet(range('a', 'd'), range('k', 'l'), range('x', 'z')), 9, CharList.charList('a', 'b', 'c', 'd', 'k', 'l', 'x', 'y', 'z')),
                arguments("Several characters", symbolSet('a', 'd', 'k', 't', 'x', 'z'), 6, CharList.charList('a', 'd', 'k', 't', 'x', 'z')),
                arguments("Characters and ranges", symbolSet(asList(range('a', 'd'), range('x', 'z')), '1', '2', '3', '4'), 11, CharList.charList('1', '2', '3', '4', 'a', 'b', 'c', 'd', 'x', 'y', 'z')),
                arguments("Negative Ranges", negativeSymbolSet(asList(range(SPACE_ASCII_CODE, DEL_ASCII_CODE - 2), RANGE_OUTSIDE_OF_SYMBOLS)), 1, CharList.charList((char) (DEL_ASCII_CODE - 1)))
        );
    }

    private static SymbolSet symbolSet(SymbolRange... symbolRanges) {
        return SymbolSet.ofAscii("", asList(symbolRanges), CharList.empty(), MatchType.POSITIVE);
    }

    private static SymbolSet symbolSet(char... characters) {
        return SymbolSet.ofAscii("", emptyList(), CharList.charList(characters), MatchType.POSITIVE);
    }

    private static SymbolSet symbolSet(List<SymbolRange> ranges, char... characters) {
        return SymbolSet.ofAscii("", ranges, CharList.charList(characters), MatchType.POSITIVE);
    }

    private static SymbolSet negativeSymbolSet(List<SymbolRange> ranges) {
        return SymbolSet.ofAscii("", ranges, CharList.empty(), MatchType.NEGATIVE);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getSymbolSets")
    void sizeTest(String name, SymbolSet symbolSet, int expectedSize, CharList expectedCharacters) {
        assertEquals(expectedSize, symbolSet.getSymbolSetIndexer().size());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getSymbolSets")
    void getTest(String name, SymbolSet symbolSet, int expectedSize, CharList expectedCharacters) {
        SymbolSetIndexer symbolSetIndexer = symbolSet.getSymbolSetIndexer();
        Boolean[] matched = new Boolean[symbolSetIndexer.size()];
        char[] actualCharacters = new char[symbolSetIndexer.size()];
        for (int i = 0; i < symbolSetIndexer.size(); i++) {
            actualCharacters[i] = symbolSetIndexer.get(i);
            matched[i] = expectedCharacters.get(i) == actualCharacters[i];
        }

        if (!Arrays.stream(matched).allMatch(x -> x)) {
            System.out.println(expectedCharacters);
            System.out.println(Arrays.stream(matched).map(b -> b ? " " : "!").collect(Collectors.toList()));
            System.out.println(Arrays.toString(actualCharacters));
            fail("There were mismatched between generated and expected characters");
        }
    }
}