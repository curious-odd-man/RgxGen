package com.github.curiousoddman.rgxgen.generator.visitors;

import com.github.curiousoddman.rgxgen.generator.nodes.*;

import java.util.concurrent.ThreadLocalRandom;

public class GenerationVisitor implements NodeVisitor {
    private StringBuilder aStringBuilder = new StringBuilder();

    @Override
    public void visit(SymbolRange node) {
        String[] allSymbols = node.getSymbols();
        int idx = ThreadLocalRandom.current()
                                   .nextInt(allSymbols.length);
        aStringBuilder.append(allSymbols[idx]);
    }

    @Override
    public void visit(Choice node) {
        Node[] nodes = node.getNodes();
        int idx = ThreadLocalRandom.current()
                                   .nextInt(nodes.length);
        nodes[idx].visit(this);
    }

    @Override
    public void visit(FinalSymbol node) {
        aStringBuilder.append(node.getValue());
    }

    @Override
    public void visit(Repeat node) {
        int max = node.getMax() == -1 ? 100 : node.getMax();
        int repeat = node.getMin() >= max ?
                     node.getMin() :
                     ThreadLocalRandom.current()
                                      .nextInt(node.getMin(), max);

        for (long i = 0; i < repeat; ++i) {
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

    public String getString() {
        return aStringBuilder.toString();
    }
}
