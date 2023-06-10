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
import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.visitors.helpers.SymbolSetIndexer;

import java.util.Map;
import java.util.Random;

public class GenerationVisitor implements NodeVisitor {

    public static GenerationVisitorBuilder builder() {
        return new GenerationVisitorBuilder(true);
    }

    protected final StringBuilder        aStringBuilder = new StringBuilder();
    protected final Map<Integer, String> aGroupValues;
    protected final Random               aRandom;
    protected final RgxGenProperties     aProperties;

    protected GenerationVisitor(Random random, Map<Integer, String> groupValues, RgxGenProperties properties) {
        aRandom = random;
        aGroupValues = groupValues;
        aProperties = properties;
    }

    @Override
    public void visit(SymbolSet node) {
        SymbolSetIndexer indexer = node.getSymbolSetIndexer();
        int idx = aRandom.nextInt(indexer.size());
        aStringBuilder.append(indexer.get(idx));
    }

    @Override
    public void visit(Choice node) {
        Node[] nodes = node.getNodes();
        int idx = aRandom.nextInt(nodes.length);
        nodes[idx].visit(this);
    }

    @Override
    public void visit(FinalSymbol node) {
        aStringBuilder.append(node.getValue());
    }

    @Override
    public void visit(Repeat node) {
        int max = node.getMax() == -1 ? RgxGenOption.INFINITE_PATTERN_REPETITION.getIntFromProperties(aProperties) : node.getMax();
        int repeat = node.getMin() >= max ?
                     node.getMin() :
                     node.getMin() + aRandom.nextInt(max + 1 - node.getMin());

        for (int i = 0; i < repeat; ++i) {
            node.getNode()
                .visit(this);
        }
    }

    @Override
    public void visit(Sequence node) {
        for (Node n : node.getNodes()) {
            n.visit(this);
        }
    }

    @Override
    public void visit(NotSymbol node) {
        GenerationVisitor nmgv = new NotMatchingGenerationVisitor(aRandom, aGroupValues, aProperties);
        node.getNode()
            .visit(nmgv);
        aStringBuilder.append(nmgv.aStringBuilder);
    }

    @Override
    public void visit(GroupRef node) {
        aStringBuilder.append(aGroupValues.getOrDefault(node.getIndex(), ""));
    }

    @Override
    public void visit(Group node) {
        int start = aStringBuilder.length();
        node.getNode()
            .visit(this);
        aGroupValues.put(node.getIndex(), aStringBuilder.substring(start));
    }

    public String getString() {
        return aStringBuilder.toString();
    }
}
