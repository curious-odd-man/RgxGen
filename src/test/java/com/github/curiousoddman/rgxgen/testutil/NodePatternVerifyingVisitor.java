package com.github.curiousoddman.rgxgen.testutil;

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NodePatternVerifyingVisitor implements NodeVisitor {
    private final List<String> aPatterns;
    private final List<String> aErrors;

    private int aCurrentIndex = -1;

    public NodePatternVerifyingVisitor(Node node) {
        NodePatternCollectingVisitor visitor = new NodePatternCollectingVisitor();
        node.visit(visitor);
        aErrors = new ArrayList<>();
        aPatterns = visitor.getPatterns();
    }

    @Override
    public void visit(SymbolSet node) {
        compareAndReport(node);
    }

    @Override
    public void visit(Choice node) {
        compareAndReport(node);
    }

    @Override
    public void visit(FinalSymbol node) {
        compareAndReport(node);
    }

    @Override
    public void visit(Repeat node) {
        compareAndReport(node);
    }

    @Override
    public void visit(Sequence node) {
        compareAndReport(node);
    }

    @Override
    public void visit(NotSymbol node) {
        compareAndReport(node);
    }

    @Override
    public void visit(GroupRef node) {
        compareAndReport(node);
    }

    @Override
    public void visit(Group node) {
        compareAndReport(node);
    }

    private void compareAndReport(Node node) {
        if (!node.getPattern()
                 .equals(aPatterns.get(++aCurrentIndex))) {
            aErrors.add("'" + node.getPattern() + "' not equal to '" + aPatterns.get(aCurrentIndex) + "'");
        }
    }

    public Collection<String> getErrors() {
        return aErrors;
    }
}
