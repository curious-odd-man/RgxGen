package com.github.curiousoddman.rgxgen.iterators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
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

    @Parameterized.Parameters(name = "{0}:{2}/{3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Arr", new ArrayIterator(new Character[]{'a', 'b'}), 1, Arrays.asList("a", "a", "a", "a")},
                {"Arr", new ArrayIterator(new Character[]{'a', 'b'}), 3, Arrays.asList("a", "b", null)},
                {"Arr", new ArrayIterator(new Character[]{'a', 'b'}), 2, Arrays.asList("a", "b", "a", "b")},

                {"Single", new SingleValueIterator(), 1, Arrays.asList("", "")},
                {"Single", new SingleValueIterator(), 2, Arrays.asList("", null)},
                {"Single", new SingleValueIterator("s"), 1, Arrays.asList("s", "s")},
                {"Single", new SingleValueIterator("s"), 2, Arrays.asList("s", null, "s")},

                {"IncSingle", new IncrementalLengthIterator(TestIterator::new, 1, 2), 1, Arrays.asList("a", "a", "a")},
                {"IncSingle", new IncrementalLengthIterator(TestIterator::new, 1, 2), 2, Arrays.asList("a", "aa", "a")},
                {"IncSingle", new IncrementalLengthIterator(TestIterator::new, 1, 2), 3, Arrays.asList("a", "aa", null)},

                {"IncBi", new IncrementalLengthIterator(TestBiIterator::new, 1, 2), 3, Arrays.asList("x", "y", "xx", "x")},
                {"IncBi", new IncrementalLengthIterator(TestBiIterator::new, 1, 2), 10, Arrays.asList("x", "y", "xx", "xy", "yx", "yy", null)},

                {"Perm", new PermutationsIterator(Arrays.asList(TestIterator::new, () -> new TestIterator("b"))), 1, Arrays.asList("ab", "ab", "ab")},
                {"Perm", new PermutationsIterator(Arrays.asList(TestIterator::new, () -> new TestIterator("b"))), 2, Arrays.asList("ab", null, "ab")},
                {"Perm", new PermutationsIterator(Arrays.asList(TestBiIterator::new, TestBiIterator::new)), 5, Arrays.asList("xx", "xy", "yx", "yy", null, "xx")},

                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 1, Arrays.asList("a", "a", "a")},
                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 2, Arrays.asList("a", "x", "a")},
                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 3, Arrays.asList("a", "x", "y")},
                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 4, Arrays.asList("a", "x", "y", null)},
                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 4, Arrays.asList("a", "x", "y", null, "a")},

                {"Case Variations", new CaseVariationIterator("a"), 1, Arrays.asList("a", "a", "a", "a")},
                {"Case Variations", new CaseVariationIterator("a"), 2, Arrays.asList("a", "A", "a", "A")},
                {"Case Variations", new CaseVariationIterator("a"), 3, Arrays.asList("a", "A", null, "a")},
                {"Case Variations", new CaseVariationIterator("ab"), 3, Arrays.asList("ab", "Ab", "aB", "ab")},
        });
    }

    @Parameterized.Parameter(0)
    public String aName;

    @Parameterized.Parameter(1)
    public StringIterator aIterator;

    @Parameterized.Parameter(2)
    public int aResetAfter;

    @Parameterized.Parameter(3)
    public List<String> aExpectedValues;


    @Test
    public void test() {
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
