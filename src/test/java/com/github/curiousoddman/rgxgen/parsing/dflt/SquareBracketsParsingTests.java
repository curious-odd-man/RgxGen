package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.nodes.SymbolSet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class SquareBracketsParsingTests {

    private static SymbolSet mkSS(Character... chars) {
        return SymbolSet.ofAsciiCharacters(Arrays.toString(chars), chars, MatchType.POSITIVE);
    }

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("[a-c]", mkSS('a', 'b', 'c')),
                Arguments.of("[a-c-]", mkSS('a', 'b', 'c', '-')),
                Arguments.of("[a-c-x]", mkSS('a', 'b', 'c', '-', 'x')),
                Arguments.of("[-a-c]", mkSS('-', 'a', 'b', 'c')),
                Arguments.of("[\\x30-\\x{0032}]", mkSS('0', '1', '2')),
                Arguments.of("[\\s-]", mkSS('\r', '\n', '\t', '\f', '\u000B', ' ', '-')),
                Arguments.of("[-]", mkSS('-')),
                Arguments.of("[\\s-a-\\s]", new RgxGenParseException("Cannot make range with a shorthand escape sequences before '\n" +
                                                                             "'s-a-\\s]'\n" +
                                                                             "      ^'")),
                Arguments.of("[\\s-a]", mkSS('\r', '\n', '\t', '\f', '\u000B', ' ', '-', 'a')),
                Arguments.of("[\\s]", mkSS('\r', '\n', '\t', '\f', '\u000B', ' ')),
                Arguments.of("[a-]", mkSS('a', '-')));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void parsingTest(String aPattern, Object aExpected) {
        try {
            DefaultTreeBuilder builder = new DefaultTreeBuilder(aPattern, null);
            Node node = builder.get();
            assertEquals(aExpected.toString(), node.toString());
        } catch (RgxGenParseException e) {
            if (aExpected instanceof Throwable) {
                assertEquals(e.getMessage(), ((Throwable) aExpected).getMessage(), e.getMessage());
            } else {
                e.printStackTrace();
                fail("Got exception when expected SymbolSet. " + e.getMessage());
            }
        }
    }
}
