package com.github.curiousoddman.rgxgen.lineages;

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
 */
public class Fragment {

    /**
     * Nodes where control can enter this sub-graph.
     */
    private final List<PathNode> entries;

    /**
     * Nodes from which control can leave this sub-graph.
     */
    private final List<PathNode> exits;

    /**
     * Edges created inside this sub-graph (not the connecting edges).
     */
    private final List<PathEdge> internalEdges;

    public Fragment(List<PathNode> entries, List<PathNode> exits, List<PathEdge> internalEdges) {
        this.entries = List.copyOf(entries);
        this.exits = List.copyOf(exits);
        this.internalEdges = new ArrayList<>(internalEdges);
    }

    // -------------------------------------------------------------------------
    // Simple (single entry / single exit) factory
    // -------------------------------------------------------------------------

    /**
     * Fragment for a single opaque node: one entry, one exit, no internal edges.
     */
    public static Fragment terminal(PathNode node) {
        List<PathNode> single = Collections.singletonList(node);
        return new Fragment(single, single, Collections.emptyList());
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public List<PathNode> getEntries() {
        return entries;
    }

    public List<PathNode> getExits() {
        return exits;
    }

    public List<PathEdge> getInternalEdges() {
        return internalEdges;
    }
}
