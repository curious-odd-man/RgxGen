package com.github.curiousoddman.rgxgen.optimizer;

import com.github.curiousoddman.rgxgen.generator.nodes.Node;

public class DefaultOptimizer implements Optimizer {
    @Override
    public Node optimize(Node input) {
        return input;
    }
}
