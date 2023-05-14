package com.github.curiousoddman.rgxgen.testutil;

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

public class NodePatternCollectingVisitor implements NodeVisitor {
    private final List<String> aPatterns = new ArrayList<>();

    @Override
    public void visit(AsciiSymbolSet node) {
        aPatterns.add(node.getPattern());
    }

    @Override
    public void visit(Choice node) {
        aPatterns.add(node.getPattern());
        for (Node nodeNode : node.getNodes()) {
            nodeNode.visit(this);
        }
    }

    @Override
    public void visit(FinalSymbol node) {
        aPatterns.add(node.getPattern());
    }

    @Override
    public void visit(Repeat node) {
        aPatterns.add(node.getPattern());
        node.getNode()
            .visit(this);
    }

    @Override
    public void visit(Sequence node) {
        aPatterns.add(node.getPattern());
        for (Node nodeNode : node.getNodes()) {
            nodeNode.visit(this);
        }
    }

    @Override
    public void visit(NotSymbol node) {
        aPatterns.add(node.getPattern());
    }

    @Override
    public void visit(GroupRef node) {
        aPatterns.add(node.getPattern());
    }

    @Override
    public void visit(Group node) {
        aPatterns.add(node.getPattern());
        node.getNode()
            .visit(this);
    }

    public List<String> getPatterns() {
        return aPatterns;
    }
}
