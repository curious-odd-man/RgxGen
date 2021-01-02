package com.github.curiousoddman.rgxgen.visitors;

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

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.iterators.ReferenceIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import com.github.curiousoddman.rgxgen.iterators.suppliers.*;
import com.github.curiousoddman.rgxgen.nodes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class UniqueGenerationVisitor implements NodeVisitor {
    private final List<Supplier<StringIterator>>        aIterators = new ArrayList<>();
    private final Map<Integer, List<ReferenceIterator>> aReferenceIteratorMap;
    private final Map<Integer, StringIterator>          aGroupIterators;
    private final RgxGenProperties                      aProperties;

    public UniqueGenerationVisitor(RgxGenProperties properties) {
        this(new HashMap<>(), new HashMap<>(), properties);
    }

    public UniqueGenerationVisitor(Map<Integer, List<ReferenceIterator>> referenceIteratorMap, Map<Integer, StringIterator> groupIterators, RgxGenProperties properties) {
        aReferenceIteratorMap = referenceIteratorMap;
        aGroupIterators = groupIterators;
        aProperties = properties;
    }

    @Override
    public void visit(SymbolSet node) {
        if (RgxGenOption.CASE_INSENSITIVE.getBooleanFromProperties(aProperties)) {
            aIterators.add(new ArrayIteratorSupplier(node.getSymbolsCaseInsensitive()));
        } else {
            aIterators.add(new ArrayIteratorSupplier(node.getSymbols()));
        }
    }

    @Override
    public void visit(Choice node) {
        List<List<Supplier<StringIterator>>> nodeIterators = new ArrayList<>(node.getNodes().length);
        for (Node n : node.getNodes()) {
            UniqueGenerationVisitor v = new UniqueGenerationVisitor(aReferenceIteratorMap, aGroupIterators, aProperties);
            n.visit(v);
            nodeIterators.add(v.aIterators);
        }

        aIterators.add(new ChoiceIteratorSupplier(nodeIterators));
    }

    @Override
    public void visit(FinalSymbol node) {
        if (RgxGenOption.CASE_INSENSITIVE.getBooleanFromProperties(aProperties)) {
            aIterators.add(new SingleCaseInsensitiveValueIteratorSupplier(node.getValue()));
        } else {
            aIterators.add(new SingleValueIteratorSupplier(node.getValue()));
        }
    }

    @Override
    public void visit(Repeat node) {
        // Getting all possible sub node contents
        UniqueGenerationVisitor v = new UniqueGenerationVisitor(aReferenceIteratorMap, aGroupIterators, aProperties);
        node.getNode()
            .visit(v);
        aIterators.add(new IncrementalLengthIteratorSupplier(new PermutationsIteratorSupplier(v.aIterators), node.getMin(), node.getMax()));
    }

    @Override
    public void visit(Sequence node) {
        UniqueGenerationVisitor v = new UniqueGenerationVisitor(aReferenceIteratorMap, aGroupIterators, aProperties);
        for (Node n : node.getNodes()) {
            n.visit(v);
        }
        aIterators.add(new PermutationsIteratorSupplier(v.aIterators));
    }

    @Override
    public void visit(NotSymbol node) {
        aIterators.add(new NegativeIteratorSupplier(node.getPattern(), new IncrementalLengthIteratorSupplier(new ArrayIteratorSupplier(SymbolSet.getAllSymbols()), 0, -1)));
    }

    @Override
    public void visit(GroupRef node) {
        aIterators.add(new ReferenceIteratorSupplier(aReferenceIteratorMap, aGroupIterators, node.getIndex()));
    }

    @Override
    public void visit(Group node) {
        UniqueGenerationVisitor v = new UniqueGenerationVisitor(aReferenceIteratorMap, aGroupIterators, aProperties);
        node.getNode()
            .visit(v);

        aIterators.add(new GroupIteratorSupplier(new PermutationsIteratorSupplier(v.aIterators), aReferenceIteratorMap, aGroupIterators, node.getIndex()));
    }

    public StringIterator getUniqueStrings() {
        return aIterators.get(0)
                         .get();
    }
}
