package com.github.curiousoddman.rgxgen.generator.visitors;


import com.github.curiousoddman.rgxgen.generator.nodes.*;

import java.math.BigInteger;
import java.util.function.Function;

public class UniqueValuesCountingVisitor implements NodeVisitor {
    private BigInteger aCount = BigInteger.ZERO;

    private void applyOrSkip(Function<BigInteger, BigInteger> func) {
        if (aCount != null) {
            aCount = func.apply(aCount);
        }
    }

    @Override
    public void visit(SymbolSet node) {
        applyOrSkip(v -> v.add(BigInteger.valueOf(node.getSymbols().length)));
    }

    @Override
    public void visit(Choice node) {
        for (Node n : node.getNodes()) {
            // Just sum up all the choices
            n.visit(this);
        }
    }

    @Override
    public void visit(FinalSymbol node) {
        applyOrSkip(v -> v.add(BigInteger.ONE));
    }

    @Override
    public void visit(Repeat node) {
        UniqueValuesCountingVisitor countingVisitor = new UniqueValuesCountingVisitor();
        node.getNode()
            .visit(countingVisitor);

        if (node.getMax() < 0 || countingVisitor.aCount == null) {
            aCount = null;
        } else if (aCount != null) {
            for (int i = node.getMin(); i <= node.getMax(); i++) {
                aCount = aCount.add(countingVisitor.aCount.pow(i));
            }
        }
    }

    @Override
    public void visit(Sequence node) {
        for (Node vnode : node.getNodes()) {
            UniqueValuesCountingVisitor countingVisitor = new UniqueValuesCountingVisitor();
            vnode.visit(countingVisitor);
            applyOrSkip(v -> {
                if (countingVisitor.aCount == null) {
                    return null;
                }
                return v.equals(BigInteger.ZERO) ? countingVisitor.aCount : v.multiply(countingVisitor.aCount);
            });
        }
    }

    @Override
    public void visit(NotSymbol notSymbol) {
        aCount = null;
    }

    @Override
    public void visit(GroupRef groupRef) {
        // FIXME:
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void visit(Group group) {
        group.getNode()
             .visit(this);
    }

    /**
     * Count of unique values that can be generated with this regex. {@code null} if infinite
     *
     * @return unique values estimation or null, if infinite
     */
    public BigInteger getCount() {
        return aCount;
    }
}
