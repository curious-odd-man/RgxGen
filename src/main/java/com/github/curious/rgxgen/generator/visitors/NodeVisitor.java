package com.github.curious.rgxgen.generator.visitors;

import com.github.curious.rgxgen.generator.nodes.*;

public interface NodeVisitor {
    void visit(AnySymbol node);

    void visit(Choice node);

    void visit(FinalSymbol node);

    void visit(Repeat node);

    void visit(Sequence node);
}
