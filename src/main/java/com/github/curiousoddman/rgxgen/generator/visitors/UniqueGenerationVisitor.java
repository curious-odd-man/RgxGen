package com.github.curiousoddman.rgxgen.generator.visitors;

/* **************************************************************************
   Copyright 2019 Vladislavs Varslavans

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
/* **************************************************************************/

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.iterators.ReferenceIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import com.github.curiousoddman.rgxgen.iterators.suppliers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class UniqueGenerationVisitor implements NodeVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UniqueGenerationVisitor.class);

    private final List<Supplier<StringIterator>>        aIterators = new ArrayList<>();
    private final Map<Integer, List<ReferenceIterator>> aReferenceIteratorMap;
    private final Map<Integer, StringIterator>          aGroupIterators;

    public UniqueGenerationVisitor() {
        this(new HashMap<>(), new HashMap<>());
    }

    public UniqueGenerationVisitor(Map<Integer, List<ReferenceIterator>> referenceIteratorMap, Map<Integer, StringIterator> groupIterators) {
        LOGGER.trace("Creating\n\trefs: {}\n\tgrps: {}", referenceIteratorMap, groupIterators);
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

    @Override
    public void visit(LineStart lineStart) {
        throw new UnsupportedOperationException("Cannot generate text with LineStart: " + lineStart.getContext());
    }

    @Override
    public void visit(LineEnd lineEnd) {
        throw new UnsupportedOperationException("Cannot generate text with LineEnd: " + lineEnd.getContext());
    }

    public StringIterator getUniqueStrings() {
        return new PermutationsIteratorSupplier(aIterators).get();
    }
}
