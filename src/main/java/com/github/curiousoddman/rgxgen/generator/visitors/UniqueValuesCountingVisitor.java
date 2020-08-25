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
        for (Node vnode : node.getNodes()) {
            BigInteger count = countSeparately(node, vnode);
            applyOrSkip(v -> {
                if (count == null) {
                    return null;
                }

                return v.add(count);
            });
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
            BigInteger count = countSeparately(node, vnode);
            applyOrSkip(v -> {
                if (count == null) {
                    return null;
                }

                if (v.equals(BigInteger.ZERO)) {
                    return count;
                }

                return count.equals(BigInteger.ZERO) ? v : v.multiply(count);
            });
        }
    }

    private BigInteger countSeparately(Node parentNode, Node vnode) {
        UniqueValuesCountingVisitor countingVisitor = new UniqueValuesCountingVisitor(parentNode);
        vnode.visit(countingVisitor);
        return countingVisitor.aCount;
    }

    @Override
    public void visit(NotSymbol node) {
        aCount = null;
    }

    @Override
    public void visit(GroupRef groupRef) {
        if (aParentNode != null
                && (aParentNode instanceof Repeat || aParentNode instanceof Choice)
        ) {
            // When repeated multiple times - it adds as much unique values as it is repeated. So we should add 1 (it will be used in Repeat for calculation).
            // E.g. (a|b)\1{2,3} - captured value of group is repeated either 2 or 3 times - it gives 2 unique values.
            aCount = aCount.add(BigInteger.ONE);
        }
        //else
        // Do nothing. It does not add new unique values apart from above mentioned cases
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
