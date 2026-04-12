package com.github.curiousoddman.rgxgen.lineages.optimization;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.lineages.PathGraph;
import com.github.curiousoddman.rgxgen.lineages.PathNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GraphOptimizer}.
 *
 * <p>Each test builds a real {@link PathGraph} from a regex string via
 * {@link RgxGen}, runs {@link GraphOptimizer#optimize}, and asserts the
 * {@link NodePosition.First} / {@link NodePosition.Last} marks on the AST nodes.
 *
 * <p>Expected values are derived by hand-tracing the BFS described in
 * {@link GraphOptimizer}. Traces are documented inline per test.
 */
class GraphOptimizerTest {

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Builds and optimizes a PathGraph from a regex pattern.
     */
    private static PathGraph optimized(String pattern) {
        PathGraph graph = RgxGen.parse(pattern).getPathGraph();
        return GraphOptimizer.optimize(graph);
    }

    /**
     * Returns all AST PathNodes from a graph, in graph insertion order.
     */
    private static List<PathNode> astNodes(PathGraph graph) {
        return graph.getNodes().stream()
                .filter(n -> n.getKind() == PathNode.Kind.AST)
                .collect(Collectors.toList());
    }

    /**
     * Asserts that every AST node has been stamped (no null marks).
     */
    private static void assertAllMarksPresent(PathGraph graph) {
        for (PathNode node : astNodes(graph)) {
            assertNotNull(node.getFirstPosition(),
                    "firstPosition is null on " + node);
            assertNotNull(node.getLastPosition(),
                    "lastPosition is null on " + node);
        }
    }

    // -------------------------------------------------------------------------
    // optimize() return-value contract
    // -------------------------------------------------------------------------

    @Test
    void optimizeReturnsSameInstance() {
        PathGraph graph = RgxGen.parse("a").getPathGraph();
        PathGraph result = GraphOptimizer.optimize(graph);
        assertSame(graph, result, "optimize() must return the same PathGraph instance");
    }

    // -------------------------------------------------------------------------
    // Non-AST nodes must not receive marks
    // -------------------------------------------------------------------------

    @Test
    void syntheticNodesAreNotMarked() {
        PathGraph graph = optimized("a|b");
        for (PathNode node : graph.getNodes()) {
            if (node.getKind() != PathNode.Kind.AST) {
                assertNull(node.getFirstPosition(),
                        "Synthetic node should not have firstPosition: " + node);
                assertNull(node.getLastPosition(),
                        "Synthetic node should not have lastPosition: " + node);
            }
        }
    }

    // -------------------------------------------------------------------------
    // "a" – single terminal
    // -------------------------------------------------------------------------
    // Graph: BEGIN → A → END
    // Forward: A reached with seenAst=false → ALWAYS first
    // Backward: A reached with seenAst=false (END is synthetic) → ALWAYS last

    @Test
    void singleNode_a() {
        PathGraph graph = optimized("a");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(1, ast.size());
        PathNode a = ast.get(0);
        assertEquals(NodePosition.First.ALWAYS, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.Last.ALWAYS, a.getLastPosition(), "a last");
    }

    // -------------------------------------------------------------------------
    // "ab" – two-node sequence
    // -------------------------------------------------------------------------
    // Graph: BEGIN → A → B → END
    // Forward BFS:
    //   BEGIN(false) → A(false), A(false) → B(true)
    //   A: {false} → ALWAYS first
    //   B: {true}  → NEVER  first
    // Backward BFS from END:
    //   END(false) → B(false), B(false) → A(true)
    //   B: {false} → ALWAYS last
    //   A: {true}  → NEVER  last

    @Test
    void sequence_ab() {
        PathGraph graph = optimized("(a)b");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(2, ast.size());
        PathNode a = ast.get(0);
        PathNode b = ast.get(1);

        assertEquals(NodePosition.First.ALWAYS, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.First.NEVER, b.getFirstPosition(), "b first");
        assertEquals(NodePosition.Last.NEVER, a.getLastPosition(), "a last");
        assertEquals(NodePosition.Last.ALWAYS, b.getLastPosition(), "b last");
    }

    // -------------------------------------------------------------------------
    // "abc" – three-node sequence
    // -------------------------------------------------------------------------
    // Graph: BEGIN → A → B → C → END
    // Forward: A:{false}→ALWAYS, B:{true}→NEVER, C:{true}→NEVER
    // Backward: C:{false}→ALWAYS, B:{true}→NEVER, A:{true}→NEVER

    @Test
    void sequence_abc() {
        PathGraph graph = optimized("a(b)c");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(3, ast.size());
        PathNode a = ast.get(0);
        PathNode b = ast.get(1);
        PathNode c = ast.get(2);

        assertEquals(NodePosition.First.ALWAYS, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.First.NEVER, b.getFirstPosition(), "b first");
        assertEquals(NodePosition.First.NEVER, c.getFirstPosition(), "c first");
        assertEquals(NodePosition.Last.NEVER, a.getLastPosition(), "a last");
        assertEquals(NodePosition.Last.NEVER, b.getLastPosition(), "b last");
        assertEquals(NodePosition.Last.ALWAYS, c.getLastPosition(), "c last");
    }

    // -------------------------------------------------------------------------
    // "a|b" – flat choice
    // -------------------------------------------------------------------------
    // Graph: BEGIN → CHOICE → a → END
    //                       → b → END
    // CHOICE is synthetic.
    // Forward:  a:{false}→ALWAYS, b:{false}→ALWAYS
    // Backward: a:{false}→ALWAYS, b:{false}→ALWAYS

    @Test
    void choice_a_or_b() {
        PathGraph graph = optimized("a|b");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(2, ast.size());
        PathNode a = ast.get(0);
        PathNode b = ast.get(1);

        assertEquals(NodePosition.First.ALWAYS, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.First.ALWAYS, b.getFirstPosition(), "b first");
        assertEquals(NodePosition.Last.ALWAYS, a.getLastPosition(), "a last");
        assertEquals(NodePosition.Last.ALWAYS, b.getLastPosition(), "b last");
    }

    // -------------------------------------------------------------------------
    // "a|b|c" – three-way choice
    // -------------------------------------------------------------------------
    // All three are independently reachable from BEGIN/END with seenAst=false.

    @Test
    void choice_a_or_b_or_c() {
        PathGraph graph = optimized("a|b|c");
        assertAllMarksPresent(graph);
        for (PathNode node : astNodes(graph)) {
            assertEquals(NodePosition.First.ALWAYS, node.getFirstPosition(),
                    node + " first");
            assertEquals(NodePosition.Last.ALWAYS, node.getLastPosition(),
                    node + " last");
        }
    }

    // -------------------------------------------------------------------------
    // "(a|b)c" – choice then sequence
    // -------------------------------------------------------------------------
    // Graph: BEGIN → CHOICE → a ─┐
    //                       → b ─┤→ C → END
    // Forward:  a:{false}→ALWAYS, b:{false}→ALWAYS, C:{true}→NEVER
    // Backward: C:{false}→ALWAYS, a:{true}→NEVER, b:{true}→NEVER

    @Test
    void choiceThenSequence() {
        PathGraph graph = optimized("(a|b)c");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        // Insertion order: a, b, c  (choice children visited first)
        assertEquals(3, ast.size());
        PathNode a = ast.get(0);
        PathNode b = ast.get(1);
        PathNode c = ast.get(2);

        assertEquals(NodePosition.First.ALWAYS, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.First.ALWAYS, b.getFirstPosition(), "b first");
        assertEquals(NodePosition.First.NEVER, c.getFirstPosition(), "c first");
        assertEquals(NodePosition.Last.NEVER, a.getLastPosition(), "a last");
        assertEquals(NodePosition.Last.NEVER, b.getLastPosition(), "b last");
        assertEquals(NodePosition.Last.ALWAYS, c.getLastPosition(), "c last");
    }

    // -------------------------------------------------------------------------
    // "a(b|c)" – sequence then choice
    // -------------------------------------------------------------------------
    // Graph: BEGIN → A → CHOICE → b → END
    //                           → c → END
    // Forward:  A:{false}→ALWAYS, b:{true}→NEVER, c:{true}→NEVER
    // Backward: b:{false}→ALWAYS, c:{false}→ALWAYS, A:{true}→NEVER

    @Test
    void sequenceThenChoice() {
        PathGraph graph = optimized("a(b|c)");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(3, ast.size());
        PathNode a = ast.get(0);
        PathNode b = ast.get(1);
        PathNode c = ast.get(2);

        assertEquals(NodePosition.First.ALWAYS, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.First.NEVER, b.getFirstPosition(), "b first");
        assertEquals(NodePosition.First.NEVER, c.getFirstPosition(), "c first");
        assertEquals(NodePosition.Last.NEVER, a.getLastPosition(), "a last");
        assertEquals(NodePosition.Last.ALWAYS, b.getLastPosition(), "b last");
        assertEquals(NodePosition.Last.ALWAYS, c.getLastPosition(), "c last");
    }

    // -------------------------------------------------------------------------
    // "a*b" – zero-or-more repeat followed by literal
    // -------------------------------------------------------------------------
    // Graph: BEGIN → REPEAT_ENTRY →[0..∞]→ A, A → REPEAT_ENTRY (back-edge),
    //               REPEAT_ENTRY → B → END
    //
    // Forward BFS:
    //   (BEGIN,false) → (REPEAT_ENTRY,false)
    //   (REPEAT_ENTRY,false) → (A,false), (B,false)
    //   (A,false) → (REPEAT_ENTRY,true)         ← A is AST, so seenAst flips
    //   (REPEAT_ENTRY,true) → (A,true), (B,true) ← new states
    //   A reached with {false,true} → SOMETIMES first
    //   B reached with {false,true} → SOMETIMES first
    //
    // Backward BFS from END:
    //   (END,false) → (B,false)
    //   (B,false)   → (REPEAT_ENTRY,true)       ← B is AST
    //   (REPEAT_ENTRY,true) → (A,true), (B,false)
    //   (A,true)    → (REPEAT_ENTRY,true)        ← already seen
    //   B reached with {false} → ALWAYS last
    //   A reached with {true}       → NEVER last

    @Test
    void repeatStar_a_then_b() {
        PathGraph graph = optimized("a*b");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(2, ast.size());
        PathNode a = ast.get(0);
        PathNode b = ast.get(1);

        assertEquals(NodePosition.First.SOMETIMES, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.First.SOMETIMES, b.getFirstPosition(), "b first");
        assertEquals(NodePosition.Last.NEVER, a.getLastPosition(), "a last");
        assertEquals(NodePosition.Last.ALWAYS, b.getLastPosition(), "b last");
    }

    // -------------------------------------------------------------------------
    // "a+" – one-or-more standalone
    // -------------------------------------------------------------------------
    // Same graph topology as "a*" (repeat with back-edge), same BFS result.
    // Graph: BEGIN → REPEAT_ENTRY → A, A → REPEAT_ENTRY, REPEAT_ENTRY → END
    //
    // Forward:
    //   (REPEAT_ENTRY,false) → (A,false)
    //   (A,false) → (REPEAT_ENTRY,true)
    //   (REPEAT_ENTRY,true) → (A,true)
    //   A reached with {false,true} → SOMETIMES first
    //
    // Backward:
    //   (END,false) → (REPEAT_ENTRY,false)
    //   (REPEAT_ENTRY,false) → (A,false)
    //   (A,false) → (REPEAT_ENTRY,true)
    //   (REPEAT_ENTRY,true) → (A,true)
    //   A reached with {false,true} → SOMETIMES last

    @Test
    void repeatPlus_a() {
        PathGraph graph = optimized("a+");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(1, ast.size());
        PathNode a = ast.get(0);

        assertEquals(NodePosition.First.SOMETIMES, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.Last.SOMETIMES, a.getLastPosition(), "a last");
    }

    // -------------------------------------------------------------------------
    // "ba+" – literal before repeat
    // -------------------------------------------------------------------------
    // Graph: BEGIN → B → REPEAT_ENTRY → A, A → REPEAT_ENTRY, REPEAT_ENTRY → END
    //
    // Forward:
    //   B:{false}→ALWAYS first
    //   (B,false) → (REPEAT_ENTRY,true)
    //   (REPEAT_ENTRY,true) → (A,true)
    //   (A,true) → (REPEAT_ENTRY,true) already seen
    //   A: {true} → NEVER first
    //
    // Backward:
    //   (END,false) → (REPEAT_ENTRY,false)
    //   (REPEAT_ENTRY,false) → (A,false), (B,false)
    //   (A,false) → (REPEAT_ENTRY,true)
    //   (REPEAT_ENTRY,true) → (A,true), (B,true)
    //   A: {false,true} → SOMETIMES last
    //   B: {false,true} → SOMETIMES last

    @Test
    void sequenceThenRepeatPlus() {
        PathGraph graph = optimized("ba+");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(2, ast.size());
        PathNode b = ast.get(0);
        PathNode a = ast.get(1);

        assertEquals(NodePosition.First.ALWAYS, b.getFirstPosition(), "b first");
        assertEquals(NodePosition.First.NEVER, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.Last.SOMETIMES, b.getLastPosition(), "b last");
        assertEquals(NodePosition.Last.SOMETIMES, a.getLastPosition(), "a last");
    }

    // -------------------------------------------------------------------------
    // "a{2,3}" – bounded repeat (same topology as a+)
    // -------------------------------------------------------------------------

    @Test
    void boundedRepeat() {
        PathGraph graph = optimized("a{2,3}");
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(1, ast.size());
        PathNode a = ast.get(0);
        // Same BFS result as a+ — topology is identical
        assertEquals(NodePosition.First.SOMETIMES, a.getFirstPosition(), "a first");
        assertEquals(NodePosition.Last.SOMETIMES, a.getLastPosition(), "a last");
    }

    // -------------------------------------------------------------------------
    // Parameterized: patterns where every AST node is ALWAYS first and ALWAYS last
    // -------------------------------------------------------------------------
    // These are single-alternative, single-node patterns.

    static Stream<Arguments> singleNodePatterns() {
        return Stream.of(
                Arguments.of("a"),
                Arguments.of("[abc]"),
                Arguments.of("[^x]")
        );
    }

    @ParameterizedTest
    @MethodSource("singleNodePatterns")
    void singleNodeIsAlwaysFirstAndLast(String pattern) {
        PathGraph graph = optimized(pattern);
        assertAllMarksPresent(graph);
        List<PathNode> ast = astNodes(graph);
        assertEquals(1, ast.size(), "Expected exactly 1 AST node for: " + pattern);
        assertEquals(NodePosition.First.ALWAYS, ast.get(0).getFirstPosition());
        assertEquals(NodePosition.Last.ALWAYS, ast.get(0).getLastPosition());
    }

    // -------------------------------------------------------------------------
    // Idempotency: running optimize twice must not change marks
    // -------------------------------------------------------------------------

    @Test
    void optimizeIsIdempotent() {
        PathGraph graph = optimized("a*b");
        // Run a second time
        GraphOptimizer.optimize(graph);

        List<PathNode> ast = astNodes(graph);
        PathNode a = ast.get(0);
        PathNode b = ast.get(1);
        assertEquals(NodePosition.First.SOMETIMES, a.getFirstPosition());
        assertEquals(NodePosition.First.SOMETIMES, b.getFirstPosition());
        assertEquals(NodePosition.Last.NEVER, a.getLastPosition());
        assertEquals(NodePosition.Last.ALWAYS, b.getLastPosition());
    }
}