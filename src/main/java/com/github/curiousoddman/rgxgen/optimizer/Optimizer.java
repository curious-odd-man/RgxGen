package com.github.curiousoddman.rgxgen.optimizer;

import com.github.curiousoddman.rgxgen.generator.nodes.Node;

public interface Optimizer {

    Node optimize(Node input);
}
