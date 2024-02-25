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
import com.github.curiousoddman.rgxgen.util.Util;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Function;

public class UniqueValuesCountingVisitor implements NodeVisitor {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // This value is returned to user later
    private Optional<BigInteger> aCount = Optional.of(BigInteger.ZERO);

    private final Node             aParentNode;
    private final RgxGenProperties aProperties;

    public UniqueValuesCountingVisitor(RgxGenProperties properties) {
        this(null, properties);
    }

    public UniqueValuesCountingVisitor(Node parentNode, RgxGenProperties properties) {
        aParentNode = parentNode;
        aProperties = properties;
    }

    private void applyOrSkip(Function<BigInteger, Optional<BigInteger>> func) {
        aCount = aCount.flatMap(func);
    }

    @Override
    public void visit(SymbolSet node) {
        applyOrSkip(v -> {
            int size = RgxGenOption.CASE_INSENSITIVE.getFromProperties(aProperties)
                       ? node.getCaseInsensitiveSymbolSetIndexer().size()
                       : node.getSymbolSetIndexer().size();
            return Optional.of(v.add(BigInteger.valueOf(size)));
        });
    }

    @Override
    public void visit(Choice node) {
        for (Node child_node : node.getNodes()) {
            applyOrSkip(v -> countSeparately(node, child_node, aProperties).map(v::add));
        }
    }

    @Override
    public void visit(FinalSymbol node) {
        if (RgxGenOption.CASE_INSENSITIVE.getFromProperties(aProperties)) {
            applyOrSkip(v -> Optional.of(v.add(Util.countCaseInsensitiveVariations(node.getValue()))));
        } else {
            applyOrSkip(v -> Optional.of(v.add(BigInteger.ONE)));
        }
    }

    @Override
    public void visit(Repeat node) {
        if (aCount.isPresent()) {
            UniqueValuesCountingVisitor countingVisitor = new UniqueValuesCountingVisitor(node, aProperties);
            node.getNode()
                .visit(countingVisitor);

            if (node.getMax() < 0 || !countingVisitor.aCount.isPresent()) {
                aCount = Optional.empty();
            } else {
                BigInteger currentValue = aCount.get();
                BigInteger nodesValue = countingVisitor.aCount.get();
                for (int i = node.getMin(); i <= node.getMax(); i++) {
                    currentValue = currentValue.add(nodesValue.pow(i));
                }
                aCount = Optional.of(currentValue);
            }
        }
    }

    @Override
    public void visit(Sequence node) {
        if (aCount.isPresent()) {
            for (Node child_node : node.getNodes()) {
                Optional<BigInteger> count = countSeparately(node, child_node, aProperties);
                applyOrSkip(v -> {
                    if (!count.isPresent()) {
                        return Optional.empty();
                    }

                    if (v.equals(BigInteger.ZERO)) {
                        return count;
                    }

                    BigInteger subCount = count.get();
                    return Optional.of(subCount.equals(BigInteger.ZERO) ? v : v.multiply(subCount));
                });
            }
        }
    }

    private static Optional<BigInteger> countSeparately(Node parentNode, Node node, RgxGenProperties properties) {
        UniqueValuesCountingVisitor countingVisitor = new UniqueValuesCountingVisitor(parentNode, properties);
        node.visit(countingVisitor);
        return countingVisitor.aCount;
    }

    @Override
    public void visit(NotSymbol node) {
        aCount = Optional.empty();
    }

    @Override
    public void visit(GroupRef groupRef) {
        if (aParentNode != null
                && (aParentNode instanceof Repeat || aParentNode instanceof Choice)
        ) {
            // When repeated multiple times - it adds as much unique values as it is repeated. So we should add 1 (it will be used in Repeat for calculation).
            // E.g. (a|b)\1{2,3} - captured value of group is repeated either 2 or 3 times - it gives 2 unique values.
            aCount = aCount.map(v -> v.add(BigInteger.ONE));
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
     * Provides an estimation of number of unique values that can be generated using pattern.
     *
     * @return unique values estimation or empty, if infinite
     */
    public Optional<BigInteger> getEstimation() {
        return aCount;
    }
}
