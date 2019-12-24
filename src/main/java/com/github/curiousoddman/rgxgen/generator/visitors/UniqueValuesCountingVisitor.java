package com.github.curiousoddman.rgxgen.generator.visitors;


import com.github.curiousoddman.rgxgen.generator.nodes.*;

import java.math.BigInteger;
import java.util.function.Function;

public class UniqueValuesCountingVisitor implements NodeVisitor {
    private BigInteger aCount = BigInteger.ZERO;

    private final Node aParentNode;

    public UniqueValuesCountingVisitor() {
        this(null);
    }

    public UniqueValuesCountingVisitor(Node parentNode) {
        aParentNode = parentNode;
    }

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
        UniqueValuesCountingVisitor countingVisitor = new UniqueValuesCountingVisitor(node);
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
            UniqueValuesCountingVisitor countingVisitor = new UniqueValuesCountingVisitor(node);
            vnode.visit(countingVisitor);
            applyOrSkip(v -> {
                if (countingVisitor.aCount == null) {
                    return null;
                }

                if (v.equals(BigInteger.ZERO)) {
                    return countingVisitor.aCount;
                }

                return countingVisitor.aCount.equals(BigInteger.ZERO) ? v : v.multiply(countingVisitor.aCount);
            });
        }
    }

    @Override
    public void visit(NotSymbol notSymbol) {
        aCount = null;
    }

    @Override
    public void visit(GroupRef groupRef) {
        if (aParentNode == null
                || !(aParentNode instanceof Repeat)) {
            // Do nothing. It does not add new unique values.
        } else {
            // When repeated multiple times - it adds as much unique values as it is repeated. So we should add 1 (it will be used in Repeat for calcuation).
            // E.g. (a|b)\1{2,3} - captured value of group is repeated either 2 or 3 times - it gives 2 unique values.
            aCount = aCount.add(BigInteger.ONE);
        }
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
