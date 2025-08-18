package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RgxGenTest {

    @Test
    void canVisitWithCustomVisitorTest() {
        String pattern = "(a|b){1,2}\\1[a-z]";
        RgxGen rgxGen = RgxGen.parse(pattern);
        StringBuilder sb = new StringBuilder();
        rgxGen.visit(new NodeVisitor() {
            @Override
            public void visit(SymbolSet node) {
                sb.append("Visited SymbolSet: ").append(node.getPattern()).append('\n');
            }

            @Override
            public void visit(Choice node) {
                sb.append("Visited Choice: ").append(node.getPattern()).append('\n');
                for (Node nodeNode : node.getNodes()) {
                    nodeNode.visit(this);
                }
            }

            @Override
            public void visit(FinalSymbol node) {
                sb.append("Visited FinalSymbol: ").append(node.getPattern()).append('\n');
            }

            @Override
            public void visit(Repeat node) {
                sb.append("Visited Repeat: ").append(node.getPattern()).append('\n');
                node.getNode().visit(this);
            }

            @Override
            public void visit(Sequence node) {
                sb.append("Visited Sequence: ").append(node.getPattern()).append('\n');
                for (Node nodeNode : node.getNodes()) {
                    nodeNode.visit(this);
                }
            }

            @Override
            public void visit(NotSymbol node) {
                sb.append("Visited NotSymbol: ").append(node.getPattern()).append('\n');
            }

            @Override
            public void visit(GroupRef node) {
                sb.append("Visited GroupRef: ").append(node.getPattern()).append('\n');
            }

            @Override
            public void visit(Group node) {
                sb.append("Visited Group: ").append(node.getPattern()).append('\n');
                node.getNode().visit(this);
            }
        });

        assertEquals("""
                Visited Sequence: (a|b){1,2}\\1[a-z]
                Visited Repeat: (a|b){1,2}
                Visited Group: (a|b)
                Visited Choice: (a|b)
                Visited FinalSymbol: a
                Visited FinalSymbol: b
                Visited GroupRef: \\1
                Visited SymbolSet: [a-z]
                """, sb.toString());
    }
}