package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.generator.visitors.NotMatchingGenerationVisitor;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
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

    public static Collection<Object[]> initialData() {
        return Arrays.asList(new Object[][]{
                {"[a-z0-5]", new SymbolSet(Arrays.asList(new SymbolSet.SymbolRange('a', 'z'), new SymbolSet.SymbolRange('0', '5')), SymbolSet.TYPE.POSITIVE)},
                {"abc|def", new Choice(new FinalSymbol("abc"), new FinalSymbol("def"))},
                {"helloworld", new FinalSymbol("helloworld")},
                {"a{2,3}", new Repeat(new FinalSymbol("a"), 2, 3)},
                {"a[a-z]", new Sequence(new FinalSymbol("a"), new SymbolSet(Collections.singletonList(new SymbolSet.SymbolRange('a', 'z')), SymbolSet.TYPE.POSITIVE))}
                // TODO: NotSymbol, Group and GroupRef
        });
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return initialData().stream()
                            .flatMap(arr -> IntStream.range(0, 1000)
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

        NotMatchingGenerationVisitor visitor = new NotMatchingGenerationVisitor(new Random(aSeed));
        aNode.visit(visitor);
        boolean matches = Pattern.compile(aRegex)
                                 .matcher(visitor.getString())
                                 .matches();
        assertFalse("Should not match " + aRegex + " got " + visitor.getString(), matches);
    }
}
