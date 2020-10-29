package com.github.curiousoddman.rgxgen;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assume.assumeNoException;

@RunWith(Parameterized.class)
public abstract class CombinedTestTemplate {

    @Parameterized.Parameter
    public TestPattern aTestPattern;

    protected Pattern   aCompiledPattern;
    protected Exception aPatternCompileException;

    @Before
    public void setUp() {
        try {
            aCompiledPattern = Pattern.compile(aTestPattern.aPattern);
        } catch (PatternSyntaxException e) {
            aPatternCompileException = e;
        }
    }

    protected boolean isValidGenerated(String value) {
        assumeNoException(aPatternCompileException);
        if (aTestPattern.useFindForMatching()) {
            return aCompiledPattern.matcher(value)
                                   .find();
        } else {
            return aCompiledPattern.matcher(value)
                                   .matches();
        }
    }
}
