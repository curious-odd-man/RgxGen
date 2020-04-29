package com.github.curiousoddman.rgxgen.simplifier;

import com.github.curiousoddman.rgxgen.generator.nodes.Node;

public interface Simplifier {

    Node simplify(Node input);
}
