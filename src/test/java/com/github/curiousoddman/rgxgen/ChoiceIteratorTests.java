package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.iterators.ChoiceIterator;
import com.github.curiousoddman.rgxgen.iterators.SingleValueIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
                        Arrays.asList(
                                Arrays.asList(
                                        (Supplier<Iterator<String>>) () -> new SingleValueIterator("A"),
                                        () -> new SingleValueIterator("B")
                                )
                        ),
                        Arrays.asList("A", "B")
                },
                {
                        "(A|B|C|D|E|F)",
                        Arrays.asList(
                                Arrays.asList(
                                        (Supplier<Iterator<String>>) () -> new SingleValueIterator("A"),
                                        () -> new SingleValueIterator("B"),
                                        () -> new SingleValueIterator("C"),
                                        () -> new SingleValueIterator("D"),
                                        () -> new SingleValueIterator("E"),
                                        () -> new SingleValueIterator("F")
                                )
                        ),
                        Arrays.asList("A", "B", "C", "D", "E", "F")
                }
        });
    }

    @Parameterized.Parameter
    public String                                 aExpression;
    @Parameterized.Parameter(1)
    public List<List<Supplier<Iterator<String>>>> aIterators;
    @Parameterized.Parameter(2)
    public List<String>                           aExpectedValues;

    @Test
    public void countTest() {
        Iterator<String> stringIterator = new ChoiceIterator(aIterators);
        Iterable<String> i = () -> stringIterator;

        Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
        assertEquals(aExpectedValues.size(), stream.count());
    }

    @Test
    public void valuesTest() {
        Iterator<String> stringIterator = new ChoiceIterator(aIterators);
        Iterable<String> i = () -> stringIterator;
        Stream<String> stream = StreamSupport.stream(i.spliterator(), false);
        assertEquals(aExpectedValues, stream.collect(Collectors.toList()));
    }
}
