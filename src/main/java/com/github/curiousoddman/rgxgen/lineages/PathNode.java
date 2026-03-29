package com.github.curiousoddman.rgxgen.lineages;

import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.nodes.Repeat;
import com.github.curiousoddman.rgxgen.util.Util;

/**
 * A vertex in the path graph.
 * <p>
 * Every AST {@link Node} that participates in the graph gets its own {@code PathNode}.
 * Synthetic nodes (BEGIN, END, REPEAT_ENTRY, CHOICE) also use this class with a
 * {@code null} {@code astNode}.
 */
public class PathNode {

    /**
     * Discriminates between real AST nodes and the synthetic kinds.
     */
    public enum Kind {
        /**
         * Wraps a real AST leaf or compound node.
         */
        AST,
        /**
         * Synthetic graph entry sentinel.
         */
        BEGIN,
        /**
         * Synthetic graph exit sentinel.
         */
        END,
        /**
         * Synthetic node that marks the re-entry point of a repetition loop.
         * One per {@code Repeat} AST node.
         */
        REPEAT_ENTRY,
        /**
         * Synthetic dispatcher node for a {@code Choice} AST node.
         * Control flows in here then fans out to each alternative.
         */
        CHOICE
    }

    private final String id;
    private final Kind kind;
    private final Node astNode;   // null for synthetic nodes
    private final String label;

    // -------------------------------------------------------------------------
    // Private constructor – use factories below
    // -------------------------------------------------------------------------

    /**
     * @param sequenceNumber a monotonic counter supplied by the caller (e.g. PathGraphBuilder)
     *                       so that IDs are unique within one graph but reset between graphs.
     */
    private PathNode(Kind kind, Node astNode, String label, int sequenceNumber) {
        this.id = kind.name() + "_" + sequenceNumber;
        this.kind = kind;
        this.astNode = astNode;
        this.label = label;
    }

    // -------------------------------------------------------------------------
    // Factories
    // -------------------------------------------------------------------------

    public static PathNode forAst(Node astNode, int seq) {
        return new PathNode(Kind.AST, astNode,
                astNode.getClass().getSimpleName() + "(" + Util.plantumlEscape(astNode.getPattern()) + ")", seq);
    }

    public static PathNode begin(int seq) {
        return new PathNode(Kind.BEGIN, null, "BEGIN", seq);
    }

    public static PathNode end(int seq) {
        return new PathNode(Kind.END, null, "END", seq);
    }

    /**
     * Creates the synthetic re-entry node for a {@code Repeat} AST node.
     */
    public static PathNode repeatEntry(com.github.curiousoddman.rgxgen.nodes.Repeat repeat, int seq) {
        return new PathNode(Kind.REPEAT_ENTRY, null,
                getRepeatLabel(repeat), seq);
    }

    private static String getRepeatLabel(Repeat repeat) {
        return "Repeat(" + Util.plantumlEscape(repeat.getPattern()) + ")";
    }

    /**
     * Creates the synthetic dispatcher node for a {@code Choice} AST node.
     */
    public static PathNode choice(com.github.curiousoddman.rgxgen.nodes.Choice choice, int seq) {
        return new PathNode(Kind.CHOICE, null, "Choice(" + Util.plantumlEscape(choice.getPattern()) + ")", seq);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public Kind getKind() {
        return kind;
    }

    /**
     * The wrapped AST node; {@code null} for synthetic nodes.
     */
    public Node getAstNode() {
        return astNode;
    }

    public String getLabel() {
        return label;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return id + "[" + label + "]";
    }
}
