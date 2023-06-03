package com.github.curiousoddman.rgxgen.data;

import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;


// CAUTION! Double braced initialization is used.
@SuppressWarnings("DoubleBraceInitialization")
public enum TestPatternCaseInsensitive implements DataInterface {
    FINAL_SYMBOL_A("a") {{
        setAllUniqueValues("a", "A");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    FINAL_SYMBOL_ABC("abc") {{
        setAllUniqueValues("abc", "Abc", "aBc", "ABc", "abC", "AbC", "aBC", "ABC");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B("[ab]") {{
        setAllUniqueValues("A", "a", "B", "b");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_RANGE("a{2,3}") {{
        setAllUniqueValues("aa", "aA", "Aa", "AA", "aaa", "aaA", "aAa", "aAA", "Aaa", "AaA", "AAa", "AAA");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B_REPEAT_CONST("(a|b){2}") {{
        setAllUniqueValues("aa", "aA", "ab", "aB", "Aa", "AA", "Ab", "AB", "ba", "bA", "bb", "bB", "Ba", "BA", "Bb", "BB");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CHOICE_CAPTURED("(a|b)\\1") {{
        setAllUniqueValues("aa", "AA", "bb", "BB");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CAPTURE_REF_REPEAT("(a|b)\\1{2,3}") {{
        setAllUniqueValues("aaa", "aaaa", "AAA", "AAAA", "bbb", "bbbb", "BBB", "BBBB");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    GROUP_RESULT_USED_IN_CHOICES("(a)(\\1|b)") {{
        setAllUniqueValues("aa", "ab", "aB", "AA", "Ab", "AB");
    }};

    final String aPattern;

    BigInteger   aEstimatedCount;
    List<String> aAllUniqueValues;

    TestPatternCaseInsensitive(String pattern) {
        aPattern = pattern;
        aEstimatedCount = TestingUtilities.BIG_INTEGER_MINUS_ONE;
    }

    public String getPattern() {
        return aPattern;
    }

    public Node getResultNode() {
        return null;
    }

    public BigInteger getEstimatedCount() {
        return aEstimatedCount;
    }

    public List<String> getAllUniqueValues() {
        return aAllUniqueValues;
    }

    protected final void setInfinite() {
        aEstimatedCount = null;
    }

    protected final void setAllUniqueValues(String... values) {
        setAllUniqueValues(Arrays.asList(values));
    }

    protected final void setAllUniqueValues(List<String> values) {
        aAllUniqueValues = values;
        aEstimatedCount = BigInteger.valueOf(values.size());
    }

    public boolean hasEstimatedCount() {
        return !TestingUtilities.BIG_INTEGER_MINUS_ONE.equals(aEstimatedCount);
    }

    public boolean hasAllUniqueValues() {
        return aAllUniqueValues != null;
    }

    @Override
    public String toString() {
        return name() + " : " + aPattern;
    }

    @Override
    public boolean useFindForMatching() {
        return false;
    }

    @Override
    public boolean isUsableWithJavaPattern() {
        return true;
    }
}
