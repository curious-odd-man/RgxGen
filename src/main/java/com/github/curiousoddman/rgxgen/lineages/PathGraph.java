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
 * The assembled path graph: a set of {@link PathNode}s, directed {@link PathEdge}s,
 * and (optionally) a hierarchy of {@link PathGraphCluster}s that group nodes by their
 * originating AST compound node.
 *
 * <h2>PlantUML output</h2>
 * {@link #toPlantUml()} emits a {@code @startuml} / {@code @enduml} block using
 * PlantUML's <em>nwdiag / component</em> syntax:
 * <ul>
 *   <li>Each {@link PathNode} becomes a component named {@code node_N}.</li>
 *   <li>Each {@link PathEdge} becomes an arrow {@code node_A --> node_B : label}.</li>
 *   <li>Each {@link PathGraphCluster} becomes a nested {@code rectangle} block that
 *       wraps its member nodes and nested sub-clusters.</li>
 * </ul>
 *
 * <p>Nodes that do not belong to any cluster (BEGIN, END sentinels) are rendered at the
 * top level, outside all {@code rectangle} blocks.
 */
public class PathGraph {
    private final String pattern;
    private final List<PathNode> nodes = new ArrayList<>();
    private final List<PathEdge> edges = new ArrayList<>();
    /**
     * Root-level clusters (one per top-level compound AST node).
     */
    private final List<PathGraphCluster> rootClusters = new ArrayList<>();

    public PathGraph(String pattern) {
        this.pattern = pattern;
    }

    // -------------------------------------------------------------------------
    // Mutation API (called by PathGraphBuilder)
    // -------------------------------------------------------------------------

    public void addNode(PathNode node) {
        nodes.add(node);
    }

    public void addEdge(PathEdge edge) {
        edges.add(edge);
    }

    /**
     * Registers a top-level (root) cluster produced by the builder.
     */
    public void addRootCluster(PathGraphCluster cluster) {
        rootClusters.add(cluster);
    }

    // -------------------------------------------------------------------------
    // Query API
    // -------------------------------------------------------------------------

    public List<PathNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public List<PathEdge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    // -------------------------------------------------------------------------
    // PlantUML rendering
    // -------------------------------------------------------------------------

    /**
     * Returns the full PlantUML diagram as a string.
     *
     * <p>The layout strategy:
     * <ol>
     *   <li>Collect all node IDs that are claimed by at least one cluster.
     *       These will be rendered <em>inside</em> their cluster rectangle.</li>
     *   <li>Render root-level clusters (which recursively render their children).</li>
     *   <li>Render unclaimed nodes (BEGIN / END sentinels, and any nodes that
     *       fall outside all compound scopes) at the top level.</li>
     *   <li>Render all edges.</li>
     * </ol>
     */
    public String toPlantUml() {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");
        sb.append("title\n\"\"Pattern: `").append(pattern).append("`\"\"\nend title\n\n");
        sb.append("skinparam rectangle {\n");
        sb.append("  BorderColor #888888\n");
        sb.append("  BackgroundColor #F8F8FF\n");
        sb.append("  FontStyle bold\n");
        sb.append("}\n\n");

        // Collect all node IDs claimed by any cluster (direct members across the tree)
        Set<String> clusteredIds = new HashSet<>();
        for (PathGraphCluster root : rootClusters) {
            collectClusteredIds(root, clusteredIds);
        }

        // 1. Render node declarations for unclaimed nodes (BEGIN / END, etc.)
        for (PathNode node : nodes) {
            if (!clusteredIds.contains(node.getId())) {
                sb.append(nodeDeclaration(node)).append('\n');
            }
        }

        if (!nodes.isEmpty() && !rootClusters.isEmpty()) {
            sb.append('\n');
        }

        // 2. Render root clusters (recursively renders child clusters + direct nodes)
        for (PathGraphCluster cluster : rootClusters) {
            sb.append(renderCluster(cluster, ""));
        }

        // 3. Render edges
        if (!edges.isEmpty()) {
            sb.append('\n');
        }
        for (PathEdge edge : edges) {
            sb.append(edgeDeclaration(edge)).append('\n');
        }

        sb.append("}\n");
        sb.append("@enduml\n");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Private rendering helpers
    // -------------------------------------------------------------------------

    /**
     * Renders a cluster and all its contents (nested clusters first, then direct
     * node declarations) as a {@code rectangle} block.
     */
    private String renderCluster(PathGraphCluster cluster, String indent) {
        StringBuilder sb = new StringBuilder();
        String escapedLabel = cluster.getLabel().replace("\"", "\\\"");
        sb.append(indent).append("rectangle \"").append(escapedLabel).append("\" {\n");

        String inner = indent + "  ";

        // Nested clusters first (depth-first, preserving left-to-right AST order)
        for (PathGraphCluster child : cluster.getChildren()) {
            sb.append(renderCluster(child, inner));
        }

        // Direct member node declarations
        for (String nodeId : cluster.getDirectNodeIds()) {
            PathNode node = findNodeById(nodeId);
            if (node != null) {
                sb.append(inner).append(nodeDeclaration(node)).append('\n');
            }
        }

        sb.append(indent).append("}\n");
        return sb.toString();
    }

    /**
     * Returns the single-line PlantUML declaration for a node.
     * Uses a {@code component} keyword so nodes get a distinct visual style.
     * The alias {@code node_N} is used as the stable identifier in edge lines.
     */
    private static String nodeDeclaration(PathNode node) {
        String label = escapePlantUml(node.getLabel());
        String alias = "node_" + node.getId();
        String stereotype = stereotypeFor(node);
        // component "label" as node_N <<stereotype>>
        return "component \"\"\"" + label + "\"\"\" as " + alias + stereotype;
    }

    private static String stereotypeFor(PathNode node) {
        return switch (node.getKind()) {
            case CHOICE -> " <<choice>>";
            case REPEAT_ENTRY -> " <<repeat>>";
            default -> "";
        };
    }

    /**
     * Returns the PlantUML arrow line for an edge.
     * Directed arrow with an optional label showing repetition bounds.
     */
    private static String edgeDeclaration(PathEdge edge) {
        String from = "node_" + edge.from().getId();
        String to = "node_" + edge.to().getId();
        String label = edge.label();
        if (label.isEmpty()) {
            return from + " --> " + to;
        }
        return from + " --> " + to + " : " + label;
    }


    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Recursively collects all PathNode IDs that are direct members of any cluster.
     */
    private static void collectClusteredIds(PathGraphCluster cluster, Set<String> ids) {
        ids.addAll(cluster.getDirectNodeIds());
        for (PathGraphCluster child : cluster.getChildren()) {
            collectClusteredIds(child, ids);
        }
    }

    private PathNode findNodeById(String id) {
        for (PathNode n : nodes) {
            if (n.getId().equals(id)) {
                return n;
            }
        }
        return null;
    }

    private static String escapePlantUml(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}