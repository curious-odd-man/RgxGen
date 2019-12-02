package com.github.curiousoddman.rgxgen.generator.visitors;

import com.github.curiousoddman.rgxgen.generator.nodes.*;

public interface NodeVisitor {
    void visit(SymbolRange node);

    void visit(Choice node);

    void visit(FinalSymbol node);

    void visit(Repeat node);

    void visit(Sequence node);
}
