package com.github.curious.rgxgen.generator.nodes;

import com.github.curious.rgxgen.generator.visitors.NodeVisitor;

public interface Node {
    void visit(NodeVisitor visitor);
}
