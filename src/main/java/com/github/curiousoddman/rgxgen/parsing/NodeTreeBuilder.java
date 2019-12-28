package com.github.curiousoddman.rgxgen.parsing;

import com.github.curiousoddman.rgxgen.generator.nodes.Node;

/**
 * Interface for the parser/nodes builder.
 */
public interface NodeTreeBuilder {

    /**
     * @return Root node for the parsed pattern
     */
    Node get();
}
