package com.github.curiousoddman.rgxgen.lineages.optimization;

import com.github.curiousoddman.rgxgen.lineages.PathEdge;
import com.github.curiousoddman.rgxgen.lineages.PathGraph;
import com.github.curiousoddman.rgxgen.lineages.PathNode;
import com.github.curiousoddman.rgxgen.nodes.AnchorNode;

import java.util.*;

/**
 * Traverses a {@link PathGraph} and stamps every {@link PathNode} of kind
 * {@link PathNode.Kind#AST} with:
 * <ul>
 *   <li>a {@link NodePosition.First} mark – how often this node appears at the
 *       very front of a path (no AST node precedes it between BEGIN and itself).</li>
 *   <li>a {@link NodePosition.Last} mark – how often this node appears at the
 *       very end of a path (no AST node follows it between itself and END).</li>
 * </ul>
 *
 * <h2>Algorithm</h2>
 *
 * <h3>Forward pass (First marks)</h3>
 * BFS from the BEGIN sentinel.  Each wavefront state is a pair
 * {@code (node, seenAst)} where {@code seenAst} is {@code true} when at least
 * one AST node has been traversed since leaving BEGIN on this path.
 * <p>
 * For each AST node we record which {@code seenAst} values have arrived:
 * <ul>
 *   <li>only {@code false} arrived → {@link NodePosition.First#ALWAYS}</li>
 *   <li>only {@code true}  arrived → {@link NodePosition.First#NEVER}</li>
 *   <li>both arrived              → {@link NodePosition.First#SOMETIMES}</li>
 * </ul>
 * <p>
 * Traversing an outgoing edge from an AST node flips {@code seenAst} to
 * {@code true} for all successors.  Traversing from a synthetic node leaves
 * {@code seenAst} unchanged.
 * <p>
 * To handle cycles (Repeat back-edges) the BFS tracks visited
 * {@code (node, seenAst)} pairs and only enqueues a state if it has not been
 * seen before.
 *
 * <h3>Backward pass (Last marks)</h3>
 * Identical logic on the reversed graph, starting from END.
 * {@code seenAst} now means "at least one AST node has been traversed since
 * leaving END on this reversed path".
 */
public class GraphOptimizer {

    /**
     * Mutates {@code input} by setting {@link NodePosition.First} and
     * {@link NodePosition.Last} on every AST node, then returns it.
     */
    public static PathGraph markNodesPositions(PathGraph input) {
        // Build adjacency maps from the flat edge list.
        Map<PathNode, List<PathNode>> forward = buildAdjacency(input, false);
        Map<PathNode, List<PathNode>> backward = buildAdjacency(input, true);

        PathNode begin = findSentinel(input, PathNode.Kind.BEGIN);
        PathNode end = findSentinel(input, PathNode.Kind.END);

        // seenAstStates[node] = set of boolean "seenAst" values that reached this node
        Map<PathNode, Set<Boolean>> firstStates = bfs(begin, forward);
        Map<PathNode, Set<Boolean>> lastStates = bfs(end, backward);

        for (PathNode node : input.getNodes()) {
            if (node.getKind() != PathNode.Kind.AST
                    || node.getAstNode() instanceof AnchorNode) {
                continue;
            }

            node.setFirstPosition(toFirstMark(firstStates.get(node)));
            node.setLastPosition(toLastMark(lastStates.get(node)));
        }

        return input;
    }

    // -------------------------------------------------------------------------
    // BFS
    // -------------------------------------------------------------------------

    /**
     * BFS that propagates a {@code seenAst} boolean through the graph.
     *
     * @param origin      starting node (BEGIN for forward, END for backward)
     * @param adjacency   neighbours in the direction of traversal
     * @param originIsAst unused here – origin is always synthetic – kept for
     *                    symmetry; seenAst starts as {@code false} at origin
     * @return map from every reachable node to the set of {@code seenAst} boolean
     * values with which it was reached
     */
    private static Map<PathNode, Set<Boolean>> bfs(
            PathNode origin,
            Map<PathNode, List<PathNode>> adjacency) {

        Map<PathNode, Set<Boolean>> reachedWith = new HashMap<>();

        // State: (node, seenAst)
        // Use a simple pair encoded as Object[2] or a dedicated record.
        record State(PathNode node, boolean seenAst) {
        }

        Queue<State> queue = new ArrayDeque<>();
        Set<State> seen = new HashSet<>();

        // Origin is always synthetic (BEGIN / END), so seenAst starts false.
        State initial = new State(origin, false);
        queue.add(initial);
        seen.add(initial);
        reachedWith.computeIfAbsent(origin, k -> new HashSet<>()).add(false);

        while (!queue.isEmpty()) {
            State current = queue.poll();
            PathNode node = current.node();
            boolean seenAst = current.seenAst();

            // After leaving an AST node, successors know an AST node was seen.
            boolean seenAstForSuccessors = seenAst || (node.getKind() == PathNode.Kind.AST && !(node.getAstNode() instanceof AnchorNode));

            for (PathNode neighbour : adjacency.getOrDefault(node, Collections.emptyList())) {
                State next = new State(neighbour, seenAstForSuccessors);
                if (seen.add(next)) {           // only enqueue if not yet visited with this state
                    queue.add(next);
                    reachedWith.computeIfAbsent(neighbour, k -> new HashSet<>())
                            .add(seenAstForSuccessors);
                }
            }
        }

        return reachedWith;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a forward or backward adjacency map from the graph's edge list.
     *
     * @param reversed if {@code true}, reverses every edge (for the backward pass)
     */
    private static Map<PathNode, List<PathNode>> buildAdjacency(PathGraph graph, boolean reversed) {
        Map<PathNode, List<PathNode>> adj = new HashMap<>();
        for (PathEdge edge : graph.getEdges()) {
            PathNode from = reversed ? edge.to() : edge.from();
            PathNode to = reversed ? edge.from() : edge.to();
            adj.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        }
        return adj;
    }

    private static PathNode findSentinel(PathGraph graph, PathNode.Kind kind) {
        for (PathNode node : graph.getNodes()) {
            if (node.getKind() == kind) {
                return node;
            }
        }
        throw new IllegalArgumentException("Graph has no " + kind + " sentinel");
    }

    private static NodePosition.First toFirstMark(Set<Boolean> arrivedWith) {
        if (arrivedWith == null || arrivedWith.isEmpty()) {
            return NodePosition.First.NEVER;   // unreachable from BEGIN → treat as never first
        }
        boolean withoutAst = arrivedWith.contains(Boolean.FALSE);
        boolean withAst = arrivedWith.contains(Boolean.TRUE);
        if (withoutAst && withAst) {
            return NodePosition.First.SOMETIMES;
        }
        return withoutAst ? NodePosition.First.ALWAYS : NodePosition.First.NEVER;
    }

    private static NodePosition.Last toLastMark(Set<Boolean> arrivedWith) {
        if (arrivedWith == null || arrivedWith.isEmpty()) {
            return NodePosition.Last.NEVER;
        }
        boolean withoutAst = arrivedWith.contains(Boolean.FALSE);
        boolean withAst = arrivedWith.contains(Boolean.TRUE);
        if (withoutAst && withAst) {
            return NodePosition.Last.SOMETIMES;
        }
        return withoutAst ? NodePosition.Last.ALWAYS : NodePosition.Last.NEVER;
    }
}