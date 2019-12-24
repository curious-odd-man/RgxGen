package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Group implements Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(Group.class);

    private final Node    aNode;
    private final int     aGroupIndex;
    private final boolean aIsCapturable;

    public Group(Node node) {
        this(-1, node);
    }

    public Group(int index, Node node) {
        LOGGER.trace("Crating idx = '{}' from '{}'", index, node);
        aNode = node;
        aGroupIndex = index;
        aIsCapturable = index != -1;
    }

    public boolean isCapture() {
        return aIsCapturable;
    }

    public int getIndex() {
        return aGroupIndex;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Node getNode() {
        return aNode;
    }

    @Override
    public String toString() {
        return "Group[" + aGroupIndex +
                "]{" + aNode +
                '}';
    }
}
