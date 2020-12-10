package com.github.curiousoddman.rgxgen.iterators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertThrows;

@RunWith(Parameterized.class)
public class IteratorsLimitTests {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"ArrayIterator", new ArrayIterator(new Character[]{'a'})},
                {"CaseVariationIterator", new CaseVariationIterator("a")},
                {"ChoiceIterator", new ChoiceIterator(new StringIterator[]{new SingleValueIterator("a"), new SingleValueIterator("a")})},
                {"IncrementalLength", new IncrementalLengthIterator(() -> new SingleValueIterator("a"), 0, 1)},
                {"PermutationIterator", new PermutationsIterator(Arrays.asList(() -> new SingleValueIterator("a"), () -> new SingleValueIterator("a")))},
                {"SingleValueIterator", new SingleValueIterator("a")}
        });
    }

    @Parameterized.Parameter
    public String aName;

    @Parameterized.Parameter(1)
    public StringIterator aIterator;

    @Test
    public void whenDrainedAllElementsThrowsException() {
        while (aIterator.hasNext()) {
            aIterator.next();
        }

        assertThrows(NoSuchElementException.class, () -> aIterator.next());
    }

}
