package com.github.curiousoddman.rgxgen.lineages;

import java.util.*;

/**
 * The fully assembled path graph for one regex pattern.
 * <p>
 * Nodes are kept in insertion order so that serialization is deterministic.
 * Edges are stored in the order they were added, which naturally reflects
 * the structure of the AST.
 */
public class PathGraph {

    private final Set<PathNode> nodes = new LinkedHashSet<>();
    private final List<PathEdge> edges = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Mutation – called only by PathGraphBuilder
    // -------------------------------------------------------------------------

    void addNode(PathNode node) {
        nodes.add(node);
    }

    void addEdge(PathEdge edge) {
        nodes.add(edge.getFrom());
        nodes.add(edge.getTo());
        edges.add(edge);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public Set<PathNode> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    public List<PathEdge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    // -------------------------------------------------------------------------
    // PlantUML / DOT serialization
    // -------------------------------------------------------------------------

    /**
     * Emits a PlantUML {@code @startuml} / {@code @enduml} block that uses
     * Graphviz DOT syntax (via PlantUML's {@code digraph} support).
     *
     * <pre>
     * @startuml
     * digraph G {
     *   rankdir=LR
     *   BEGIN_0 [label="BEGIN" shape=circle]
     *   ...
     *   BEGIN_0 -> AST_1 [label="1..1"]
     *   ...
     * }
     * @enduml
     * </pre>
     */
    public String toPlantUml() {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");
        sb.append("digraph G {\n");
        sb.append("  rankdir=LR\n");
        sb.append("\n");

        // Node declarations
        for (PathNode node : nodes) {
            sb.append("  ")
                    .append(quote(node.getId()))
                    .append(" [label=")
                    .append(quote(node.getLabel()))
                    .append(shapeAttribute(node))
                    .append("]\n");
        }

        sb.append("\n");

        // Edge declarations
        for (PathEdge edge : edges) {
            sb.append("  ")
                    .append(quote(edge.getFrom().getId()))
                    .append(" -> ")
                    .append(quote(edge.getTo().getId()))
                    .append(" [label=")
                    .append(quote(edge.label()))
                    .append("]\n");
        }

        sb.append("}\n");
        sb.append("@enduml\n");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String shapeAttribute(PathNode node) {
        return switch (node.getKind()) {
            case BEGIN, END -> " shape=circle";
            case REPEAT_ENTRY -> " shape=diamond";
            case CHOICE -> " shape=trapezium";
            default -> " shape=box";
        };
    }

    private static String quote(String s) {
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }

    @Override
    public String toString() {
        return toPlantUml();
    }
}
