package org.curious.regex.generator.nodes;

import org.curious.regex.generator.visitors.NodeVisitor;

import java.util.Arrays;

public class Sequence implements Node {
    private final Node[] aNodes;

    public Sequence(Node... nodes) {
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
        return "Sequence{" +
                Arrays.toString(aNodes) +
                '}';
    }
}
