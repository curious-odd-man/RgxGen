package com.github.curious.rgxgen.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Spliterator;
import java.util.stream.Stream;

public final class Permutations {
    private Permutations() {
    }

    /**
     * @param length the length of resulting permutations
     * @param values values to permute
     * @return stream of permuted values
     */
    public static Stream<String> withRepetitions(int length, String[] values) {
        if (length == 0) {
            return Stream.of("");
        }

        class Tmp {
            ArrayList<Spliterator<String>> spliterators    = new ArrayList<>(length);
            String[]                       generatedValues = new String[length];

            public Tmp() {
                for (int i = 0; i < length; i++) {
                    Spliterator<String> spliterator = Arrays.spliterator(values);
                    spliterators.add(spliterator);
                    int finalI = i;
                    spliterator.tryAdvance(v -> generatedValues[finalI] = v);
                }
            }

            public String[] next() {
                for (int i = length - 1; i >= 0; --i) {
                    int finalI = i;
                    if (spliterators.get(i)
                                    .tryAdvance(v -> generatedValues[finalI] = v)) {
                        break;
                    } else {
                        Spliterator<String> spliterator = Arrays.spliterator(values);
                        spliterators.set(i, spliterator);
                        spliterator.tryAdvance(v -> generatedValues[finalI] = v);
                    }
                }

                return generatedValues.clone();
            }
        }

        Tmp tmp = new Tmp();

        return Stream.iterate(tmp.generatedValues.clone(), v -> tmp.next())
                     .limit(Util.pow(values.length, length))
                     .map(Arrays::stream)
                     .map(v -> v.reduce("", String::concat));


    }
}