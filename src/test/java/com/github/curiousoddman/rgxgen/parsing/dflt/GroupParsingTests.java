package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.nodes.Group;
import com.github.curiousoddman.rgxgen.nodes.Node;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class GroupParsingTests {

    public static Stream<Arguments> data() {
        return Stream.of(
                // Invalid group
                arguments("(?123)", new RgxGenParseException("Incomplete group structure: \n'(?123)'\n   ^")),
                arguments("(?<123)", new RgxGenParseException("Incomplete group structure: \n'(?<123)'\n   ^")),
                arguments("(?<123\\>)", new RgxGenParseException("Incomplete group structure: \n'(?<123\\'\n   ^")),
                arguments("(?'123)", new RgxGenParseException("Incomplete group structure: \n'(?'123)'\n   ^")),
                // Valid group
                arguments("(\\?123)", new Group("(\\?123)", 1, new FinalSymbol("?123"))),
                arguments("(?<name>123)", new Group("(?<name>123)", 1, new FinalSymbol("123"))),
                arguments("(?<name\\>x>123)", new Group("(?<name\\>x>123)", 1, new FinalSymbol("123")))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void parsingTest(String pattern, Object expected) {
        try {
            DefaultTreeBuilder builder = new DefaultTreeBuilder(pattern, new DefaultNodeCreator(), null);
            Node node = builder.get();
            assertEquals(expected.toString(), node.toString());
        } catch (RgxGenParseException e) {
            if (expected instanceof Throwable) {
                assertEquals(e.getMessage(), ((Throwable) expected).getMessage(), e.getMessage());
            } else {
                fail("Got exception when not expected. ", e);
            }
        }
    }
}
