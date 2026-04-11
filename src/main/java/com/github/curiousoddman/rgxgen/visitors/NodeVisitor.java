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

/**
 * Visitor interface for traversing and processing different types of syntax tree nodes.
 * <p>
 * Implementations of this interface define operations to be performed on each specific
 * node type in the structure. This follows the Visitor design pattern, allowing new
 * operations to be added without modifying the node classes.
 */
public interface NodeVisitor {

    /**
     * Visits a {@link SymbolSet} node.
     *
     * @param node the {@link SymbolSet} node to visit
     */
    void visit(SymbolSet node);

    /**
     * Visits a {@link Choice} node.
     *
     * @param node the {@link Choice} node to visit
     */
    void visit(Choice node);

    /**
     * Visits a {@link FinalSymbol} node.
     *
     * @param node the {@link FinalSymbol} node to visit
     */
    void visit(FinalSymbol node);

    /**
     * Visits a {@link Repeat} node.
     *
     * @param node the {@link Repeat} node to visit
     */
    void visit(Repeat node);

    /**
     * Visits a {@link Sequence} node.
     *
     * @param node the {@link Sequence} node to visit
     */
    void visit(Sequence node);

    /**
     * Visits a {@link NotSymbol} node.
     *
     * @param node the {@link NotSymbol} node to visit
     */
    void visit(NotSymbol node);

    /**
     * Visits a {@link GroupRef} node.
     *
     * @param node the {@link GroupRef} node to visit
     */
    void visit(GroupRef node);

    /**
     * Visits a {@link Group} node.
     *
     * @param node the {@link Group} node to visit
     */
    void visit(Group node);
}
