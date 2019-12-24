package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repeat implements Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(Repeat.class);

    private final Node aNode;
    private final int  aMin;
    private final int  aMax;

    public static Repeat minimum(Node node, int times) {
        return new Repeat(node, times, -1);
    }

    public Repeat(Node node, int times) {
        this(node, times, times);
    }

    public Repeat(Node node, int min, int max) {
        LOGGER.trace("Crating ({} to {}) '{}'", min, max, node);
        aNode = node;
        aMin = min;
        aMax = max;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Node getNode() {
        return aNode;
    }

    public int getMin() {
        return aMin;
    }

    public int getMax() {
        return aMax;
    }

    @Override
    public String toString() {
        return "Repeat{" + aNode +
                ", aMin=" + aMin +
                ", aMax=" + aMax +
                '}';
    }
}
