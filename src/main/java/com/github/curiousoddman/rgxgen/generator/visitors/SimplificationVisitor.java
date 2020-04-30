package com.github.curiousoddman.rgxgen.generator.visitors;

import com.github.curiousoddman.rgxgen.generator.nodes.*;

public class SimplificationVisitor implements NodeVisitor {
    @Override
    public void visit(SymbolSet node) {

    }

    @Override
    public void visit(Choice node) {

    }

    @Override
    public void visit(FinalSymbol node) {

    }

    @Override
    public void visit(Repeat node) {

    }

    @Override
    public void visit(Sequence node) {
        // Empty Sequence -> PatternDoesntMatchAnyStringException
        // LineStart or LineEnd in the middle -> PatternDoesntMatchAnyStringException
        // LineStart at first position or LineEnd at last position -> Remove, but depends on outside
        // Sequence with only one child -> unwrap child
        // PatternDoesntMatchAnyStringException from any node -> PatternDoesntMatchAnyStringException
    }

    @Override
    public void visit(NotSymbol notSymbol) {

    }

    @Override
    public void visit(GroupRef groupRef) {

    }

    @Override
    public void visit(Group group) {

    }

    @Override
    public void visit(LineStart lineStart) {

    }

    @Override
    public void visit(LineEnd lineEnd) {

    }

    public Node getSimplifiedNode() {
        return null;
    }
}
