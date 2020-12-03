package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.data.DataInterface;
import org.junit.Before;
import org.junit.runners.Parameterized;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assume.assumeTrue;

public abstract class CombinedTestTemplate<T extends DataInterface> {

    @Parameterized.Parameter
    public T aTestPattern;

    protected Pattern aCompiledPattern;

    @Before
    public void setUp() {
        setCompiledPattern(0);
    }

    public void setCompiledPattern(int flags) {
        try {
            aCompiledPattern = Pattern.compile(aTestPattern.getPattern(), flags);
        } catch (PatternSyntaxException e) {
            aCompiledPattern = null;
        }
    }

    protected boolean isValidGenerated(String value) {
        assumeTrue(aTestPattern.isUsableWithJavaPattern());
        if (aTestPattern.useFindForMatching()) {
            return aCompiledPattern.matcher(value)
                                   .find();
        } else {
            return aCompiledPattern.matcher(value)
                                   .matches();
        }
    }
}
