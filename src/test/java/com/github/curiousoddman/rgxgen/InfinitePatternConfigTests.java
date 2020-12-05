package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.After;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class InfinitePatternConfigTests {

    @After
    public void afterEach() {
        RgxGen.setDefaultProperties(null);
    }

    @Test
    public void maxLengthIsRespectedTest() {
        RgxGenProperties properties_3 = new RgxGenProperties();
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(properties_3, 2);
        RgxGen rgxGen_3 = new RgxGen("x*");
        rgxGen_3.setProperties(properties_3);
        for (int i = 0; i < 100000; i++) {
            String value = rgxGen_3.generate(TestingUtilities.newRandom(i));
            assertTrue("Expected to have either empty, or 'x' or 'xx' string. But got " + value, value != null && value.isEmpty()
                    || "x".equals(value) || "xx".equals(value));
        }
    }
}
