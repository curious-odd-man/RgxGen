package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import com.github.curiousoddman.rgxgen.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.visitors.NotMatchingGenerationVisitor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class NotMatchingGenerationTests {

    private static Collection<Object[]> initialData() {
        return Arrays.asList(new Object[][]{
                {"[a-z0-5]", SymbolSet.ofAsciiRanges("[a-z0-5]", Arrays.asList(SymbolRange.range('a', 'z'), SymbolRange.range('0', '5')), MatchType.POSITIVE)},
                {"abc|def", new Choice("abc|def", new FinalSymbol("abc"), new FinalSymbol("def"))},
                {"helloworld", new FinalSymbol("helloworld")},
                {"a{2,3}", new Repeat("a{2,3}", new FinalSymbol("a"), 2, 3)},
                {"a[a-z]", new Sequence("a[a-z]", new FinalSymbol("a"),
                                        SymbolSet.ofAsciiRanges("[a-z]", Collections.singletonList(SymbolRange.range('a', 'z')), MatchType.POSITIVE))},
                {"([a-z])\\1", new Sequence("([a-z])\\1", new Group("([a-z])", 1,
                                                                    SymbolSet.ofAsciiRanges("[a-z]", Collections.singletonList(SymbolRange.range('a', 'z')), MatchType.POSITIVE)),
                                            new GroupRef("\\1", 1)
                )},
                {"foo(?!bar)", new Sequence("foo(?!bar)",
                                            new FinalSymbol("foo"),
                                            new NotSymbol("bar", new FinalSymbol("bar"))
                )}
        });
    }

    public static Stream<Arguments> getTestData() {
        return initialData()
                .stream()
                .flatMap(arr -> IntStream.range(0, 100)
                                         .mapToObj(i -> Arguments.of(arr[0], arr[1], i)));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void nodeVisitingWorksTest(String pattern, Node expectedNode, int seed) {
        NodeTreeBuilder builder = new DefaultTreeBuilder(pattern, null);
        Node node = builder.get();
        // Verify that nodes are correct
        assertEquals(expectedNode.toString(), node.toString());

        GenerationVisitor visitor = NotMatchingGenerationVisitor.builder()
                                                                .withRandom(TestingUtilities.newRandom(seed))
                                                                .get();
        node.visit(visitor);
        boolean matches = Pattern.compile(pattern)
                                 .matcher(visitor.getString())
                                 .matches();
        assertFalse(matches, "Should not match " + pattern + " got " + visitor.getString());
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void caseInsensitiveVisitingWorksTest(String pattern, Node expectedNode, int seed) {
        NodeTreeBuilder builder = new DefaultTreeBuilder(pattern, null);
        Node node = builder.get();
        // Verify that nodes are correct
        assertEquals(expectedNode.toString(), node.toString());

        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        GenerationVisitor visitor = NotMatchingGenerationVisitor.builder()
                                                                .withRandom(TestingUtilities.newRandom(seed))
                                                                .withProperties(properties)
                                                                .get();
        node.visit(visitor);
        boolean matches = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
                                 .matcher(visitor.getString())
                                 .matches();
        assertFalse(matches, "Should not match " + pattern + " got " + visitor.getString());
    }
}
