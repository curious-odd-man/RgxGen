package org.curious.regex.generator.visitors;

import org.curious.regex.generator.nodes.*;

import java.util.concurrent.ThreadLocalRandom;

public class GenerationVisitor implements NodeVisitor {
    private StringBuilder aStringBuilder = new StringBuilder();

    @Override
    public void visit(AnySymbol node) {
        String[] allSymbols = AnySymbol.ALL_SYMBOLS;
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
        long repeat = node.getMin() == node.getMax() ?
                      node.getMin() :
                      ThreadLocalRandom.current()
                                       .nextLong(node.getMin(), node.getMax());

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
