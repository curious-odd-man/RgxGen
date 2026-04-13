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

import java.util.*;

/**
 * A named cluster of {@link PathNode}s that should be rendered as a grouped
 * subgraph (PlantUML {@code rectangle}) in the path graph diagram.
 *
 * <p>Clusters are built by {@link PathGraphBuilder} during AST traversal:
 * every compound node (Choice, Repeat, Group, Sequence) that wraps children
 * produces one cluster whose membership is the union of all {@link PathNode}s
 * created for its subtree, plus any synthetic nodes (CHOICE, REPEAT_ENTRY)
 * introduced for it.
 *
 * <p>Clusters are <em>nested</em>: a cluster for a {@code Repeat} that wraps a
 * {@code Choice} will contain the Choice's cluster as a direct child.  The
 * by two spaces.
 */
public class PathGraphCluster {

    /**
     * Human-readable label shown in the diagram rectangle header.
     */
    private final String label;

    /**
     * PathNode IDs that belong <em>directly</em> to this cluster (not to a
     * nested child cluster).  Synthetic nodes (CHOICE, REPEAT_ENTRY) are
     * always direct members of their own cluster.
     */
    private final Set<String> directNodeIds = new LinkedHashSet<>();

    /**
     * Nested clusters, in the order they were added (= left-to-right AST order).
     */
    private final List<PathGraphCluster> children = new ArrayList<>();

    public PathGraphCluster(String label) {
        this.label = label;
    }

    // -------------------------------------------------------------------------
    // Builder API (called by PathGraphBuilder)
    // -------------------------------------------------------------------------

    /**
     * Registers a PathNode as a direct member of this cluster.
     */
    public void addDirectNode(String nodeId) {
        directNodeIds.add(nodeId);
    }

    /**
     * Attaches a nested child cluster.
     */
    public void addChild(PathGraphCluster child) {
        children.add(child);
    }

    // -------------------------------------------------------------------------
    // Query API
    // -------------------------------------------------------------------------

    /**
     * Returns the IDs of PathNodes that are direct members (not in sub-clusters).
     */
    public Set<String> getDirectNodeIds() {
        return Collections.unmodifiableSet(directNodeIds);
    }

    /**
     * Returns all nested child clusters.
     */
    public List<PathGraphCluster> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "Cluster[" + label + ", directNodes=" + directNodeIds + ", children=" + children.size() + "]";
    }
}
