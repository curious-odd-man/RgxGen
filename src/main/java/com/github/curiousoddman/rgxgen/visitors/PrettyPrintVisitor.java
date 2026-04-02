package com.github.curiousoddman.rgxgen.visitors;

/* **************************************************************************
   Copyright 2019 Vladislavs Varslavans

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
/* **************************************************************************/

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.nodes.type.ArrayChildNode;
import com.github.curiousoddman.rgxgen.nodes.type.SingleChildNode;

public class PrettyPrintVisitor implements NodeVisitor {
    private final StringBuilder sb = new StringBuilder();
    private int indentLevel = 0;
    private static final String INDENT = "  "; // 2 spaces

    public String getResult() {
        return sb.toString();
    }

    private void appendIndent() {
        sb.append(INDENT.repeat(Math.max(0, indentLevel)));
    }

    private void appendLine(Node node) {
        appendIndent();
        sb.append(node.toString()).append("\n");
    }

    private void visitChildren(Node node) {
        if (node instanceof ArrayChildNode arrayNode) {
            indentLevel++;
            for (Node child : arrayNode.getNodes()) {
                child.visit(this);
            }
            indentLevel--;
        } else if (node instanceof SingleChildNode singleNode) {
            indentLevel++;
            if (singleNode.getNode() != null) {
                singleNode.getNode().visit(this);
            }
            indentLevel--;
        }
    }

    @Override
    public void visit(SymbolSet node) {
        appendLine(node);
        visitChildren(node);
    }

    @Override
    public void visit(Choice node) {
        appendLine(node);
        visitChildren(node);
    }

    @Override
    public void visit(FinalSymbol node) {
        appendLine(node);
        visitChildren(node);
    }

    @Override
    public void visit(Repeat node) {
        appendLine(node);
        visitChildren(node);
    }

    @Override
    public void visit(Sequence node) {
        appendLine(node);
        visitChildren(node);
    }

    @Override
    public void visit(NotSymbol node) {
        appendLine(node);
        visitChildren(node);
    }

    @Override
    public void visit(GroupRef node) {
        appendLine(node);
        visitChildren(node);
    }

    @Override
    public void visit(Group node) {
        appendLine(node);
        visitChildren(node);
    }
}