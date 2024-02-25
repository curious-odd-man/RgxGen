package com.github.curiousoddman.rgxgen.iterators;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;


public class IteratorResettingTests {
    private static class TestIterator implements StringIterator {
        private boolean ok = true;

        private String aValue = "a";

        TestIterator() {

        }

        TestIterator(String value) {
            aValue = value;
        }

        @Override
        public String next() {
            if (ok) {
                ok = false;
                return aValue;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void reset() {
            ok = true;
        }

        @Override
        public String current() {
            return aValue;
        }

        @Override
        public boolean hasNext() {
            return ok;
        }

        @Override
        public String toString() {
            return "TestIterator{} ";
        }
    }

    private static class TestBiIterator implements StringIterator {

        private String aCurrent;

        private int ok;

        @Override
        public String next() {
            if (hasNext()) {
                ++ok;
                if (ok == 1) {
                    aCurrent = "x";
                    return "x";
                } else if (ok == 2) {
                    aCurrent = "y";
                    return "y";
                } else {
                    aCurrent = "ERROR";
                    return "ERROR";
                }
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void reset() {
            ok = 0;
        }

        @Override
        public String current() {
            return aCurrent;
        }

        @Override
        public boolean hasNext() {
            return ok < 2;
        }

        @Override
        public String toString() {
            return "TestBiIterator{} ";
        }
    }

    public static Stream<Arguments> data() {
        return Stream.of(
                arguments("Arr", new ArrayIterator(new char[]{'a', 'b'}), 1, Arrays.asList("a", "a", "a", "a")),
                arguments("Arr", new ArrayIterator(new char[]{'a', 'b'}), 3, Arrays.asList("a", "b", null)),
                arguments("Arr", new ArrayIterator(new char[]{'a', 'b'}), 2, Arrays.asList("a", "b", "a", "b")),

                arguments("Single", new SingleValueIterator(), 1, Arrays.asList("", "")),
                arguments("Single", new SingleValueIterator(), 2, Arrays.asList("", null)),
                arguments("Single", new SingleValueIterator("s"), 1, Arrays.asList("s", "s")),
                arguments("Single", new SingleValueIterator("s"), 2, Arrays.asList("s", null, "s")),

                arguments("IncSingle", new IncrementalLengthIterator(TestIterator::new, 1, 2), 1, Arrays.asList("a", "a", "a")),
                arguments("IncSingle", new IncrementalLengthIterator(TestIterator::new, 1, 2), 2, Arrays.asList("a", "aa", "a")),
                arguments("IncSingle", new IncrementalLengthIterator(TestIterator::new, 1, 2), 3, Arrays.asList("a", "aa", null)),

                arguments("IncBi", new IncrementalLengthIterator(TestBiIterator::new, 1, 2), 3, Arrays.asList("x", "y", "xx", "x")),
                arguments("IncBi", new IncrementalLengthIterator(TestBiIterator::new, 1, 2), 10, Arrays.asList("x", "y", "xx", "xy", "yx", "yy", null)),

                arguments("Perm", new PermutationsIterator(Arrays.asList(TestIterator::new, () -> new TestIterator("b"))), 1, Arrays.asList("ab", "ab", "ab")),
                arguments("Perm", new PermutationsIterator(Arrays.asList(TestIterator::new, () -> new TestIterator("b"))), 2, Arrays.asList("ab", null, "ab")),
                arguments("Perm", new PermutationsIterator(Arrays.asList(TestBiIterator::new, TestBiIterator::new)), 5, Arrays.asList("xx", "xy", "yx", "yy", null, "xx")),

                arguments("Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 1, Arrays.asList("a", "a", "a")),
                arguments("Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 2, Arrays.asList("a", "x", "a")),
                arguments("Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 3, Arrays.asList("a", "x", "y")),
                arguments("Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 4, Arrays.asList("a", "x", "y", null)),
                arguments("Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 4, Arrays.asList("a", "x", "y", null, "a")),

                arguments("Case Variations", new CaseVariationIterator("a"), 1, Arrays.asList("a", "a", "a", "a")),
                arguments("Case Variations", new CaseVariationIterator("a"), 2, Arrays.asList("a", "A", "a", "A")),
                arguments("Case Variations", new CaseVariationIterator("a"), 3, Arrays.asList("a", "A", null, "a")),
                arguments("Case Variations", new CaseVariationIterator("ab"), 3, Arrays.asList("ab", "Ab", "aB", "ab"))
        );
    }

    @ParameterizedTest(name = "{0}:{2}/{3}")
    @MethodSource("data")
    public void test(String aName,
                     StringIterator aIterator,
                     int aResetAfter,
                     List<String> aExpectedValues
    ) {
        for (int i = 1; i <= aExpectedValues.size(); i++) {
            String next;
            if (aIterator.hasNext()) {
                next = aIterator.next();
            } else {
                next = null;
            }

            assertEquals(aExpectedValues.get(i - 1), next);
            if (i % aResetAfter == 0) {
                aIterator.reset();
            }
        }
    }
}
