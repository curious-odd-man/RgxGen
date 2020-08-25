package com.github.curiousoddman.rgxgen;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.regex.Pattern;

@RunWith(Parameterized.class)
public abstract class CombinedTestTemplate {

    @Parameterized.Parameter
    public TestPattern aTestPattern;

    protected Pattern aPattern;

    @Before
    public void setUp() {
        aPattern = Pattern.compile(aTestPattern.aPattern);
    }

    protected boolean isValidGenerated(String value) {
        if (aTestPattern.useFindForMatching()) {
            return aPattern.matcher(value)
                           .find();
        } else {
            return aPattern.matcher(value)
                           .matches();
        }
    }
}
