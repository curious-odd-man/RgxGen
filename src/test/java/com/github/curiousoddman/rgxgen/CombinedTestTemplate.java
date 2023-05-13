package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.data.DataInterface;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assumptions.assumeTrue;


public abstract class CombinedTestTemplate<T extends DataInterface> {

    protected boolean isValidGenerated(T testPattern, String value) {
        return isValidGenerated(testPattern, value, 0);
    }

    protected boolean isValidGenerated(T testPattern, String value, int patternCompilationFlags) {
        assumeTrue(testPattern.isUsableWithJavaPattern());
        Pattern aCompiledPattern = Pattern.compile(testPattern.getPattern(), patternCompilationFlags);
        if (testPattern.useFindForMatching()) {
            return aCompiledPattern.matcher(value)
                                   .find();
        } else {
            return aCompiledPattern.matcher(value)
                                   .matches();
        }
    }
}
