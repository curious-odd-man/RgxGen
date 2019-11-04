package org.curious.regex.generator.nodes;

import org.curious.regex.generator.visitors.NodeVisitor;

import java.util.Arrays;

public class Choice implements Node {

    private final Node[] aNodes;

    public Choice(Node... nodes) {
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
        return "Choice{"
                + Arrays.toString(aNodes) +
                '}';
    }
}
