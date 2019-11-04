package org.curious.regex.generator.nodes;

import org.curious.regex.generator.visitors.NodeVisitor;

public interface Node {
    void visit(NodeVisitor visitor);
}
