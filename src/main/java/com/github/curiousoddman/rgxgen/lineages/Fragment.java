package com.github.curiousoddman.rgxgen.lineages;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Intermediate result produced by {@link PathGraphBuilder} while compiling one AST node.
 * <p>
 * A Fragment is an open sub-graph: it has a set of entry points (where control flows in)
 * and a set of exit points (where control can flow out), plus the edges that were
 * created internally while compiling that sub-tree.
 * <p>
 * The parent compilation step connects the exits of one Fragment to the entries of the
 * next one, progressively assembling the full graph.
 *
 * @param entries       Nodes where control can enter this sub-graph.
 * @param exits         Nodes from which control can leave this sub-graph.
 * @param internalEdges Edges created inside this sub-graph (not the connecting edges).
 */
public record Fragment(List<PathNode> entries, List<PathNode> exits, List<PathEdge> internalEdges) {

    public Fragment(List<PathNode> entries, List<PathNode> exits, List<PathEdge> internalEdges) {
        this.entries = List.copyOf(entries);
        this.exits = List.copyOf(exits);
        this.internalEdges = new ArrayList<>(internalEdges);
    }

    /**
     * Fragment for a single opaque node: one entry, one exit, no internal edges.
     */
    public static Fragment terminal(PathNode node) {
        List<PathNode> single = Collections.singletonList(node);
        return new Fragment(single, single, Collections.emptyList());
    }
}
