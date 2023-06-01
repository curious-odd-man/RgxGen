package com.github.curiousoddman.rgxgen.data;

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.SymbolRange;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.*;
import static com.github.curiousoddman.rgxgen.testutil.TestingUtilities.getAllDigits;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;


// CAUTION! Double braced initialization is used.
@SuppressWarnings("DoubleBraceInitialization")
public enum TestPattern implements DataInterface {
    SIMPLE_A("a",
             new FinalSymbol("a")) {{
        setAllUniqueValues("a");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    SIMPLE_A_WITH_START_END("^a$",
                            new FinalSymbol("a")) {{
        setAllUniqueValues("a");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_DIGIT("\\d",
              SymbolSet.ofAsciiCharacters("\\d",
                                          getAllDigits(), MatchType.POSITIVE)) {{
        setAllUniqueValues("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NOT_A_DIGIT("\\D",      // Any non-digit
                SymbolSet.ofAsciiCharacters("\\D",
                                            getAllDigits(), MatchType.NEGATIVE)
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_DIGIT_RANGE("[0-9]",
                    SymbolSet.ofAsciiCharacters("[0-9]",
                                                getAllDigits(), MatchType.POSITIVE)) {{
        setAllUniqueValues("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    }},
    LETTER_RANGE("[a-cA-C]",
                 SymbolSet.ofAsciiRanges("[a-cA-C]",
                                         asList(SymbolRange.range('a', 'c'), SymbolRange.range('A', 'C')), MatchType.POSITIVE)
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_WORD_CHARACTER("\\w",      // Any word character  [a-zA-Z0-9_]
                       SymbolSet.ofAscii("\\w",
                                         asList(SMALL_LATIN_LETTERS, CAPITAL_LATIN_LETTERS, DIGITS), new Character[]{'_'}, MatchType.POSITIVE)
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_NON_WORD_CHARACTER("\\W",      // Any non-word symbol  [a-zA-Z0-9_]
                           SymbolSet.ofAscii("\\W",
                                             asList(SMALL_LATIN_LETTERS, CAPITAL_LATIN_LETTERS, DIGITS), new Character[]{'_'}, MatchType.NEGATIVE)
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    HEX_SPACE("\\x20", // Space
              new FinalSymbol(" ")
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    HEX_SYMBOL("\\x{26F8}",
               new FinalSymbol("⛸")),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    HEX_SPACE_THEN_A("\\x20a", // Space
                     new FinalSymbol(" a")),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    HEX_SYMBOL_THEN_A("\\x{26F8}a",
                      new FinalSymbol("⛸a")),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B("[ab]",
           SymbolSet.ofAsciiCharacters("[ab]",
                                       new Character[]{
                                               'a', 'b'
                                       }, MatchType.POSITIVE)) {{
        setAllUniqueValues("a", "b");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B_THEN_C("[ab]c",
                  new Sequence("[ab]c",
                               SymbolSet.ofAsciiCharacters("[ab]", new Character[]{
                                       'a', 'b'
                               }, MatchType.POSITIVE), new FinalSymbol("c"))) {{
        setAllUniqueValues("ac", "bc");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    D_THEN_A_OR_B_THEN_C("d[ab]c",
                         new Sequence("d[ab]c",
                                      new FinalSymbol("d"),
                                      SymbolSet.ofAsciiCharacters("[ab]", new Character[]{
                                              'a', 'b'
                                      }, MatchType.POSITIVE), new FinalSymbol("c"))) {{
        setAllUniqueValues("dac", "dbc");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_RANGE("a{2,5}",
                   new Repeat("a{2,5}", new FinalSymbol("a"), 2, 5)) {{
        setAllUniqueValues("aa", "aaa", "aaaa", "aaaaa");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_CONST("a{2}",
                   new Repeat("a{2}", new FinalSymbol("a"), 2)) {{
        setAllUniqueValues("aa");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_60K("a{60000}",
                 new Repeat("a{60000}", new FinalSymbol("a"), 60000)) {{
        setAllUniqueValues(Stream.generate(() -> "a")
                                 .limit(60000)
                                 .reduce("", String::concat));
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B_REPEAT_CONST(
            "(a|b){2}",
            new Repeat("(a|b){2}", new Group("(a|b)", 1,
                                             new Choice("(a|b)", new FinalSymbol("a"), new FinalSymbol("b"))), 2)) {{
        setAllUniqueValues("aa", "ab", "ba", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_OR_B_REPEAT_RANGE("(a|b){0,2}",
                        new Repeat("(a|b){0,2}",
                                   new Group("(a|b)", 1,
                                             new Choice("(a|b)", new FinalSymbol("a"), new FinalSymbol("b"))), 0, 2)) {{
        setAllUniqueValues("", "a", "b", "aa", "ab", "ba", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_OR_B_REPEAT("(a{0,2}|b{0,2})",
                         new Group("(a{0,2}|b{0,2})", 1,
                                   new Choice("(a{0,2}|b{0,2})",
                                              new Repeat("a{0,2}", new FinalSymbol("a"), 0, 2),
                                              new Repeat("b{0,2}", new FinalSymbol("b"), 0, 2)))) {{
        setAllUniqueValues("", "a", "aa", "", "b", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NOTHING_OR_A_REPEAT_OR_B_REPEAT("(|(a{1,2}|b{1,2}))",
                                    new Group("(|(a{1,2}|b{1,2}))", 1,
                                              new Choice("(|(a{1,2}|b{1,2}))",
                                                         new FinalSymbol(""),
                                                         new Group("(a{1,2}|b{1,2})", 2,
                                                                   new Choice("(a{1,2}|b{1,2})",
                                                                              new Repeat("a{1,2}", new FinalSymbol("a"), 1, 2),
                                                                              new Repeat("b{1,2}", new FinalSymbol("b"), 1, 2)))))) {{
        setAllUniqueValues("", "a", "aa", "b", "bb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    // FIXME
//    A_THEN_ANY("a.",
//               new Sequence("a.", new FinalSymbol("a"), SymbolSet.ofAsciiDotPattern())) {{
//        setAllUniqueValues(stream(makeAsciiCharacterArray())
//                                   .map(s -> "" + 'a' + s)
//                                   .collect(Collectors.toList()));
//    }},
//    //-----------------------------------------------------------------------------------------------------------------------------------------
//    ANY_THEN_ANY("..",
//                 new Sequence("..", SymbolSet.ofAsciiDotPattern(), SymbolSet.ofAsciiDotPattern())) {{
//        setAllUniqueValues(stream(makeAsciiCharacterArray())
//                                   .flatMap(s -> stream(makeAsciiCharacterArray())
//                                           .map(v -> "" + s + v))
//                                   .collect(Collectors.toList()));
//    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_ZERO_OR_MORE("a*",
                          Repeat.minimum("a*", new FinalSymbol("a"), 0)) {{
        setInfinite();
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_REPEAT_MIN_4("a{4,}",
                   Repeat.minimum("a{4,}", new FinalSymbol("a"), 4)
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    // FIXME
//    NOT_A("[^a]",
//          SymbolSet.ofAsciiCharacters("[^a]", stream(makeAsciiCharacterArray())
//                  .filter(c -> !c.equals('a'))
//                  .toArray(Character[]::new), MatchType.POSITIVE)
//    ),
//    //-----------------------------------------------------------------------------------------------------------------------------------------
//    NOT_LETTER_RANGE("[^a-dE-F]",
//                     SymbolSet.ofAsciiCharacters("[^a-dE-F]",
//                                                 stream(makeAsciiCharacterArray())
//                                                         .filter(c -> !(c.equals('a') || c.equals('b') || c.equals('c') || c.equals('d') || c.equals('E') || c.equals('F')))
//                                                         .toArray(Character[]::new), MatchType.POSITIVE)
//    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    ANY_WHITESPACE("\\s",      // Any White Space
                   SymbolSet.ofAsciiCharacters("\\s", new Character[]{
                           '\r', '\f', '\u000B', ' ', '\t', '\n'
                   }, MatchType.POSITIVE)
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NOT_A_WHITESPACE("\\S",      // Any Non White Space
                     SymbolSet.ofAsciiCharacters("\\S", new Character[]{
                             '\r', '\f', '\u000B', ' ', '\t', '\n'
                     }, MatchType.NEGATIVE)
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_THEN_A_OR_NOT("aa?",
                    new Sequence("aa?",
                                 new FinalSymbol("a"),
                                 new Repeat("a?", new FinalSymbol("a"), 0, 1))) {{
        setAllUniqueValues("a", "aa");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_THEN_A_ONE_OR_MORE("aa+",
                         new Sequence("aa+",
                                      new FinalSymbol("a"),
                                      Repeat.minimum("a+", new FinalSymbol("a"), 1))) {{
        setInfinite();
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    A_THEN_ANY_REPEAT_INFINITE("a.*",      // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                               new Sequence("a.*",
                                            new FinalSymbol("a"),
                                            Repeat.minimum(".*", SymbolSet.ofAsciiDotPattern(), 0))) {{
        setInfinite();
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    POSITIVE_LOOKAHEAD("foo(?=bar)",
                       new Sequence("foo(?=bar)",
                                    new FinalSymbol("foo"), new FinalSymbol("bar"))
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NEGATIVE_LOOKAHEAD("foo(?!bar)",
                       new Sequence("foo(?!bar)",
                                    new FinalSymbol("foo"), new NotSymbol("bar", new FinalSymbol("bar")))) {{
        setInfinite();
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    POSITIVE_LOOKBEHIND("(?<=foo)bar",
                        new Sequence("(?<=foo)bar",
                                     new FinalSymbol("foo"), new FinalSymbol("bar"))
    ),
    //-----------------------------------------------------------------------------------------------------------------------------------------
    NEGATIVE_LOOKBEHIND("(?<!not)foo",
                        new Sequence("(?<!not)foo",
                                     new NotSymbol("not", new FinalSymbol("not")), new FinalSymbol("foo"))) {{
        setInfinite();
    }},
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
    CAPTURE_REPEAT_0("(a|b){3}\\1",
                     new Sequence("(a|b){3}\\1",
                                  new Repeat("(a|b){3}",
                                             new Group("(a|b){3}", 1,
                                                       new Choice("(a|b)",
                                                                  new FinalSymbol("a"),
                                                                  new FinalSymbol("b"))), 3, 3),
                                  new GroupRef("\\1", 1))) {{
        setAllUniqueValues("aaaa", "aabb", "abaa", "abbb", "baaa", "babb", "bbaa", "bbbb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    CAPTURE_REPEAT_1("(a|b){2}\\1",
                     new Sequence("(a|b){2}\\1",
                                  new Repeat("(a|b){2}",
                                             new Group("(a|b)", 1,
                                                       new Choice("(a|b)",
                                                                  new FinalSymbol("a"),
                                                                  new FinalSymbol("b"))), 2, 2),
                                  new GroupRef("\\1", 1))) {{
        setAllUniqueValues("aaa", "abb", "baa", "bbb");
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
    CAPTURE_REPEAT_AND_REF_REPEAT(
            "(a|b){2,3}\\1{2,3}",
            new Sequence("(a|b){2,3}\\1{2,3}",
                         new Repeat("(a|b){2,3}",
                                    new Group("(a|b)", 1,
                                              new Choice("(a|b)",
                                                         new FinalSymbol("a"),
                                                         new FinalSymbol("b"))), 2, 3),
                         new Repeat("\\1{2,3}",
                                    new GroupRef("\\1", 1), 2, 3))) {{
        setAllUniqueValues("aaaa", "aaaaa", "abbb", "abbbb", "baaa", "baaaa", "bbbb", "bbbbb", "aaaaa", "aaaaaa", "aabbb", "aabbbb", "abaaa", "abaaaa", "abbbb", "abbbbb", "baaaa", "baaaaa", "babbb", "babbbb", "bbaaa", "bbaaaa", "bbbbb", "bbbbbb");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    XML_NODE("<([abc])>d<\\/\\1>",
             new Sequence("<([abc])>d<\\/\\1>", new FinalSymbol("<"),
                          new Group("([abc])", 1, SymbolSet.ofAsciiCharacters("[abc]", new Character[]{
                                  'a', 'b', 'c'
                          }, MatchType.POSITIVE)),
                          new FinalSymbol(">d</"),
                          new GroupRef("\\1", 1),
                          new FinalSymbol(">")
             )) {{
        setAllUniqueValues("<a>d</a>", "<b>d</b>", "<c>d</c>");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    METASEQUENCE_IN_SQUARE_BRACKETS("['\\-/\\.\\s]",
                                    SymbolSet.ofAsciiCharacters("['\\-/\\.\\s]", new Character[]{'\'', '-', '/', '.', '\r', '\f', '\u000B', ' ', '\t', '\n'}, MatchType.POSITIVE)) {{
        setAllUniqueValues("\t", "\n", "\u000B", "\f", "\r", " ", "'", "-", ".", "/");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    TOP_LEVEL_CHOICE_WITHOUT_PARENTHESIS("a|b",
                                         new Choice("a|b", new FinalSymbol("a"), new FinalSymbol("b"))) {{
        setAllUniqueValues("a", "b");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    EMPTY_CHOICE_AT_THE_START_OF_CHOICES("(|A)",
                                         new Group("(|A)", 1, new Choice("(|A)", new FinalSymbol(""), new FinalSymbol("A")))) {{
        setAllUniqueValues("", "A");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    EMPTY_CHOICE_IN_THE_MIDDLE_OF_CHOICES("(B||A)",
                                          new Group("(B||A)", 1, new Choice("(B||A)", new FinalSymbol("B"), new FinalSymbol(""), new FinalSymbol("A")))) {{
        setAllUniqueValues("B", "", "A");
    }},
    //-----------------------------------------------------------------------------------------------------------------------------------------
    EMPTY_CHOICE_AT_THE_END_OF_CHOICES("(A|)",
                                       new Group("(A|)", 1, new Choice("(A|)", new FinalSymbol("A"), new FinalSymbol("")))) {{
        setAllUniqueValues("A", "");
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
    }},
    SLASH_Q_AND_SLASH_E_BASIC("\\Qm\\E",
                              new FinalSymbol("m")) {{
        setAllUniqueValues("m");
    }},
    SLASH_Q_WITHOUT_SLASH_E_BASIC("\\Qmas",
                                  new FinalSymbol("mas")) {{
        setAllUniqueValues("mas");
    }},
    SLASH_E_WITHOUT_SLASH_Q_BASIC("mas\\E",
                                  new FinalSymbol("mas")) {{
        setAllUniqueValues("mas");
        setCannotCompilePattern();
    }},
    SLASH_Q_AND_SLASH_E_IGNORE_SPECIALS("\\Q[a]\\1(a|c).*\\W\\E",
                                        new FinalSymbol("[a]\\1(a|c).*\\W")) {{
        setAllUniqueValues("[a]\\1(a|c).*\\W");
    }},
    SLASH_Q_AND_SLASH_E_WITH_PREFIX_SUFFIX("123\\Qm\\Ezxc",
                                           new FinalSymbol("123mzxc")) {{
        setAllUniqueValues("123mzxc");
    }},
    SLASH_Q_AND_SLASH_E_WITH_REPEAT("123\\Qmass[]\\E{1,2}zxc",
                                    new Sequence("123\\Qmass[]\\E{1,2}zxc",
                                                 new FinalSymbol("123mass["),
                                                 new Repeat("]",
                                                            new FinalSymbol("]"), 1, 2),
                                                 new FinalSymbol("zxc"))) {{
        setAllUniqueValues("123mass[]zxc", "123mass[]]zxc");
    }},
    UNICODE("\\u0041", new FinalSymbol("A")) {{
        setAllUniqueValues("A");
    }};

    final String aPattern;
    final Node   aResultNode;

    BigInteger   aEstimatedCount;
    List<String> aAllUniqueValues;
    boolean      aIsUsableWithJavaPattern;

    TestPattern(String pattern, Node resultNode) {
        aPattern = pattern;
        aResultNode = resultNode;
        aEstimatedCount = TestingUtilities.BIG_INTEGER_MINUS_ONE;
        aIsUsableWithJavaPattern = true;
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
        setAllUniqueValues(asList(values));
    }

    protected final void setAllUniqueValues(List<String> values) {
        aAllUniqueValues = values;
        aEstimatedCount = BigInteger.valueOf(values.size());
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
        return aPattern;
    }
}
