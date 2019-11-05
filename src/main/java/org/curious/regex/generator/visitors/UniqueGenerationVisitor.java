package org.curious.regex.generator.visitors;

import org.curious.regex.generator.nodes.*;
import org.curious.regex.util.Permutations;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class UniqueGenerationVisitor implements NodeVisitor {

    private Stream<String> aStringStream;

    /**
     * The supplier is required because stream cannot be reused.
     * We need new stream each time to map elements. TODO: Maybe there is better solution?
     *
     * @param stream
     */
    private void putOrMap(Supplier<Stream<String>> stream) {
        if (aStringStream == null) {
            aStringStream = stream.get();
        } else {
            aStringStream = aStringStream.flatMap(v -> stream.get()
                                                             .map(vv -> v + vv));
        }
    }

    @Override
    public void visit(AnySymbol node) {
        putOrMap(() -> Stream.of(AnySymbol.ALL_SYMBOLS));
    }

    @Override
    public void visit(Choice node) {
        putOrMap(() -> Arrays.stream(node.getNodes())
                             .flatMap(n -> {
                                 UniqueGenerationVisitor v = new UniqueGenerationVisitor();
                                 n.visit(v);
                                 return v.aStringStream;
                             }));
    }

    @Override
    public void visit(FinalSymbol node) {
        putOrMap(() -> Stream.of(node.getValue()));
    }

    private static Stream<String> combine(String[] values, int takeCnt) {
        return Permutations.withRepetitions(takeCnt, () -> Arrays.stream(values), arr -> Stream.of(arr)
                                                                                               .reduce("", String::concat), String[]::new);
    }

    @Override
    public void visit(Repeat node) {
        // Getting all possible sub node contents
        UniqueGenerationVisitor v = new UniqueGenerationVisitor();
        node.getNode()
            .visit(v);
        String[] strings = v.aStringStream.toArray(String[]::new);

        // (a|b){1} -> "a", "b" --> "a", "b"
        // (a|b){2} -> "a", "b" --> "aa", "ab", "ba", "bb"
        // (a|b){1,2} -> "a", "b" --> "a", "b", "aa", "ab", "ba", "bb"
        // (a|b){0,2} -> "a", "b" --> "", "a", "b", "aa", "ab", "ba", "bb"


        // Take 0 from list
        // Take 1 from list
        // Take and concatenate 2 from list
        // ...

        putOrMap(() -> {
            Stream<String> tmp = Stream.empty();
            for (long i = node.getMin(); i <= node.getMax(); ++i) {
                tmp = Stream.concat(tmp, combine(strings, (int) i));
            }
            return tmp;
        });
    }

    @Override
    public void visit(Sequence node) {
        for (Node n : node.getNodes()) {
            n.visit(this);
        }
    }

    public Stream<String> getUniqueStrings() {
        return aStringStream == null ? Stream.empty() : aStringStream.distinct();
    }
}
