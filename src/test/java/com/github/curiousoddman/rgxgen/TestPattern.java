package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public enum TestPattern {
    SIMPLE_A("a",
             new FinalSymbol("a"),
             BigInteger.ONE,
             Collections.singletonList("a")),
    SIMPLE_A_WITH_START_END("^a$",
                            new FinalSymbol("a"),
                            BigInteger.ONE,
                            Collections.singletonList("a")),
    ANY_DIGIT("\\d",
              new SymbolSet(IntStream.rangeClosed(0, 9)
                                     .mapToObj(Integer::toString)
                                     .toArray(String[]::new), SymbolSet.TYPE.POSITIVE),
              BigInteger.TEN,
              Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
    NOT_A_DIGIT("\\D",      // Any non-digit
                new SymbolSet(IntStream.rangeClosed(0, 9)
                                       .mapToObj(Integer::toString)
                                       .toArray(String[]::new), SymbolSet.TYPE.NEGATIVE)
    ),
    ANY_DIGIT_RANGE("[0-9]",
                    new SymbolSet(IntStream.rangeClosed(0, 9)
                                           .mapToObj(Integer::toString)
                                           .toArray(String[]::new), SymbolSet.TYPE.POSITIVE),
                    BigInteger.TEN,
                    Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
    LETTER_RANGE("[a-cA-C]",
                 new SymbolSet(Arrays.asList(new SymbolSet.SymbolRange('a', 'c'), new SymbolSet.SymbolRange('A', 'C')), SymbolSet.TYPE.POSITIVE)
    ),
    ANY_WORD_CHARACTER("\\w",      // Any word character  [a-zA-Z0-9_]
                       new SymbolSet(Arrays.asList(SymbolSet.SymbolRange.SMALL_LETTERS, SymbolSet.SymbolRange.CAPITAL_LETTERS, SymbolSet.SymbolRange.DIGITS), new String[]{"_"}, SymbolSet.TYPE.POSITIVE)
    ),
    ANY_NON_WORD_CHARACTER("\\W",      // Any non-word symbol  [a-zA-Z0-9_]
                           new SymbolSet(Arrays.asList(SymbolSet.SymbolRange.SMALL_LETTERS, SymbolSet.SymbolRange.CAPITAL_LETTERS, SymbolSet.SymbolRange.DIGITS), new String[]{"_"}, SymbolSet.TYPE.NEGATIVE)
    ),
    HEX_SPACE("\\x20", // Space
              new FinalSymbol(" ")
    ),
    HEX_SYMBOL("\\x{26F8}",
               new FinalSymbol("⛸")),
    HEX_SPACE_THEN_A("\\x20a", // Space
                     new FinalSymbol(" a")),
    HEX_SYMBOL_THEN_A("\\x{26F8}a",
                      new FinalSymbol("⛸a")),

    A_OR_B("[ab]",
           new SymbolSet(new String[]{
                   "a", "b"
           }, SymbolSet.TYPE.POSITIVE),
           BigInteger.valueOf(2),
           Arrays.asList("a", "b")),

    A_OR_B_THEN_C("[ab]c",
                  new Sequence(new SymbolSet(new String[]{
                          "a", "b"
                  }, SymbolSet.TYPE.POSITIVE), new FinalSymbol("c")), BigInteger.valueOf(2)),

    D_THEN_A_OR_B_THEN_C("d[ab]c",
                         new Sequence(new FinalSymbol("d"), new SymbolSet(new String[]{
                                 "a", "b"
                         }, SymbolSet.TYPE.POSITIVE), new FinalSymbol("c")),
                         BigInteger.valueOf(2)),

    A_REPEAT_RANGE("a{2,5}",
                   new Repeat(new FinalSymbol("a"), 2, 5),
                   BigInteger.valueOf(4),
                   Arrays.asList("aa", "aaa", "aaaa", "aaaaa")),

    A_REPEAT_CONST("a{2}",
                   new Repeat(new FinalSymbol("a"), 2),
                   BigInteger.ONE,
                   Collections.singletonList("aa")),

    A_REPEAT_60K("a{60000}",
                 new Repeat(new FinalSymbol("a"), 60000),
                 BigInteger.ONE,
                 Collections.singletonList(Stream.generate(() -> "a")
                                                 .limit(60000)
                                                 .reduce("", String::concat))),

    A_OR_B_REPEAT_CONST(
            "(a|b){2}",
            new Repeat(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))), 2),
            BigInteger.valueOf(4),
            Arrays.asList("aa", "ab", "ba", "bb")),

    A_OR_B_REPEAT_RANGE("(a|b){0,2}",
                        new Repeat(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))), 0, 2),
                        BigInteger.valueOf(7),
                        Arrays.asList("", "a", "b", "aa", "ab", "ba", "bb")),

    A_REPEAT_OR_B_REPEAT("(a{0,2}|b{0,2})",
                         new Group(1, new Choice(new Repeat(new FinalSymbol("a"), 0, 2), new Repeat(new FinalSymbol("b"), 0, 2))),
                         BigInteger.valueOf(6), // Actual is 5! but left and right side can yield same values - that can't be analysed by counting
                         Arrays.asList("", "a", "aa", "", "b", "bb")
    ),

    NOTHING_OR_A_REPEAT_OR_B_REPEAT("(|(a{1,2}|b{1,2}))",
                                    new Group(1, new Choice(new FinalSymbol(""), new Group(2, new Choice(new Repeat(new FinalSymbol("a"), 1, 2), new Repeat(new FinalSymbol("b"), 1, 2))))),
                                    BigInteger.valueOf(5),
                                    Arrays.asList("", "a", "aa", "b", "bb")),

    A_THEN_ANY("a.",
               new Sequence(new FinalSymbol("a"), new SymbolSet()),
               BigInteger.valueOf(95),
               Arrays.stream(SymbolSet.getAllSymbols())
                     .map(s -> 'a' + s)
                     .collect(Collectors.toList())),

    ANY_THEN_ANY("..",
                 new Sequence(new SymbolSet(), new SymbolSet()),
                 BigInteger.valueOf(95 * 95),
                 Arrays.stream(SymbolSet.getAllSymbols())
                       .flatMap(s -> Arrays.stream(SymbolSet.getAllSymbols())
                                           .map(v -> s + v))
                       .collect(Collectors.toList())),

    A_REPEAT_ZERO_OR_MORE("a*",
                          Repeat.minimum(new FinalSymbol("a"), 0),
                          null),

    A_REPEAT_MIN_4("a{4,}",
                   Repeat.minimum(new FinalSymbol("a"), 4)
    ),

    NOT_A("[^a]",
          new SymbolSet(Arrays.stream(SymbolSet.getAllSymbols())
                              .filter(s -> !s.equals("a"))
                              .toArray(String[]::new), SymbolSet.TYPE.POSITIVE)
    ),

    NOT_LETTER_RANGE("[^a-dE-F]",
                     new SymbolSet(Arrays.stream(SymbolSet.getAllSymbols())
                                         .filter(s -> !(s.equals("a") || s.equals("b") || s.equals("c") || s.equals("d") || s.equals("E") || s.equals("F")))
                                         .toArray(String[]::new), SymbolSet.TYPE.POSITIVE)
    ),

    ANY_WHITESPACE("\\s",      // Any White Space
                   new SymbolSet(new String[]{
                           "\r", "\f", "\u000B", " ", "\t", "\n"
                   }, SymbolSet.TYPE.POSITIVE)
    ),

    NOT_A_WHITESPACE("\\S",      // Any Non White Space
                     new SymbolSet(new String[]{
                             "\r", "\f", "\u000B", " ", "\t", "\n"
                     }, SymbolSet.TYPE.NEGATIVE)
    ),

    A_THEN_A_OR_NOT("aa?",
                    new Sequence(new FinalSymbol("a"), new Repeat(new FinalSymbol("a"), 0, 1)),
                    BigInteger.valueOf(2),
                    Arrays.asList("a", "aa")),

    A_THEN_A_ONE_OR_MORE("aa+",
                         new Sequence(new FinalSymbol("a"), Repeat.minimum(new FinalSymbol("a"), 1)),
                         null),

    A_THEN_ANY_REPEAT_INFINITE("a.*",      // If use unlimited repetition that will cause an error when trying to save all data in memory, thus we limit repetition times
                               new Sequence(new FinalSymbol("a"), Repeat.minimum(new SymbolSet(), 0)),
                               null),

    POSITIVE_LOOKAHEAD("foo(?=bar)",
                       new Sequence(new FinalSymbol("foo"), new FinalSymbol("bar"))
    ),
    NEGATIVE_LOOKAHEAD("foo(?!bar)",
                       new Sequence(new FinalSymbol("foo"), new NotSymbol("bar")),
                       null),

    POSITIVE_LOOKBEHIND("(?<=foo)bar",
                        new Sequence(new FinalSymbol("foo"), new FinalSymbol("bar"))
    ),

    NEGATIVE_LOOKBEHIND("(?<!not)foo",
                        new Sequence(new NotSymbol("not"), new FinalSymbol("foo")),
                        null),

    CHOICE_CAPTURED("(a|b)\\1",
                    new Sequence(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))),
                                 new GroupRef(1)),
                    BigInteger.valueOf(2),
                    Arrays.asList("aa", "bb")
    ),

    CAPTURE_REPEAT("(a|b){2,3}\\1",
                   new Sequence(new Repeat(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))), 2, 3),
                                new GroupRef(1)),
                   BigInteger.valueOf(12),
                   Arrays.asList("aaa", "abb", "baa", "bbb", "aaaa", "aabb", "abaa", "abbb", "baaa", "babb", "bbaa", "bbbb")),
    CAPTURE_REPEAT_0("(a|b){3}\\1",
                     new Sequence(new Repeat(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))), 3, 3),
                                  new GroupRef(1)),
                     BigInteger.valueOf(8),
                     Arrays.asList("aaaa", "aabb", "abaa", "abbb", "baaa", "babb", "bbaa", "bbbb")),
    CAPTURE_REPEAT_1("(a|b){2}\\1",
                     new Sequence(new Repeat(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))), 2, 2),
                                  new GroupRef(1)),
                     BigInteger.valueOf(4),
                     Arrays.asList("aaa", "abb", "baa", "bbb")),
    CAPTURE_REF_REPEAT("(a|b)\\1{2,3}",
                       new Sequence(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))),
                                    new Repeat(new GroupRef(1), 2, 3)),
                       BigInteger.valueOf(4),
                       Arrays.asList("aaa", "aaaa", "bbb", "bbbb")
    ),
    CAPTURE_REPEAT_AND_REF_REPEAT(
            "(a|b){2,3}\\1{2,3}",
            new Sequence(new Repeat(new Group(1, new Choice(new FinalSymbol("a"), new FinalSymbol("b"))), 2, 3),
                         new Repeat(new GroupRef(1), 2, 3)),
            BigInteger.valueOf(24),       // Real count is less, since some patterns repeat. e.g. "aaaaa"
            Arrays.asList("aaaa", "aaaaa", "abbb", "abbbb", "baaa", "baaaa", "bbbb", "bbbbb", "aaaaa", "aaaaaa", "aabbb", "aabbbb", "abaaa", "abaaaa", "abbbb", "abbbbb", "baaaa", "baaaaa", "babbb", "babbbb", "bbaaa", "bbaaaa", "bbbbb", "bbbbbb")
    ),
    XML_NODE("<([abc])>d<\\/\\1>",
             new Sequence(new FinalSymbol("<"),
                          new Group(1, new SymbolSet(new String[]{
                                  "a", "b", "c"
                          }, SymbolSet.TYPE.POSITIVE)),
                          new FinalSymbol(">d</"),
                          new GroupRef(1),
                          new FinalSymbol(">")
             ),
             BigInteger.valueOf(3),
             Arrays.asList("<a>d</a>", "<b>d</b>", "<c>d</c>")),
    METASEQUENCE_IN_SQUARE_BRACKETS("['\\-/\\.\\s]",
                                    new SymbolSet(new String[]{"'", "-", "/", ".", "\r", "\f", "\u000B", " ", "\t", "\n"}, SymbolSet.TYPE.POSITIVE),
                                    BigInteger.valueOf(10),
                                    Arrays.asList("'", "-", "/", ".", "\r", "\f", "\u000B", " ", "\t", "\n")),
    TOP_LEVEL_CHOICE_WITHOUT_PARENTHESIS("a|b",
                                         new Choice(new FinalSymbol("a"), new FinalSymbol("b")),
                                         BigInteger.valueOf(2),
                                         Arrays.asList("a", "b")),
    EMPTY_CHOICE_AT_THE_START_OF_CHOICES("(|A)",
                                         new Group(1, new Choice(new FinalSymbol(""), new FinalSymbol("A"))),
                                         BigInteger.valueOf(2),
                                         Arrays.asList("", "A")),
    EMPTY_CHOICE_AT_THE_END_OF_CHOICES("(A|)",
                                       new Group(1, new Choice(new FinalSymbol("A"), new FinalSymbol(""))),
                                       BigInteger.valueOf(2),
                                       Arrays.asList("A", ""));

    final String       aPattern;
    final Node         aResultNode;
    final BigInteger   aEstimatedCount;
    final List<String> aAllUniqueValues;

    TestPattern(String pattern, Node resultNode) {
        this(pattern, resultNode, BigInteger.valueOf(-1), null);
    }

    TestPattern(String pattern, Node resultNode, BigInteger estimatedCount) {
        this(pattern, resultNode, estimatedCount, null);
    }

    TestPattern(String pattern, Node resultNode, BigInteger estimatedCount, List<String> allUniqueValues) {
        aPattern = pattern;
        aResultNode = resultNode;
        aEstimatedCount = estimatedCount;
        aAllUniqueValues = allUniqueValues;
    }

    public boolean hasEstimatedCound() {
        return aEstimatedCount == null || !aEstimatedCount.equals(BigInteger.valueOf(-1));
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

    @Override
    public String toString() {
        return aPattern;
    }
}
