package org.curious.regex.generator.visitors;


import org.curious.regex.generator.nodes.*;

import java.math.BigInteger;

public class UniqueValuesCountingVisitor implements NodeVisitor {
    private BigInteger aCount = BigInteger.ZERO;

    @Override
    public void visit(AnySymbol node) {
        aCount = aCount.add(BigInteger.valueOf(AnySymbol.ALL_SYMBOLS.length));
    }

    @Override
    public void visit(Choice node) {
        for (Node n : node.getNodes()) {
            n.visit(this);
        }
    }

    @Override
    public void visit(FinalSymbol node) {
        aCount = aCount.add(BigInteger.ONE);
    }

    @Override
    public void visit(Repeat node) {
        UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor();
        node.getNode()
            .visit(v);

        if (node.getMin() == 0) {
            aCount = aCount.add(v.aCount.pow((int) (node.getMax() + 1)));
        } else {
            aCount = aCount.add(v.aCount.pow((int) (node.getMax() - node.getMin())));
        }
    }

    @Override
    public void visit(Sequence node) {
        for (Node vnode : node.getNodes()) {
            UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor();
            vnode.visit(v);
            aCount = aCount.multiply(v.aCount);
        }
    }

    public BigInteger getCount() {
        return aCount;
    }
}
