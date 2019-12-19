package com.github.curiousoddman.rgxgen.generator.visitors;

import com.github.curiousoddman.rgxgen.generator.nodes.*;

public interface NodeVisitor {
    void visit(SymbolSet node);

    void visit(Choice node);

    void visit(FinalSymbol node);

    void visit(Repeat node);

    void visit(Sequence node);

    void visit(NotSymbol notSymbol);

    void visit(GroupRef groupRef);

    void visit(Group group);
}
