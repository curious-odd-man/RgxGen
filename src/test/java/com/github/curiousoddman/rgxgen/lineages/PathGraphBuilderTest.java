package com.github.curiousoddman.rgxgen.lineages;

import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultNodeCreator;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PathGraphBuilderTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static PathGraph buildGraph(String pattern) {
        DefaultTreeBuilder treeBuilder = new DefaultTreeBuilder(pattern, new DefaultNodeCreator(), null);
        Node root = treeBuilder.get();
        return PathGraphBuilder.build(root);
    }

    /** All nodes of a given kind. */
    private static List<PathNode> nodesOfKind(PathGraph g, PathNode.Kind kind) {
        return g.getNodes().stream()
                .filter(n -> n.getKind() == kind)
                .collect(Collectors.toList());
    }

    /** Edges whose 'from' node has the given kind. */
    private static List<PathEdge> edgesFrom(PathGraph g, PathNode.Kind kind) {
        return g.getEdges().stream()
                .filter(e -> e.from().getKind() == kind)
                .collect(Collectors.toList());
    }

    /** Edges whose 'to' node has the given kind. */
    private static List<PathEdge> edgesTo(PathGraph g, PathNode.Kind kind) {
        return g.getEdges().stream()
                .filter(e -> e.to().getKind() == kind)
                .collect(Collectors.toList());
    }

    /** Edges from a specific node. */
    private static List<PathEdge> edgesFromNode(PathGraph g, PathNode node) {
        return g.getEdges().stream()
                .filter(e -> e.from() == node)
                .collect(Collectors.toList());
    }

    /** Edges into a specific node. */
    private static List<PathEdge> edgesIntoNode(PathGraph g, PathNode node) {
        return g.getEdges().stream()
                .filter(e -> e.to() == node)
                .collect(Collectors.toList());
    }

    private static PathNode singleNodeOfKind(PathGraph g, PathNode.Kind kind) {
        List<PathNode> nodes = nodesOfKind(g, kind);
        assertEquals(1, nodes.size(), "Expected exactly one " + kind + " node");
        return nodes.get(0);
    }

    // =========================================================================
    // Structural invariants that must hold for every pattern
    // =========================================================================

    @ParameterizedTest(name = "[{index}] \"{0}\"")
    @ValueSource(strings = {
            "a",
            "abc",
            "a|b",
            "(a|b)",
            "a+",
            "a*",
            "a?",
            "a{3}",
            "a{2,5}",
            "(a|b)+",
            "(a$|c)x",
            "(a|^x)+",
            "[a-z]+",
            "\\d{2,4}",
            "a(b|c)d",
            "(a(b|c))+",
    })
    void everyGraphHasExactlyOneBeginAndOneEnd(String pattern) {
        PathGraph g = buildGraph(pattern);
        assertEquals(1, nodesOfKind(g, PathNode.Kind.BEGIN).size(), "Should have exactly one BEGIN");
        assertEquals(1, nodesOfKind(g, PathNode.Kind.END).size(),   "Should have exactly one END");
    }

    @ParameterizedTest(name = "[{index}] \"{0}\"")
    @ValueSource(strings = {
            "a", "abc", "a|b", "(a|b)", "a+", "(a|b)+", "(a$|c)x", "(a|^x)+"
    })
    void beginHasNoIncomingEdges(String pattern) {
        PathGraph g = buildGraph(pattern);
        PathNode begin = singleNodeOfKind(g, PathNode.Kind.BEGIN);
        List<PathEdge> incoming = edgesIntoNode(g, begin);
        assertTrue(incoming.isEmpty(), "BEGIN must have no incoming edges but got: " + incoming);
    }

    @ParameterizedTest(name = "[{index}] \"{0}\"")
    @ValueSource(strings = {
            "a", "abc", "a|b", "(a|b)", "a+", "(a|b)+", "(a$|c)x", "(a|^x)+"
    })
    void endHasNoOutgoingEdges(String pattern) {
        PathGraph g = buildGraph(pattern);
        PathNode end = singleNodeOfKind(g, PathNode.Kind.END);
        List<PathEdge> outgoing = edgesFromNode(g, end);
        assertTrue(outgoing.isEmpty(), "END must have no outgoing edges but got: " + outgoing);
    }

    @ParameterizedTest(name = "[{index}] \"{0}\"")
    @ValueSource(strings = {
            "a", "abc", "a|b", "(a|b)", "a+", "(a|b)+", "(a$|c)x", "(a|^x)+"
    })
    void everyNodeIsReachableFromBegin(String pattern) {
        PathGraph g = buildGraph(pattern);
        PathNode begin = singleNodeOfKind(g, PathNode.Kind.BEGIN);

        // BFS from BEGIN
        Set<PathNode> visited = new java.util.HashSet<>();
        java.util.Queue<PathNode> queue = new java.util.ArrayDeque<>();
        queue.add(begin);
        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            if (visited.add(current)) {
                edgesFromNode(g, current).forEach(e -> queue.add(e.to()));
            }
        }

        for (PathNode node : g.getNodes()) {
            assertTrue(visited.contains(node),
                    "Node " + node + " is not reachable from BEGIN in pattern \"" + pattern + "\"");
        }
    }

    // =========================================================================
    // Single literal  "a"
    // =========================================================================

    @Test
    void singleLiteral_hasThreeNodes() {
        PathGraph g = buildGraph("a");
        // BEGIN, FinalSymbol(a), END
        assertEquals(3, g.getNodes().size());
    }

    @Test
    void singleLiteral_hasExactlyTwoEdges() {
        PathGraph g = buildGraph("a");
        assertEquals(2, g.getEdges().size());
    }

    @Test
    void singleLiteral_edgesAreOnceEach() {
        PathGraph g = buildGraph("a");
        for (PathEdge e : g.getEdges()) {
            assertEquals(1, e.min(), "All edges in single literal should be [1..1]");
            assertEquals(1, e.max(), "All edges in single literal should be [1..1]");
        }
    }

    // =========================================================================
    // Sequence  "abc"
    // =========================================================================

    @Test
    void sequence_chainedWithOnceEdges() {
        PathGraph g = buildGraph("[a-z]bc");
        // Every edge in a plain sequence is [1..1]
        for (PathEdge e : g.getEdges()) {
            assertEquals(1, e.min());
            assertEquals(1, e.max());
        }
    }

    @Test
    void sequence_literalCollapsedToSingleNode() {
        // The parser collapses "abc" into a single FinalSymbol("abc") — not three nodes.
        PathGraph g = buildGraph("abc");
        assertEquals(3, g.getNodes().size()); // BEGIN + FinalSymbol(abc) + END
    }

    @Test
    void sequence_distinctParts_correctNodeCount() {
        // "[a-z]bc" forces a real Sequence: SymbolSet + FinalSymbol(bc)
        // Nodes: BEGIN, SymbolSet, FinalSymbol(bc), END = 4
        PathGraph g = buildGraph("[a-z]bc");
        assertEquals(4, g.getNodes().size());
    }

    @Test
    void sequence_linearOrder() {
        // In a pure sequence every non-END node has exactly one outgoing edge
        // and every non-BEGIN node has exactly one incoming edge
        PathGraph g = buildGraph("[a-z]bc");
        for (PathNode n : g.getNodes()) {
            if (n.getKind() != PathNode.Kind.BEGIN) {
                assertEquals(1, edgesIntoNode(g, n).size(),
                        "incoming edges for " + n);
            }
            if (n.getKind() != PathNode.Kind.END) {
                assertEquals(1, edgesFromNode(g, n).size(),
                        "outgoing edges for " + n);
            }
        }
    }

    // =========================================================================
    // Choice  "a|b"
    // =========================================================================

    @Test
    void choice_hasSyntheticChoiceNode() {
        PathGraph g = buildGraph("a|b");
        assertEquals(1, nodesOfKind(g, PathNode.Kind.CHOICE).size());
    }

    @Test
    void choice_choiceNodeHasTwoOutgoingEdges() {
        PathGraph g = buildGraph("a|b");
        PathNode choice = singleNodeOfKind(g, PathNode.Kind.CHOICE);
        assertEquals(2, edgesFromNode(g, choice).size());
    }

    @Test
    void choice_bothAlternativesConnectToEnd() {
        PathGraph g = buildGraph("a|b");
        PathNode end = singleNodeOfKind(g, PathNode.Kind.END);
        // Two AST nodes (a, b) each connect to END
        assertEquals(2, edgesIntoNode(g, end).size());
    }

    @Test
    void choice_threeAlternatives_choiceNodeHasThreeOutgoing() {
        PathGraph g = buildGraph("a|b|c");
        PathNode choice = singleNodeOfKind(g, PathNode.Kind.CHOICE);
        assertEquals(3, edgesFromNode(g, choice).size());
    }

    // =========================================================================
    // Repeat  "a+"  "a*"  "a?"  "a{2,5}"
    // =========================================================================

    @Test
    void repeatPlus_hasSyntheticRepeatEntry() {
        PathGraph g = buildGraph("a+");
        assertEquals(1, nodesOfKind(g, PathNode.Kind.REPEAT_ENTRY).size());
    }

    @Test
    void repeatPlus_minIsOneMaxIsUnbounded() {
        PathGraph g = buildGraph("a+");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        // Forward edge from REPEAT_ENTRY
        List<PathEdge> forward = edgesFromNode(g, rep).stream()
                .filter(e -> e.to().getKind() == PathNode.Kind.AST)
                .collect(Collectors.toList());
        assertEquals(1, forward.size());
        assertEquals(1, forward.get(0).min());
        assertEquals(PathEdge.UNBOUNDED, forward.get(0).max());
    }

    @Test
    void repeatStar_minIsZeroMaxIsUnbounded() {
        PathGraph g = buildGraph("a*");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        List<PathEdge> forward = edgesFromNode(g, rep).stream()
                .filter(e -> e.to().getKind() == PathNode.Kind.AST)
                .collect(Collectors.toList());
        assertEquals(1, forward.size());
        assertEquals(0, forward.get(0).min());
        assertEquals(PathEdge.UNBOUNDED, forward.get(0).max());
    }

    @Test
    void repeatQuestion_minIsZeroMaxIsOne() {
        PathGraph g = buildGraph("a?");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        List<PathEdge> forward = edgesFromNode(g, rep).stream()
                .filter(e -> e.to().getKind() == PathNode.Kind.AST)
                .collect(Collectors.toList());
        assertEquals(1, forward.size());
        assertEquals(0, forward.get(0).min());
        assertEquals(1, forward.get(0).max());
    }

    @Test
    void repeatFixed_minEqualsMax() {
        PathGraph g = buildGraph("a{3}");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        List<PathEdge> forward = edgesFromNode(g, rep).stream()
                .filter(e -> e.to().getKind() == PathNode.Kind.AST)
                .collect(Collectors.toList());
        assertEquals(1, forward.size());
        assertEquals(3, forward.get(0).min());
        assertEquals(3, forward.get(0).max());
    }

    @Test
    void repeatRange_minAndMaxPreserved() {
        PathGraph g = buildGraph("a{2,5}");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        List<PathEdge> forward = edgesFromNode(g, rep).stream()
                .filter(e -> e.to().getKind() == PathNode.Kind.AST)
                .collect(Collectors.toList());
        assertEquals(1, forward.size());
        assertEquals(2, forward.get(0).min());
        assertEquals(5, forward.get(0).max());
    }

    @Test
    void repeat_bodyExitHasBackEdgeToRepeatEntry() {
        PathGraph g = buildGraph("a+");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        // Back-edge: AST node → REPEAT_ENTRY
        List<PathEdge> backEdges = edgesIntoNode(g, rep).stream()
                .filter(e -> e.from().getKind() == PathNode.Kind.AST)
                .collect(Collectors.toList());
        assertEquals(1, backEdges.size(), "Should have exactly one back-edge from body to REPEAT_ENTRY");
    }

    @Test
    void repeat_repeatEntryConnectsToEnd() {
        PathGraph g = buildGraph("a+");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        PathNode end = singleNodeOfKind(g, PathNode.Kind.END);
        boolean repeatConnectsToEnd = edgesFromNode(g, rep).stream()
                .anyMatch(e -> e.to() == end);
        assertTrue(repeatConnectsToEnd, "REPEAT_ENTRY should connect to END (it is the fragment exit)");
    }

    // =========================================================================
    // Example 1:  (a$|c)x
    // =========================================================================

    @Test
    void example1_nodeKindCounts() {
        PathGraph g = buildGraph("(a$|c)x");
        // BEGIN, CHOICE, FinalSymbol(a$), FinalSymbol(c), FinalSymbol(x), END
        assertEquals(1, nodesOfKind(g, PathNode.Kind.BEGIN).size());
        assertEquals(1, nodesOfKind(g, PathNode.Kind.END).size());
        assertEquals(1, nodesOfKind(g, PathNode.Kind.CHOICE).size());
        assertEquals(0, nodesOfKind(g, PathNode.Kind.REPEAT_ENTRY).size());
        assertEquals(4, nodesOfKind(g, PathNode.Kind.AST).size()); // a, $, c, x
    }

    @Test
    void example1_choiceHasTwoBranches() {
        PathGraph g = buildGraph("(a$|c)x");
        PathNode choice = singleNodeOfKind(g, PathNode.Kind.CHOICE);
        assertEquals(2, edgesFromNode(g, choice).size());
    }

    @Test
    void example1_bothBranchesConvergeOnX() {
        PathGraph g = buildGraph("(a$|c)x");
        // Find the AST node for "x" — it is the one that connects to END
        PathNode end = singleNodeOfKind(g, PathNode.Kind.END);
        List<PathEdge> toEnd = edgesIntoNode(g, end);
        assertEquals(1, toEnd.size(), "Only x connects to END");

        PathNode xNode = toEnd.get(0).from();
        // Both alternative exits (a$ and c) connect to x
        List<PathEdge> toX = edgesIntoNode(g, xNode);
        assertEquals(2, toX.size(), "Both branches (a$ and c) should connect to x");
    }

    @Test
    void example1_allEdgesAreOnce() {
        PathGraph g = buildGraph("(a$|c)x");
        for (PathEdge e : g.getEdges()) {
            assertEquals(1, e.min(), "All edges should be [1..1] for (a$|c)x");
            assertEquals(1, e.max(), "All edges should be [1..1] for (a$|c)x");
        }
    }

    // =========================================================================
    // Example 2:  (a|^x)+
    // =========================================================================

    @Test
    void example2_nodeKindCounts() {
        PathGraph g = buildGraph("(a|^x)+");
        assertEquals(1, nodesOfKind(g, PathNode.Kind.BEGIN).size());
        assertEquals(1, nodesOfKind(g, PathNode.Kind.END).size());
        assertEquals(1, nodesOfKind(g, PathNode.Kind.REPEAT_ENTRY).size());
        assertEquals(1, nodesOfKind(g, PathNode.Kind.CHOICE).size());
        assertEquals(3, nodesOfKind(g, PathNode.Kind.AST).size()); // a, ^, x
    }

    @Test
    void example2_repeatEntryIsBoundedOneToInfinity() {
        PathGraph g = buildGraph("(a|^x)+");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        List<PathEdge> forward = edgesFromNode(g, rep).stream()
                .filter(e -> e.to().getKind() == PathNode.Kind.CHOICE)
                .toList();
        assertEquals(1, forward.size());
        assertEquals(1, forward.get(0).min());
        assertEquals(PathEdge.UNBOUNDED, forward.get(0).max());
    }

    @Test
    void example2_bothAlternativesHaveBackEdgeToRepeatEntry() {
        PathGraph g = buildGraph("(a|^x)+");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        // Two AST nodes (a and ^x) should both have back-edges to REPEAT_ENTRY
        List<PathEdge> backEdges = edgesIntoNode(g, rep).stream()
                .filter(e -> e.from().getKind() == PathNode.Kind.AST)
                .collect(Collectors.toList());
        assertEquals(2, backEdges.size(),
                "Both 'a' and '^x' must have back-edges to REPEAT_ENTRY");
    }

    @Test
    void example2_choiceHasTwoOutgoing() {
        PathGraph g = buildGraph("(a|^x)+");
        PathNode choice = singleNodeOfKind(g, PathNode.Kind.CHOICE);
        assertEquals(2, edgesFromNode(g, choice).size());
    }

    @Test
    void example2_repeatEntryConnectsToEnd() {
        PathGraph g = buildGraph("(a|^x)+");
        PathNode rep = singleNodeOfKind(g, PathNode.Kind.REPEAT_ENTRY);
        PathNode end = singleNodeOfKind(g, PathNode.Kind.END);
        assertTrue(edgesFromNode(g, rep).stream().anyMatch(e -> e.to() == end));
    }

    // =========================================================================
    // Nested repeat  "(a+b)+"
    // =========================================================================

    @Test
    void nestedRepeat_hasTwoRepeatEntryNodes() {
        PathGraph g = buildGraph("(a+b)+");
        assertEquals(2, nodesOfKind(g, PathNode.Kind.REPEAT_ENTRY).size());
    }

    @Test
    void nestedRepeat_eachRepeatEntryHasItsOwnBackEdge() {
        PathGraph g = buildGraph("(a+b)+");
        for (PathNode rep : nodesOfKind(g, PathNode.Kind.REPEAT_ENTRY)) {
            long backEdgeCount = edgesIntoNode(g, rep).stream()
                    .filter(e -> e.from().getKind() != PathNode.Kind.BEGIN)
                    .count();
            assertTrue(backEdgeCount >= 1,
                    "Each REPEAT_ENTRY must have at least one back-edge, got 0 for " + rep);
        }
    }

    // =========================================================================
    // PlantUML output smoke test
    // =========================================================================

    @Test
    void plantUml_containsStartAndEndMarkers() {
        PathGraph g = buildGraph("a|b");
        String uml = g.toPlantUml();
        assertTrue(uml.contains("@startuml"), "Should start with @startuml");
        assertTrue(uml.contains("@enduml"),   "Should end with @enduml");
    }

    @Test
    void plantUml_containsBeginAndEndLabels() {
        PathGraph g = buildGraph("a");
        String uml = g.toPlantUml();
        assertTrue(uml.contains("BEGIN"), "PlantUML should reference BEGIN node");
        assertTrue(uml.contains("END"),   "PlantUML should reference END node");
    }

    @Test
    void plantUml_edgeLabelOnceToOnce() {
        PathGraph g = buildGraph("a");
        String uml = g.toPlantUml();
        assertTrue(uml.contains("1"), "Should have [1] edge labels");
    }

    @Test
    void plantUml_edgeLabelUnbounded() {
        PathGraph g = buildGraph("a+");
        String uml = g.toPlantUml();
        assertTrue(uml.contains("<&infinity>"), "Should have <&infinity> in edge labels for unbounded repeat");
    }

    @Test
    void plantUml_isNonEmpty() {
        for (String pattern : new String[]{"a", "a|b", "a+", "(a|b)+", "(a$|c)x"}) {
            PathGraph g = buildGraph(pattern);
            assertFalse(g.toPlantUml().isBlank(), "PlantUML output should not be blank for \"" + pattern + "\"");
        }
    }

    // =========================================================================
    // Group transparency
    // =========================================================================

    @Test
    void group_doesNotAddExtraNode() {
        // "(a)" and "a" should produce the same graph shape
        PathGraph withGroup    = buildGraph("(a)");
        PathGraph withoutGroup = buildGraph("a");
        assertEquals(withoutGroup.getNodes().size(), withGroup.getNodes().size(),
                "Grouping a single literal should not add extra nodes");
        assertEquals(withoutGroup.getEdges().size(), withGroup.getEdges().size(),
                "Grouping a single literal should not add extra edges");
    }

    // =========================================================================
    // SymbolSet  "[a-z]+"
    // =========================================================================

    @Test
    void symbolSet_treatedAsTerminal() {
        PathGraph g = buildGraph("[a-z]");
        // BEGIN, SymbolSet, END
        assertEquals(3, g.getNodes().size());
        assertEquals(1, nodesOfKind(g, PathNode.Kind.AST).size());
    }

    @Test
    void symbolSetWithRepeat_hasRepeatEntry() {
        PathGraph g = buildGraph("[a-z]+");
        assertEquals(1, nodesOfKind(g, PathNode.Kind.REPEAT_ENTRY).size());
        assertEquals(1, nodesOfKind(g, PathNode.Kind.AST).size());
    }
}
