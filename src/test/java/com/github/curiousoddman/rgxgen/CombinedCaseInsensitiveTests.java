package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.data.TestPatternCaseInsensitive;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class CombinedCaseInsensitiveTests extends CombinedTestTemplate<TestPatternCaseInsensitive> {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object> data() {
        return Arrays.asList(TestPatternCaseInsensitive.values());
    }

    @Before
    @Override
    public void setUp() {
        setCompiledPattern(Pattern.CASE_INSENSITIVE);
    }

    @Test
    public void countTest() {
        assumeTrue(aTestPattern.hasEstimatedCount());
        RgxGen rgxGen = new RgxGen(aTestPattern.getPattern());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        rgxGen.setProperties(properties);
        assertEquals(aTestPattern.getEstimatedCount(), rgxGen.numUnique());
    }

    @Test
    public void generateUniqueTest() {
        assumeTrue(aTestPattern.hasAllUniqueValues());
        RgxGen rgxGen = new RgxGen(aTestPattern.getPattern());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        rgxGen.setProperties(properties);
        assertEquals(aTestPattern.getAllUniqueValues(), TestingUtilities.iteratorToList(rgxGen.iterateUnique()));
    }

    @Test
    public void classRgxGenCaseInsensitiveTest() {
        RgxGen rgxGen = new RgxGen(aTestPattern.getPattern());
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        rgxGen.setProperties(properties);
        List<String> strings = rgxGen.stream()
                                     .limit(1000)
                                     .collect(Collectors.toList());
        Pattern caseSensitivePattern = Pattern.compile(aTestPattern.getPattern());
        boolean atLeastOneCaseSensitiveMismatch = false;
        for (String generated : strings) {
            boolean result = isValidGenerated(generated);
            boolean caseSensitiveMatches = !caseSensitivePattern.matcher(generated)
                                                                .matches();
            assertTrue("Text: '" + generated + "' does not match pattern " + aTestPattern.getPattern(), result);
            atLeastOneCaseSensitiveMismatch |= caseSensitiveMatches;
        }

        assertTrue(atLeastOneCaseSensitiveMismatch);
    }
}
