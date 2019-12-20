package com.github.curiousoddman.rgxgen.generator.visitors;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.iterators.*;

import java.util.*;
import java.util.function.Supplier;

public class UniqueGenerationVisitor implements NodeVisitor {
    private final List<Supplier<StringIterator>> aIterators = new ArrayList<>();
    private final Map<Integer, Observable>       aGroupObservables;
    private final Map<Integer, List<Observer>>   aGroupObservers;

    public UniqueGenerationVisitor() {
        this(new HashMap<>(), new HashMap<>());
    }

    public UniqueGenerationVisitor(Map<Integer, Observable> groupObservables, Map<Integer, List<Observer>> groupObservers) {
        aGroupObservables = groupObservables;
        aGroupObservers = groupObservers;
    }

    private static StringIterator permutationsOrFlat(List<Supplier<StringIterator>> itSupp) {
        if (itSupp.size() == 1) {
            return itSupp.get(0)
                         .get();
        } else {
            return new PermutationsIterator(itSupp);
        }
    }

    @Override
    public void visit(SymbolSet node) {
        aIterators.add(() -> new ArrayIterator(node.getSymbols()));
    }

    @Override
    public void visit(Choice node) {
        List<List<Supplier<StringIterator>>> nodeIterators = new ArrayList<>(node.getNodes().length);
        for (Node n : node.getNodes()) {
            UniqueGenerationVisitor v = new UniqueGenerationVisitor(aGroupObservables, aGroupObservers);
            n.visit(v);
            nodeIterators.add(v.aIterators);
        }

        aIterators.add(() -> new ChoiceIterator(nodeIterators));
    }

    @Override
    public void visit(FinalSymbol node) {
        aIterators.add(() -> new SingleValueIterator(node.getValue()));
    }

    @Override
    public void visit(Repeat node) {
        // Getting all possible sub node contents
        UniqueGenerationVisitor v = new UniqueGenerationVisitor(aGroupObservables, aGroupObservers);
        node.getNode()
            .visit(v);

        // (a|b){1} -> "a", "b" --> "a", "b"
        // (a|b){2} -> "a", "b" --> "aa", "ab", "ba", "bb"
        // (a|b){1,2} -> "a", "b" --> "a", "b", "aa", "ab", "ba", "bb"
        // (a|b){0,2} -> "a", "b" --> "", "a", "b", "aa", "ab", "ba", "bb"


        // Take 0 from list
        // Take 1 from list
        // Take and concatenate 2 from list
        // ...

        aIterators.add(() -> new IncrementalLengthIterator(() -> permutationsOrFlat(v.aIterators), node.getMin(), node.getMax()));
    }

    @Override
    public void visit(Sequence node) {
        for (Node n : node.getNodes()) {
            n.visit(this);
        }
    }

    @Override
    public void visit(NotSymbol notSymbol) {
        aIterators.add(() -> new NegativeStringIterator(
                new IncrementalLengthIterator(() -> new ArrayIterator(SymbolSet.getAllSymbols()), 0, -1),
                notSymbol.getSubPattern()
        ));
    }

    @Override
    public void visit(GroupRef groupRef) {
        aIterators.add(() -> {
            ObserverIterator observerIterator = new ObserverIterator();
            final Observable observable = aGroupObservables.get(groupRef.getIndex());
            if (observable == null) {
                final List<Observer> observerList = aGroupObservers.computeIfAbsent(groupRef.getIndex(), i -> new ArrayList<>());
                observerList.add(observerIterator.getObserver());
            } else {
                observable.addObserver(observerIterator.getObserver());
            }

            return observerIterator;
        });
    }

    @Override
    public void visit(Group group) {
        UniqueGenerationVisitor v = new UniqueGenerationVisitor(aGroupObservables, aGroupObservers);
        group.getNode()
             .visit(v);

        aIterators.add(() -> {
            final StringIterator stringIterator = permutationsOrFlat(v.aIterators);
            final List<Observer> observers = aGroupObservers.get(group.getIndex());
            if (observers != null) {
                observers.forEach(stringIterator::addObserver);
            }

            aGroupObservables.put(group.getIndex(), stringIterator);
            return stringIterator;
        });
    }

    public StringIterator getUniqueStrings() {
        return permutationsOrFlat(aIterators);
    }
}
