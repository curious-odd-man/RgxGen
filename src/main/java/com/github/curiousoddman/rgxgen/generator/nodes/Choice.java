package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Choice implements Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(Choice.class);

    private final Node[] aNodes;

    public Choice(Node... nodes) {
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
        return "Choice" + Arrays.toString(aNodes);
    }
}
