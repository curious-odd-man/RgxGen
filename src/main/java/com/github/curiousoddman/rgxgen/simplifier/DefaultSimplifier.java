package com.github.curiousoddman.rgxgen.simplifier;

import com.github.curiousoddman.rgxgen.generator.nodes.Node;
import com.github.curiousoddman.rgxgen.generator.visitors.SimplificationVisitor;

public class DefaultSimplifier implements Simplifier {
    @Override
    public Node simplify(Node input) {
        SimplificationVisitor visitor = new SimplificationVisitor();
        input.visit(visitor);
        return visitor.getSimplifiedNode();
    }
}
