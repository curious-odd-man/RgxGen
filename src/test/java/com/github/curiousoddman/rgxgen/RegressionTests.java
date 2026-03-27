package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.BIG_INTEGER_TWO;
import static org.junit.jupiter.api.Assertions.*;

public class RegressionTests {

    @Test
    public void bug23_parseEscapeCharacterInSquareBracketsTest() {
        String pattern = "[A-Z0-9'\\-/\\.\\s]{0,2}";
        Pattern compile = Pattern.compile(pattern);
        RgxGen rgxGen = RgxGen.parse(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        StringIterator stringIterator = rgxGen.iterateUnique();
        while (stringIterator.hasNext()) {
            assertTrue(compile.matcher(stringIterator.next())
                    .matches());
        }
    }

    @Test
    public void bug31_topLevelChoiceIsNotRecognizedTest() {
        String pattern = "1|2";
        RgxGen rgxGen = RgxGen.parse(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        StringIterator stringIterator = rgxGen.iterateUnique();
        assertEquals(BIG_INTEGER_TWO, rgxGen.getUniqueEstimation()
                .orElse(null));
        assertEquals("1", stringIterator.next());
        assertEquals("2", stringIterator.next());
        assertFalse(stringIterator.hasNext());
    }

    @Test
    public void bug32_capAndDollarInTheMiddleAreNotHandledTest() {
        String pattern = "(^x|y$)";
        final RgxGen rgxGen = RgxGen.parse(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        final StringIterator stringIterator = rgxGen.iterateUnique();
        assertEquals(BigInteger.valueOf(2), rgxGen.getUniqueEstimation()
                .orElse(null));
        assertEquals("x", stringIterator.next());
        assertEquals("y", stringIterator.next());
        assertFalse(stringIterator.hasNext());
    }

    @Test
    public void bug53_incorrectHandlingOfDashInSquareBracketsTest() {
        String pattern = "^[a-zA-Z0-9-._:]*$";
        Pattern compile = Pattern.compile(pattern);
        RgxGen rgxGen = RgxGen.parse(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        for (int i = 0; i < 100; i++) {
            String generated = rgxGen.generate();
            assertTrue(compile.matcher(generated)
                    .matches(), '\'' + generated + "' for pattern '" + pattern + '\'');
        }
    }

    @Test
    public void bug53_incorrectHandlingOfDashInSquareBracketsVariation1Test() {
        String pattern = "[\\s-a]";
        Pattern compile = Pattern.compile(pattern);
        RgxGen rgxGen = RgxGen.parse(pattern);
        assertNotNull(rgxGen); // Not throwing an exception is a success
        for (int i = 0; i < 100; i++) {
            String generated = rgxGen.generate();
            assertTrue(compile.matcher(generated)
                    .matches(), '\'' + generated + "' for pattern '" + pattern + '\'');
        }
    }

    @Test
    public void bug61_iterateUniqueProducesIncorrectPatternTest() {
        RgxGen rgxGen = RgxGen.parse("a?b|c");
        StringIterator noGroupIterator = rgxGen.iterateUnique();
        RgxGen rgxGen1 = RgxGen.parse("(a?b)|c");
        StringIterator withGroupIterator = rgxGen1.iterateUnique();
        while (noGroupIterator.hasNext()) {
            assertTrue(withGroupIterator.hasNext());
            String next = noGroupIterator.next();
            String next1 = withGroupIterator.next();
            System.out.println('\'' + next + "' : '" + next1 + '\'');
            assertEquals(next, next1);
        }

        assertFalse(withGroupIterator.hasNext());
    }

    @Test
    void bug112_infinitePatternRepetitionPropertyDoesNotWorkInGenerateUniqueTest() {
        RgxGenProperties rgxGenProperties = new RgxGenProperties();
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(rgxGenProperties, 1);
        RgxGen rgxGen = RgxGen.parse(rgxGenProperties, "[0-9]+");

        StringIterator stringIterator = rgxGen.iterateUnique();
        List<String> values = new ArrayList<>();

        int count = 0;
        while (stringIterator.hasNext()) {
            values.add(stringIterator.next());
            count++;
            if (count > 10) {
                fail("Expected only 10 unique values, due to INFINITE_PATTERN_REPETITION limit");
            }
        }
        assertEquals(List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"), values);
    }

    @Test
    void bug116_supportForNamedCaptureGroupTest() {
        String pattern = "^(?<parliamentaryTerm>[0-9]{1,2})$";
        assertDoesNotThrow(() -> RgxGen.parse(pattern));
    }

    @Test
    void bug120_dollarInsideTheExpressionTest() {
        String pattern = "(axx$|byy)czz";
        RgxGen rgxGen = RgxGen.parse(pattern);
        Random random = new Random(99);
        for (int i = 0; i < 10; i++) {
            assertEquals("byyczz", rgxGen.generate(random));
        }
    }

    @Test
    void bug120_caretInsideTheExpressionTest() {
        String pattern = "czz(^axx|byy)";
        RgxGen rgxGen = RgxGen.parse(pattern);
        Random random = new Random(101);
        for (int i = 0; i < 10; i++) {
            assertEquals("czzbyy", rgxGen.generate(random));
        }
    }

    @Test
    void bug120_originalExpressionSubmittedTest() {
        String pattern = "^((1$|1,){0,1}(2$|2,){0,1}(3$|3,){0,1}(4$|4,){0,1}(5$|5,){0,1}(6$|6,){0,1}(7$|7,){0,1}(8$|8,){0,1}(9$|9,){0,1}(10$|10,){0,1}(11$|11,){0,1}(12$){0,1})$";
        RgxGen rgxGen = RgxGen.parse(pattern);
        for (int i = 0; i < 10; i++) {
            String generate = rgxGen.generate();
            List<Integer> list = Arrays.stream(generate.split(",")).map(Integer::valueOf).toList();
            List<Integer> orderedList = list.stream().sorted().toList();
            assertEquals(orderedList, list);
            IntSummaryStatistics intSummaryStatistics = list.stream().mapToInt(v -> v).summaryStatistics();
            assertTrue(intSummaryStatistics.getMin() >= 1);
            assertTrue(intSummaryStatistics.getMax() <= 12);
        }
    }
}
