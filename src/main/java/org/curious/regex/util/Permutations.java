package org.curious.regex.util;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Permutations {
    private Permutations() {
    }

    /**
     * @param length                the length of resulting permutations
     * @param values                values to permute
     * @param <T>                   type of values
     * @param concatenationFunction function to transform ArrayList into single value
     * @return stream of permuted values
     */
    public static <T, R> Stream<R> withRepetitions(int length, Collection<T> values, Function<T[], R> concatenationFunction, IntFunction<T[]> arrSupplier) {
        return withRepetitions(length, values::stream, concatenationFunction, arrSupplier);
    }

    /**
     * @param length                the length of resulting permutations
     * @param values                values to permute
     * @param <T>                   type of values
     * @param concatenationFunction function to transform ArrayList into single value
     * @return stream of permuted values
     */
    public static <T, R> Stream<R> withRepetitions(int length, Supplier<Stream<T>> values, Function<T[], R> concatenationFunction, IntFunction<T[]> arrSupplier) {
        return withRepetitions(length, values, arrSupplier).map(concatenationFunction);
    }

    /**
     * @param length the length of resulting permutations
     * @param values values to permute
     * @param <T>    type of values
     * @return stream of permuted values
     */
    public static <T> Stream<T[]> withRepetitions(int length, Supplier<Stream<T>> values, IntFunction<T[]> arrSupplier) {
        if (length == 0) {
            Stream.Builder<T[]> builder = Stream.builder();
            builder.accept(arrSupplier.apply(0));
            return builder.build();
        }

        Stream<T[]> stream = values.get()
                                   .map(v -> {
                                       T[] arrayList = arrSupplier.apply(length);
                                       arrayList[0] = v;
                                       return arrayList;
                                   });

        for (int i = 1; i < length; i++) {
            int index = i;
            stream = stream.flatMap(arr -> values.get()
                                                 .map(v -> {
                                                     arr[index] = v;
                                                     return arr;
                                                 }));
        }

        return stream;
    }
}