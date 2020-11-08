package com.github.curiousoddman.rgxgen.iterators;

import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CaseVariationIteratorTests {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                {"A", Arrays.asList("a", "A")},
                {"a", Arrays.asList("a", "A")},
                {"1", Arrays.asList("1")},
                {"1a", Arrays.asList("1a", "1A")},
                {"1ab", Arrays.asList("1ab", "1Ab", "1aB", "1AB")},
                {"a1b", Arrays.asList("a1b", "A1b", "a1B", "A1B")},
                {"A1B", Arrays.asList("a1b", "A1b", "a1B", "A1B")},
                {"abc", Arrays.asList("abc", "Abc", "aBc", "ABc", "abC", "AbC", "aBC", "ABC")},
                {"AbC", Arrays.asList("abc", "Abc", "aBc", "ABc", "abC", "AbC", "aBC", "ABC")},
                {"ABC", Arrays.asList("abc", "Abc", "aBc", "ABc", "abC", "AbC", "aBC", "ABC")}
                });
    }

    @Parameterized.Parameter
    public String aInput;

    @Parameterized.Parameter(1)
    public List<String> aExpected;

    @Test
    public void test() {
        CaseVariationIterator caseVariationIterator = new CaseVariationIterator(aInput);
        List<String> strings = TestingUtilities.iteratorToList(caseVariationIterator);
        assertEquals(aExpected, strings);
    }

    @Test(expected = NoSuchElementException.class)
    public void testThrows() {
        CaseVariationIterator caseVariationIterator = new CaseVariationIterator(aInput);
        List<String> strings = TestingUtilities.iteratorToList(caseVariationIterator);
        assertFalse(caseVariationIterator.hasNext());
        caseVariationIterator.next();       // Should throw
        fail("Expected to be terminated by exception...");
    }
}
