package org.curious.regex.generator.visitors;

import org.curious.regex.generator.nodes.*;

public interface NodeVisitor {
    void visit(AnySymbol node);

    void visit(Choice node);

    void visit(FinalSymbol node);

    void visit(Repeat node);

    void visit(Sequence node);
}
