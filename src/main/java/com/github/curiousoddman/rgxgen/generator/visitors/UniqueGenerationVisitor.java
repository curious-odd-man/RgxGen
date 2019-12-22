package com.github.curiousoddman.rgxgen.generator.visitors;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.iterators.ReferenceIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import com.github.curiousoddman.rgxgen.iterators.suppliers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class UniqueGenerationVisitor implements NodeVisitor {
    private final List<Supplier<StringIterator>>        aIterators = new ArrayList<>();
    private final Map<Integer, List<ReferenceIterator>> aReferenceIteratorMap;
    private final Map<Integer, StringIterator>          aGroupIterators;

    public UniqueGenerationVisitor() {
        this(new HashMap<>(), new HashMap<>());
    }

    public UniqueGenerationVisitor(Map<Integer, List<ReferenceIterator>> referenceIteratorMap, Map<Integer, StringIterator> groupIterators) {
        aReferenceIteratorMap = referenceIteratorMap;
        aGroupIterators = groupIterators;
    }

    @Override
    public void visit(SymbolSet node) {
        aIterators.add(new ArrayIteratorSupplier(node.getSymbols()));
    }

    @Override
    public void visit(Choice node) {
        List<List<Supplier<StringIterator>>> nodeIterators = new ArrayList<>(node.getNodes().length);
        for (Node n : node.getNodes()) {
            UniqueGenerationVisitor v = new UniqueGenerationVisitor(aReferenceIteratorMap, aGroupIterators);
            n.visit(v);
            nodeIterators.add(v.aIterators);
        }

        aIterators.add(new ChoiceIteratorSupplier(nodeIterators));
    }

    @Override
    public void visit(FinalSymbol node) {
        aIterators.add(new SingleValueIteratorSupplier(node.getValue()));
    }

    @Override
    public void visit(Repeat node) {
        // Getting all possible sub node contents
        UniqueGenerationVisitor v = new UniqueGenerationVisitor(aReferenceIteratorMap, aGroupIterators);
        node.getNode()
            .visit(v);
        aIterators.add(new IncrementalLengthIteratorSupplier(new PermutationsIteratorSupplier(v.aIterators), node.getMin(), node.getMax()));
    }

    @Override
    public void visit(Sequence node) {
        for (Node n : node.getNodes()) {
            n.visit(this);
        }
    }

    @Override
    public void visit(NotSymbol notSymbol) {
        aIterators.add(new NegativeIteratorSupplier(notSymbol.getSubPattern(), new IncrementalLengthIteratorSupplier(new ArrayIteratorSupplier(SymbolSet.getAllSymbols()), 0, -1)));
    }

    @Override
    public void visit(GroupRef groupRef) {
        aIterators.add(new ReferenceIteratorSupplier(aReferenceIteratorMap, aGroupIterators, groupRef.getIndex()));
    }

    @Override
    public void visit(Group group) {
        UniqueGenerationVisitor v = new UniqueGenerationVisitor(aReferenceIteratorMap, aGroupIterators);
        group.getNode()
             .visit(v);

        aIterators.add(new GroupIteratorSupplier(new PermutationsIteratorSupplier(v.aIterators), aReferenceIteratorMap, aGroupIterators, group.getIndex()));
    }

    public StringIterator getUniqueStrings() {
        return new PermutationsIteratorSupplier(aIterators).get();
    }
}
