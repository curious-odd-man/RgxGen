package com.github.curiousoddman.rgxgen.lineages;

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
 * {@link #toPlantUml(String)} method renders depth-first, indenting each level
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

    // -------------------------------------------------------------------------
    // PlantUML rendering
    // -------------------------------------------------------------------------

    /**
     * Renders this cluster (and its children recursively) as indented PlantUML
     * {@code rectangle} blocks.
     *
     * @param indent the leading whitespace for this level (e.g. {@code "  "})
     * @return the PlantUML snippet, ready to be embedded inside a {@code @startuml} block
     */
    public String toPlantUml(String indent) {
        StringBuilder sb = new StringBuilder();
        String escapedLabel = label.replace("\"", "\\\"");
        sb.append(indent).append("rectangle \"").append(escapedLabel).append("\" {\n");

        String inner = indent + "  ";

        // Render nested child clusters first (depth-first)
        for (PathGraphCluster child : children) {
            sb.append(child.toPlantUml(inner));
        }

        // Render direct member nodes as plain identifiers (PlantUML state/object refs)
        for (String nodeId : directNodeIds) {
            sb.append(inner).append("node_").append(nodeId).append('\n');
        }

        sb.append(indent).append("}\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Cluster[" + label + ", directNodes=" + directNodeIds + ", children=" + children.size() + "]";
    }
}
