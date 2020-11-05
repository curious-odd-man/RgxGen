package com.github.curiousoddman.rgxgen.data;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


// CAUTION! Double braced initialization is used.
@SuppressWarnings("DoubleBraceInitialization")
public enum TestPatternCaseInsensitive {
    FINAL_SYMBOL_A("a",
                   new FinalSymbol("a")) {{
        setAllUniqueValues("a", "A");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    FINAL_SYMBOL_ABC("abc",
                     new FinalSymbol("a")) {{
        setAllUniqueValues("abc", "Abc", "aBc", "ABc", "abC", "AbC", "aBC", "ABC");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B("[ab]",
           new SymbolSet("[ab]",
                         new String[]{
                                 "a", "b"
                         }, SymbolSet.TYPE.POSITIVE)) {{
        setAllUniqueValues("a", "A", "b", "B");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_RANGE("a{2,3}",
                   new Repeat("a{2,5}", new FinalSymbol("a"), 2, 5)) {{
        setAllUniqueValues("aa", "Aa", "aA", "AA", "aaa", "Aaa", "aAa", "AAa", "aaA", "AaA", "aAA", "AAA");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B_REPEAT_CONST(
            "(a|b){2}",
            new Repeat("(a|b){2}", new Group("(a|b)", 1,
                                             new Choice("(a|b)", new FinalSymbol("a"), new FinalSymbol("b"))), 2)) {{
        setAllUniqueValues("aa", "ab", "ba", "bb", "Aa", "Ab", "Ba", "Bb", "aA", "aB", "bA", "bB", "AA", "AB", "BA", "BB");
    }},

    // TODO: Review those
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CHOICE_CAPTURED("(a|b)\\1",
                    new Sequence("(a|b)\\1",
                                 new Group("(a|b)", 1,
                                           new Choice("(a|b)",
                                                      new FinalSymbol("a"),
                                                      new FinalSymbol("b"))),
                                 new GroupRef("\\1", 1))) {{
        setAllUniqueValues("aa", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CAPTURE_REPEAT("(a|b){2,3}\\1",
                   new Sequence("(a|b){2,3}\\1",
                                new Repeat("(a|b){2,3}",
                                           new Group("(a|b)", 1,
                                                     new Choice(
                                                             "(a|b)",
                                                             new FinalSymbol("a"),
                                                             new FinalSymbol("b"))), 2, 3),
                                new GroupRef("\\1", 1))) {{
        setAllUniqueValues("aaa", "abb", "baa", "bbb", "aaaa", "aabb", "abaa", "abbb", "baaa", "babb", "bbaa", "bbbb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CAPTURE_REF_REPEAT("(a|b)\\1{2,3}",
                       new Sequence("(a|b)\\1{2,3}",
                                    new Group("(a|b)", 1,
                                              new Choice("(a|b)",
                                                         new FinalSymbol("a"),
                                                         new FinalSymbol("b"))),
                                    new Repeat("\\1{2,3}",
                                               new GroupRef("\\1", 1), 2, 3))) {{
        setAllUniqueValues("aaa", "aaaa", "bbb", "bbbb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    GROUP_RESULT_USED_IN_CHOICES("(a)(\\1|b)",
                                 new Sequence("(a)(\\1|b)",
                                              new Group("(a)", 1, new FinalSymbol("a")),
                                              new Group("(\\1|b)", 2,
                                                        new Choice("(\\1|b)",
                                                                   new GroupRef("\\1", 1),
                                                                   new FinalSymbol("b"))
                                              ))) {{
        setAllUniqueValues("aa", "ab");
    }};

    final String aPattern;
    final Node   aResultNode;

    BigInteger   aEstimatedCount;
    List<String> aAllUniqueValues;

    TestPatternCaseInsensitive(String pattern, Node resultNode) {
        aPattern = pattern;
        aResultNode = resultNode;       /// TODO: I think that I can remove the result node. Parsing is tested elswhere. This is only for case insensitive tests.
        aEstimatedCount = TestingUtilities.BIG_INTEGER_MINUS_ONE;
    }

    public String getPattern() {
        return aPattern;
    }

    public Node getResultNode() {
        return aResultNode;
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
        return aPattern;
    }
}
