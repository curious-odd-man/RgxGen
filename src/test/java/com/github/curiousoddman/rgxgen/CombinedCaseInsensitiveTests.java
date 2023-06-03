package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.data.TestPatternCaseInsensitive;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CombinedCaseInsensitiveTests extends CombinedTestTemplate<TestPatternCaseInsensitive> {
    public static Stream<TestPatternCaseInsensitive> getAllPatterns() {
        return Arrays.stream(TestPatternCaseInsensitive.values());
    }

    @ParameterizedTest
    @MethodSource("getAllPatterns")
    public void countTest(TestPatternCaseInsensitive testPattern) {
        assumeTrue(testPattern.hasEstimatedCount());
        RgxGen rgxGen = new RgxGen(testPattern.getPattern());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        rgxGen.setProperties(properties);
        assertEquals(testPattern.getEstimatedCount(), rgxGen.getUniqueEstimation().orElse(null));
    }

    @ParameterizedTest
    @MethodSource("getAllPatterns")
    public void generateUniqueTest(TestPatternCaseInsensitive testPattern) {
        assumeTrue(testPattern.hasAllUniqueValues());
        RgxGen rgxGen = new RgxGen(testPattern.getPattern());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        rgxGen.setProperties(properties);
        assertEquals(testPattern.getAllUniqueValues(), TestingUtilities.iteratorToList(rgxGen.iterateUnique()));
    }

    @ParameterizedTest
    @MethodSource("getAllPatterns")
    public void classRgxGenCaseInsensitiveTest(TestPatternCaseInsensitive testPattern) {
        RgxGen rgxGen = new RgxGen(testPattern.getPattern());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        rgxGen.setProperties(properties);
        List<String> strings = rgxGen.stream()
                                     .limit(1000)
                                     .collect(Collectors.toList());
        Pattern caseSensitivePattern = Pattern.compile(testPattern.getPattern());
        boolean atLeastOneCaseSensitiveMismatch = false;
        for (String generated : strings) {
            boolean result = isValidGenerated(testPattern, generated, Pattern.CASE_INSENSITIVE);
            boolean caseSensitiveMatches = !caseSensitivePattern.matcher(generated).matches();
            assertTrue(result, "Text: '" + generated + "' does not match pattern " + testPattern.getPattern());
            atLeastOneCaseSensitiveMismatch = atLeastOneCaseSensitiveMismatch || caseSensitiveMatches;
        }

        assertTrue(atLeastOneCaseSensitiveMismatch);
    }
}
