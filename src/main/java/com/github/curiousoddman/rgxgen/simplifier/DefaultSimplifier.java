package com.github.curiousoddman.rgxgen.simplifier;

import com.github.curiousoddman.rgxgen.generator.nodes.Node;

public class DefaultSimplifier implements Simplifier {
    @Override
    public Node simplify(Node input) {
        return input;
    }
}
