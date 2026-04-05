package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.data.TestPatternCaseInsensitive;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CombinedCaseInsensitiveTests extends CombinedTestTemplate<TestPatternCaseInsensitive> {
    @ParameterizedTest
    @EnumSource(TestPatternCaseInsensitive.class)
    public void countTest(TestPatternCaseInsensitive testPattern) {
        assumeTrue(testPattern.hasEstimatedCount());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        RgxGen rgxGen = RgxGen.parse(properties, testPattern.getPattern());
        assertEquals(testPattern.getEstimatedCount(), rgxGen.getUniqueEstimation().orElse(null));
    }

    @ParameterizedTest
    @EnumSource(TestPatternCaseInsensitive.class)
    public void generateUniqueTest(TestPatternCaseInsensitive testPattern) {
        assumeTrue(testPattern.hasAllUniqueValues());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        RgxGen rgxGen = RgxGen.parse(properties, testPattern.getPattern());
        assertEquals(testPattern.getAllUniqueValues(), TestingUtilities.iteratorToList(rgxGen.iterateUnique()));
    }

    @ParameterizedTest
    @EnumSource(TestPatternCaseInsensitive.class)
    public void classRgxGenCaseInsensitiveTest(TestPatternCaseInsensitive testPattern) {
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        RgxGen rgxGen = RgxGen.parse(properties, testPattern.getPattern());
        List<String> strings = rgxGen.stream()
                .limit(1000)
                .toList();
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
