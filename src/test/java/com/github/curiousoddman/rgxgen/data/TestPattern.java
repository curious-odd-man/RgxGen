package com.github.curiousoddman.rgxgen.data;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;
import static com.github.curiousoddman.rgxgen.model.UnicodeCategory.DECIMAL_DIGIT_NUMBER;
import static com.github.curiousoddman.rgxgen.model.UnicodeCategoryConstants.BASIC_LATIN_LOWERCASE_LATIN_ALPHABET;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.*;
import static java.util.Arrays.asList;


// CAUTION! Double braced initialization is used.
@SuppressWarnings("DoubleBraceInitialization")
public enum TestPattern implements DataInterface {
    SIMPLE_A("a"
    ) {{
        setAllUniqueValues("a");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    SIMPLE_A_WITH_START_END("^a$"
    ) {{
        setAllUniqueValues("a");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_DIGIT("\\d"
    ) {{
        setAllUniqueValues("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NOT_A_DIGIT("\\D"      // Any non-digit
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_DIGIT_RANGE("[0-9]"
    ) {{
        setAllUniqueValues("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    }},
    LETTER_RANGE("[a-cA-C]"
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_WORD_CHARACTER("\\w"      // Any word character  [a-zA-Z0-9_]
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_NON_WORD_CHARACTER("\\W"      // Any non-word symbol  [a-zA-Z0-9_]
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    HEX_SPACE("\\x20" // Space
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    HEX_SYMBOL("\\x{26F8}"
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    HEX_SPACE_THEN_A("\\x20a" // Space
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    HEX_SYMBOL_THEN_A("\\x{26F8}a"
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B("[ab]"
    ) {{
        setAllUniqueValues("a", "b");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B_THEN_C("[ab]c"
    ) {{
        setAllUniqueValues("ac", "bc");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    D_THEN_A_OR_B_THEN_C("d[ab]c"
    ) {{
        setAllUniqueValues("dac", "dbc");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_RANGE("a{2,5}"
    ) {{
        setAllUniqueValues("aa", "aaa", "aaaa", "aaaaa");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_CONST("a{2}"
    ) {{
        setAllUniqueValues("aa");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_60K("a{60000}"
    ) {{
        setAllUniqueValues(Stream.generate(() -> "a")
                .limit(60000)
                .reduce("", String::concat));
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B_REPEAT_CONST(
            "(a|b){2}"
    ) {{
        setAllUniqueValues("aa", "ab", "ba", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B_REPEAT_RANGE("(a|b){0,2}"
    ) {{
        setAllUniqueValues("", "a", "b", "aa", "ab", "ba", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_OR_B_REPEAT("(a{0,2}|b{0,2})"
    ) {{
        setAllUniqueValues("", "a", "aa", "", "b", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NOTHING_OR_A_REPEAT_OR_B_REPEAT("(|(a{1,2}|b{1,2}))"
    ) {{
        setAllUniqueValues("", "a", "aa", "b", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_THEN_ANY("a."
    ) {{
        setAllUniqueValues(stream(makeAsciiCharacterArray())
                .map(s -> String.valueOf('a') + s)
                .collect(Collectors.toList()));
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_THEN_ANY(".."
    ) {{
        setAllUniqueValues(stream(makeAsciiCharacterArray())
                .flatMap(s -> stream(makeAsciiCharacterArray())
                        .map(v -> String.valueOf(s) + v))
                .collect(Collectors.toList()));
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_ZERO_OR_MORE("a*"
    ) {{
        setInfinite();
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_MIN_4("a{4,}"
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NOT_A("[^a]"
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NOT_LETTER_RANGE("[^a-dE-F]"
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------

    ANY_WHITESPACE("\\s"      // Any White Space
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NOT_A_WHITESPACE("\\S"      // Any Non White Space
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_THEN_A_OR_NOT("aa?"
    ) {{
        setAllUniqueValues("a", "aa");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_THEN_A_ONE_OR_MORE("aa+"
    ) {{
        setInfinite();
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_THEN_ANY_REPEAT_INFINITE("a.*"      // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
    ) {{
        setInfinite();
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    POSITIVE_LOOKAHEAD("foo(?=bar)"
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NEGATIVE_LOOKAHEAD("foo(?!bar)"
    ) {{
        setInfinite();
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    POSITIVE_LOOKBEHIND("(?<=foo)bar"
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NEGATIVE_LOOKBEHIND("(?<!not)foo"
    ) {{
        setInfinite();
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CHOICE_CAPTURED("(a|b)\\1"
    ) {{
        setAllUniqueValues("aa", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CAPTURE_REPEAT("(a|b){2,3}\\1"
    ) {{
        setAllUniqueValues("aaa", "abb", "baa", "bbb", "aaaa", "aabb", "abaa", "abbb", "baaa", "babb", "bbaa", "bbbb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CAPTURE_REPEAT_0("(a|b){3}\\1"
    ) {{
        setAllUniqueValues("aaaa", "aabb", "abaa", "abbb", "baaa", "babb", "bbaa", "bbbb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CAPTURE_REPEAT_1("(a|b){2}\\1"
    ) {{
        setAllUniqueValues("aaa", "abb", "baa", "bbb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CAPTURE_REF_REPEAT("(a|b)\\1{2,3}"
    ) {{
        setAllUniqueValues("aaa", "aaaa", "bbb", "bbbb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CAPTURE_REPEAT_AND_REF_REPEAT(
            "(a|b){2,3}\\1{2,3}"
    ) {{
        setAllUniqueValues("aaaa", "aaaaa", "abbb", "abbbb", "baaa", "baaaa", "bbbb", "bbbbb", "aaaaa", "aaaaaa", "aabbb", "aabbbb", "abaaa", "abaaaa", "abbbb", "abbbbb", "baaaa", "baaaaa", "babbb", "babbbb", "bbaaa", "bbaaaa", "bbbbb", "bbbbbb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    XML_NODE("<([abc])>d<\\/\\1>"
    ) {{
        setAllUniqueValues("<a>d</a>", "<b>d</b>", "<c>d</c>");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    METASEQUENCE_IN_SQUARE_BRACKETS("['\\-/\\.\\s]"
    ) {{
        setAllUniqueValues("\t", " ", "'", "-", ".", "/");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    TOP_LEVEL_CHOICE_WITHOUT_PARENTHESIS("a|b"
    ) {{
        setAllUniqueValues("a", "b");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    EMPTY_CHOICE_AT_THE_START_OF_CHOICES("(|A)"
    ) {{
        setAllUniqueValues("", "A");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    EMPTY_CHOICE_IN_THE_MIDDLE_OF_CHOICES("(B||A)"
    ) {{
        setAllUniqueValues("B", "", "A");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    EMPTY_CHOICE_AT_THE_END_OF_CHOICES("(A|)"
    ) {{
        setAllUniqueValues("A", "");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    GROUP_RESULT_USED_IN_CHOICES("(a)(\\1|b)"
    ) {{
        setAllUniqueValues("aa", "ab");
    }},
    SLASH_Q_AND_SLASH_E_BASIC("\\Qm\\E"
    ) {{
        setAllUniqueValues("m");
    }},
    SLASH_Q_WITHOUT_SLASH_E_BASIC("\\Qmas"
    ) {{
        setAllUniqueValues("mas");
    }},
    SLASH_E_WITHOUT_SLASH_Q_BASIC("mas\\E"
    ) {{
        setAllUniqueValues("mas");
        setCannotCompilePattern();
    }},
    SLASH_Q_AND_SLASH_E_IGNORE_SPECIALS("\\Q[a]\\1(a|c).*\\W\\E"
    ) {{
        setAllUniqueValues("[a]\\1(a|c).*\\W");
    }},
    SLASH_Q_AND_SLASH_E_WITH_PREFIX_SUFFIX("123\\Qm\\Ezxc"
    ) {{
        setAllUniqueValues("123mzxc");
    }},
    SLASH_Q_AND_SLASH_E_WITH_REPEAT("123\\Qmass[]\\E{1,2}zxc"
    ) {{
        setAllUniqueValues("123mass[]zxc", "123mass[]]zxc");
    }},
    UNICODE("\\u0041") {{
        setAllUniqueValues("A");
    }},
    IN_CYRILLIC_CATEGORY("\\p{InCyrillic}{2}"
    ) {{
        setAllUniqueValues(
                getSymbolStream(UnicodeCategory.IN_CYRILLIC)
                        .flatMap(c -> getSymbolStream(UnicodeCategory.IN_CYRILLIC).map(cc -> c + cc))
                        .collect(Collectors.toList()));
    }},
    CATEGORY_WITHIN_SQUART_BRACKETS("[a-z\\p{Nd}]{2}") {{
        setAllUniqueValues(
                Stream.concat(
                                getRangeSymbolStream(BASIC_LATIN_LOWERCASE_LATIN_ALPHABET),
                                getSymbolStream(DECIMAL_DIGIT_NUMBER)
                        )
                        .sorted(Comparator.naturalOrder())
                        .flatMap(c -> Stream
                                .concat(getRangeSymbolStream(BASIC_LATIN_LOWERCASE_LATIN_ALPHABET), getSymbolStream(DECIMAL_DIGIT_NUMBER))
                                .sorted(Comparator.naturalOrder())
                                .map(cc -> c + cc))
                        .collect(Collectors.toList()));
    }};

    final String aPattern;
    BigInteger aEstimatedCount;
    List<String> aAllUniqueValues;
    boolean aIsUsableWithJavaPattern;
    TestPattern(String pattern) {
        aPattern = pattern;
        aEstimatedCount = TestingUtilities.BIG_INTEGER_MINUS_ONE;
        aIsUsableWithJavaPattern = true;
    }

    private static Stream<String> getSymbolStream(UnicodeCategory category) {
        return Stream.concat(
                stream(category.getSymbols()).map(String::valueOf),
                category
                        .getSymbolRanges()
                        .stream()
                        .flatMap(TestPattern::getRangeSymbolStream)
        );
    }

    private static Stream<String> getRangeSymbolStream(SymbolRange range) {
        return IntStream.range(range.from(), range.to() + 1).mapToObj(i -> String.valueOf((char) i));
    }

    public static Stream<Character> stream(char[] chars) {
        return new String(chars).chars().mapToObj(i -> (char) i);
    }

    public String getPattern() {
        return aPattern;
    }

    public BigInteger getEstimatedCount() {
        return aEstimatedCount;
    }

    public List<String> getAllUniqueValues() {
        return aAllUniqueValues;
    }

    protected final void setAllUniqueValues(String... values) {
        setAllUniqueValues(asList(values));
    }

    protected final void setAllUniqueValues(List<String> values) {
        aAllUniqueValues = values;
        aEstimatedCount = BigInteger.valueOf(values.size());
    }

    protected final void setInfinite() {
        aEstimatedCount = null;
    }

    protected final void setCannotCompilePattern() {
        aIsUsableWithJavaPattern = false;
    }

    public boolean hasEstimatedCount() {
        return !TestingUtilities.BIG_INTEGER_MINUS_ONE.equals(aEstimatedCount);
    }

    public boolean hasAllUniqueValues() {
        return aAllUniqueValues != null;
    }

    public boolean useFindForMatching() {
        return this == POSITIVE_LOOKAHEAD
                || this == NEGATIVE_LOOKAHEAD
                || this == POSITIVE_LOOKBEHIND
                || this == NEGATIVE_LOOKBEHIND;
    }

    public boolean isUsableWithJavaPattern() {
        return aIsUsableWithJavaPattern;
    }

    @Override
    public String toString() {
        return name() + " : " + aPattern;
    }
}
