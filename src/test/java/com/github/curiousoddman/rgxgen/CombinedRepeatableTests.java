package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.data.TestPattern;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import com.github.curiousoddman.rgxgen.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.visitors.NotMatchingGenerationVisitor;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CombinedRepeatableTests extends CombinedTestTemplate<TestPattern> {
    public static Stream<Arguments> getPatterns() {
        return Arrays.stream(TestPattern.values())
                     .flatMap(testPattern -> IntStream.range(0, 100)
                                                      .mapToObj(index -> Arguments.of(index, testPattern)));
    }

    @ParameterizedTest(name = "{1}: {0}")
    @MethodSource("getPatterns")
    public void generateTest(int aSeed, TestPattern testPattern) {
        GenerationVisitor generationVisitor = GenerationVisitor.builder()
                                                               .withRandom(TestingUtilities.newRandom(aSeed))
                                                               .get();
        testPattern.getResultNode()
                   .visit(generationVisitor);
        boolean result = isValidGenerated(testPattern, generationVisitor.getString(), 0);
        assertTrue(result, "Text: '" + generationVisitor.getString() + "'does not match pattern " + testPattern.getPattern());
    }

    @ParameterizedTest(name = "{1}: {0}")
    @MethodSource("getPatterns")
    public void repeatableGenerationTest(int aSeed, TestPattern testPattern) {
        Random rnd1 = TestingUtilities.newRandom(aSeed);
        Random rnd2 = TestingUtilities.newRandom(aSeed);

        RgxGen rgxGen_1 = RgxGen.parse(testPattern.getPattern());
        RgxGen rgxGen_2 = RgxGen.parse(testPattern.getPattern());
        assertEquals(rgxGen_1.generate(rnd1), rgxGen_2.generate(rnd2));
    }

    @ParameterizedTest(name = "{1}: {0}")
    @MethodSource("getPatterns")
    @Timeout(5000)
    public void generateNotMatchingTest(int aSeed, TestPattern testPattern) {
        GenerationVisitor generationVisitor = NotMatchingGenerationVisitor.builder()
                                                                          .withRandom(TestingUtilities.newRandom(aSeed))
                                                                          .get();
        testPattern.getResultNode()
                   .visit(generationVisitor);
        boolean result = isValidGenerated(testPattern, generationVisitor.getString(), 0);
        assertFalse(result, "Text: '" + generationVisitor.getString() + "' matches pattern " + testPattern.getPattern());
    }

    @ParameterizedTest(name = "{1}: {0}")
    @MethodSource("getPatterns")
    @Timeout(5000)
    public void repeatableNotMatchingGenerationTest(int aSeed, TestPattern testPattern) {
        Random rnd1 = TestingUtilities.newRandom(aSeed);
        Random rnd2 = TestingUtilities.newRandom(aSeed);

        RgxGen rgxGen_1 = RgxGen.parse(testPattern.getPattern());
        RgxGen rgxGen_2 = RgxGen.parse(testPattern.getPattern());
        assertEquals(rgxGen_1.generateNotMatching(rnd1), rgxGen_2.generateNotMatching(rnd2));
    }
}
