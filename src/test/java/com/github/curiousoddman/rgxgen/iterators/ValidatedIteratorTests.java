package com.github.curiousoddman.rgxgen.iterators;

import com.github.curiousoddman.rgxgen.validation.Validator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidatedIteratorTests {

    private static class TestIterator implements StringIterator {

        private final Collection<String> aTexts;

        private Iterator<String> aIterator;
        private String           lastValue = null;

        public TestIterator(String... texts) {
            aTexts = Arrays.asList(texts);
            aIterator = aTexts.iterator();
        }

        @Override
        public void reset() {
            aIterator = aTexts.iterator();
        }

        @Override
        public String current() {
            return lastValue;
        }

        @Override
        public boolean hasNext() {
            return aIterator.hasNext();
        }

        @Override
        public String next() {
            lastValue = aIterator.next();
            return lastValue;
        }
    }

    @Mock
    private Validator aValidator;

    private ValidatedIterator aValidatedIterator;

    @Test
    public void next_returnsNext_whenThereAreMoreElements() {
        aValidatedIterator = new ValidatedIterator(aValidator, new TestIterator("A", "B", "C"));

        when(aValidator.isValid(anyString())).thenReturn(true);
        assertEquals("A", aValidatedIterator.next());
        assertEquals("B", aValidatedIterator.next());
        assertEquals("C", aValidatedIterator.next());
    }

    @Test(expected = NoSuchElementException.class)
    public void next_throws_whenNoMoreElements() {
        aValidatedIterator = new ValidatedIterator(aValidator, new TestIterator());
        aValidatedIterator.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void next_throws_afterFirstElement() {
        aValidatedIterator = new ValidatedIterator(aValidator, new TestIterator("A"));

        assertEquals("A", aValidatedIterator.next());
        aValidatedIterator.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void next_throws_whenThereAreOnlyInvalidElementsLeft() {
        aValidatedIterator = new ValidatedIterator(aValidator, new TestIterator("A", "B", "C"));
        when(aValidator.isValid(anyString())).thenReturn(false);
        aValidatedIterator.next();
    }

    @Test
    public void hasNext_returnsTrue_whenThereAreMoreElements() {
        aValidatedIterator = new ValidatedIterator(aValidator, new TestIterator("A"));

        when(aValidator.isValid(anyString())).thenReturn(true);

        assertTrue(aValidatedIterator.hasNext());
    }

    @Test
    public void hasNext_returnsFalse_whenThereAreNoMoreElements() {
        aValidatedIterator = new ValidatedIterator(aValidator, new TestIterator());
        assertFalse(aValidatedIterator.hasNext());
    }

    @Test
    public void hasNext_returnsFalse_whenThereAreOnlyInvalidElementsLeft() {
        aValidatedIterator = new ValidatedIterator(aValidator, new TestIterator("A", "B"));

        when(aValidator.isValid(anyString())).thenReturn(false);

        assertFalse(aValidatedIterator.hasNext());
    }

    @Test
    public void current_returnsCurrentElement() {
        aValidatedIterator = new ValidatedIterator(aValidator, new TestIterator("A", "B"));

        when(aValidator.isValid(anyString())).thenReturn(true);

        assertEquals("A", aValidatedIterator.next());
        assertEquals("A", aValidatedIterator.current());

    }

    @Test
    public void reset_reStartsFromBeginning() {
        aValidatedIterator = new ValidatedIterator(aValidator, new TestIterator("A", "B", "C"));

        when(aValidator.isValid(eq("A"))).thenReturn(true);
        when(aValidator.isValid(eq("B"))).thenReturn(false);
        when(aValidator.isValid(eq("C"))).thenReturn(true);

        assertEquals("A", aValidatedIterator.next());
        assertEquals("C", aValidatedIterator.next());

        assertFalse(aValidatedIterator.hasNext());

        aValidatedIterator.reset();

        assertEquals("A", aValidatedIterator.next());
        assertEquals("C", aValidatedIterator.next());

        assertFalse(aValidatedIterator.hasNext());
    }
}
