package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Sequence implements Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sequence.class);

    private final Node[] aNodes;

    public Sequence(Node... nodes) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Creating from {} ", Arrays.asList(nodes));
        }
        aNodes = nodes;
    }

    public Node[] getNodes() {
        return aNodes;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Sequence" + Arrays.toString(aNodes);
    }
}
