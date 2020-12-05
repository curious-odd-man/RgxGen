package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import com.github.curiousoddman.rgxgen.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.visitors.NotMatchingGenerationVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class NotMatchingGenerationTests {

    private static Collection<Object[]> initialData() {
        return Arrays.asList(new Object[][]{
                {"[a-z0-5]", new SymbolSet("[a-z0-5]", Arrays.asList(new SymbolSet.SymbolRange('a', 'z'), new SymbolSet.SymbolRange('0', '5')), SymbolSet.TYPE.POSITIVE)},
                {"abc|def", new Choice("abc|def", new FinalSymbol("abc"), new FinalSymbol("def"))},
                {"helloworld", new FinalSymbol("helloworld")},
                {"a{2,3}", new Repeat("a{2,3}", new FinalSymbol("a"), 2, 3)},
                {"a[a-z]", new Sequence("a[a-z]", new FinalSymbol("a"),
                                        new SymbolSet("[a-z]", Collections.singletonList(new SymbolSet.SymbolRange('a', 'z')), SymbolSet.TYPE.POSITIVE))},
                {"([a-z])\\1", new Sequence("([a-z])\\1", new Group("([a-z])", 1,
                                                                    new SymbolSet("[a-z]", Collections.singletonList(new SymbolSet.SymbolRange('a', 'z')), SymbolSet.TYPE.POSITIVE)),
                                            new GroupRef("\\1", 1)
                )},
                {"foo(?!bar)", new Sequence("foo(?!bar)",
                                            new FinalSymbol("foo"),
                                            new NotSymbol("bar", new FinalSymbol("bar"))
                )}
        });
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return initialData().stream()
                            .flatMap(arr -> IntStream.range(0, 100)
                                                     .mapToObj(i -> new Object[]{arr[0], arr[1], i}))
                            .collect(Collectors.toList());
    }

    @Parameterized.Parameter
    public String aRegex;

    @Parameterized.Parameter(1)
    public Node aNode;

    @Parameterized.Parameter(2)
    public int aSeed;

    @Test
    public void nodeVisitingWorksTest() {
        NodeTreeBuilder builder = new DefaultTreeBuilder(aRegex);
        Node node = builder.get();
        // Verify that nodes are correct
        assertEquals(aNode.toString(), node.toString());

        GenerationVisitor visitor = NotMatchingGenerationVisitor.builder()
                                                                .withRandom(TestingUtilities.newRandom(aSeed))
                                                                .get();
        aNode.visit(visitor);
        boolean matches = Pattern.compile(aRegex)
                                 .matcher(visitor.getString())
                                 .matches();
        assertFalse("Should not match " + aRegex + " got " + visitor.getString(), matches);
    }

    @Test
    public void caseInsensitiveVisitingWorksTest() {
        NodeTreeBuilder builder = new DefaultTreeBuilder(aRegex);
        Node node = builder.get();
        // Verify that nodes are correct
        assertEquals(aNode.toString(), node.toString());

        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        GenerationVisitor visitor = NotMatchingGenerationVisitor.builder()
                                                                .withRandom(TestingUtilities.newRandom(aSeed))
                                                                .withProperties(properties)
                                                                .get();
        aNode.visit(visitor);
        boolean matches = Pattern.compile(aRegex, Pattern.CASE_INSENSITIVE)
                                 .matcher(visitor.getString())
                                 .matches();
        assertFalse("Should not match " + aRegex + " got " + visitor.getString(), matches);
    }
}
