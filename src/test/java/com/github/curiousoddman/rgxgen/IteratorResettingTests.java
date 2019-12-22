package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.iterators.*;
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
    private static class TestIterator extends StringIterator {
        private boolean ok = true;

        private String aValue = "a";

        public TestIterator() {

        }

        public TestIterator(String value) {
            aValue = value;
        }

        @Override
        protected String nextImpl() {
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
        public boolean hasNext() {
            return ok;
        }

        @Override
        public String toString() {
            return "TestIterator{} ";
        }
    }

    private static class TestBiIterator extends StringIterator {

        private int ok = 0;

        @Override
        protected String nextImpl() {
            if (hasNext()) {
                ++ok;
                if (ok == 1) {
                    return "x";
                } else if (ok == 2) {
                    return "y";
                } else {
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
                {"Arr", new ArrayIterator(new String[]{"a", "b"}), 1, 4, Arrays.asList("a", "a", "a", "a")},
                {"Arr", new ArrayIterator(new String[]{"a", "b"}), 3, 3, Arrays.asList("a", "b", null)},
                {"Arr", new ArrayIterator(new String[]{"a", "b"}), 2, 4, Arrays.asList("a", "b", "a", "b")},

                {"Single", new SingleValueIterator(), 1, 2, Arrays.asList("", "")},
                {"Single", new SingleValueIterator(), 2, 2, Arrays.asList("", null)},
                {"Single", new SingleValueIterator("s"), 1, 2, Arrays.asList("s", "s")},
                {"Single", new SingleValueIterator("s"), 2, 3, Arrays.asList("s", null, "s")},

                {"IncSingle", new IncrementalLengthIterator(TestIterator::new, 1, 2), 1, 3, Arrays.asList("a", "a", "a")},
                {"IncSingle", new IncrementalLengthIterator(TestIterator::new, 1, 2), 2, 3, Arrays.asList("a", "aa", "a")},
                {"IncSingle", new IncrementalLengthIterator(TestIterator::new, 1, 2), 3, 3, Arrays.asList("a", "aa", null)},

                {"IncBi", new IncrementalLengthIterator(TestBiIterator::new, 1, 2), 3, 4, Arrays.asList("x", "y", "xx", "x")},
                {"IncBi", new IncrementalLengthIterator(TestBiIterator::new, 1, 2), 10, 7, Arrays.asList("x", "y", "xx", "xy", "yx", "yy", null)},

                {"Perm", new PermutationsIterator(Arrays.asList(TestIterator::new, () -> new TestIterator("b"))), 1, 3, Arrays.asList("ab", "ab", "ab")},
                {"Perm", new PermutationsIterator(Arrays.asList(TestIterator::new, () -> new TestIterator("b"))), 2, 3, Arrays.asList("ab", null, "ab")},
                {"Perm", new PermutationsIterator(Arrays.asList(TestBiIterator::new, TestBiIterator::new)), 5, 6, Arrays.asList("xx", "xy", "yx", "yy", null, "xx")},

                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 1, 3, Arrays.asList("a", "a", "a")},
                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 2, 3, Arrays.asList("a", "x", "a")},
                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 3, 3, Arrays.asList("a", "x", "y")},
                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 4, 4, Arrays.asList("a", "x", "y", null)},
                {"Choice", new ChoiceIterator(new StringIterator[]{new TestIterator(), new TestBiIterator()}), 4, 5, Arrays.asList("a", "x", "y", null, "a")},
                });
    }

    @Parameterized.Parameter(0)
    public String aName;

    @Parameterized.Parameter(1)
    public StringIterator aIterator;

    @Parameterized.Parameter(2)
    public int aResetAfter;

    @Parameterized.Parameter(3)
    public int aNumIterations;

    @Parameterized.Parameter(4)
    public List<String> aExpectedValues;


    @Test
    public void test() {
        for (int i = 1; i <= aNumIterations; i++) {
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
