package com.github.curiousoddman.rgxgen.iterators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ChoiceIteratorTests {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "(A|B)",
                        (Supplier<StringIterator[]>) () -> new StringIterator[]{
                                new SingleValueIterator("A"),
                                new SingleValueIterator("B")
                        },
                        Arrays.asList("A", "B")
                },
                {
                        "(A|B|C|D|E|F)",
                        (Supplier<StringIterator[]>) () -> new StringIterator[]{
                                new SingleValueIterator("A"),
                                new SingleValueIterator("B"),
                                new SingleValueIterator("C"),
                                new SingleValueIterator("D"),
                                new SingleValueIterator("E"),
                                new SingleValueIterator("F")},
                        Arrays.asList("A", "B", "C", "D", "E", "F")
                }
        });
    }

    @Parameterized.Parameter
    public String                     aExpression;
    @Parameterized.Parameter(1)
    public Supplier<StringIterator[]> aIterators;
    @Parameterized.Parameter(2)
    public List<String>               aExpectedValues;

    @Test
    public void countTest() {
        StringIterator stringIterator = new ChoiceIterator(aIterators.get());
        Iterable<String> i = () -> stringIterator;

        Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
        assertEquals(aExpectedValues.size(), stream.count());
    }

    @Test
    public void valuesTest() {
        StringIterator stringIterator = new ChoiceIterator(aIterators.get());
        Iterable<String> i = () -> stringIterator;
        Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
        assertEquals(aExpectedValues, stream.collect(Collectors.toList()));
    }
}
