package com.github.curiousoddman.rgxgen.lineages;

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;

import java.util.*;

/**
 * Compiles a parsed RgxGen AST into a {@link PathGraph}.
 *
 * <h2>Usage</h2>
 * <pre>
 *   Node root = RgxGen.parse("(a|b)+x").getNode();   // however you obtain the root
 *   PathGraph graph = PathGraphBuilder.build(root);
 *   System.out.println(graph.toPlantUml());
 * </pre>
 *
 * <h2>Algorithm</h2>
 * The builder implements {@link NodeVisitor}.  Each {@code visit} method compiles its
 * AST node into a {@link Fragment} and pushes it onto an internal stack.  Compound
 * nodes (Sequence, Choice, Repeat, Group) pop their children's fragments off the stack
 * and wire them together before pushing the composite fragment back.
 * <p>
 * Once the root AST node has been visited the stack contains exactly one Fragment.
 * {@link #build(Node)} then wraps it with BEGIN/END sentinels and returns the
 * fully assembled {@link PathGraph}.
 *
 * <h2>Node treatment</h2>
 * <ul>
 *   <li><b>FinalSymbol, SymbolSet, NotSymbol, GroupRef</b> – terminals: one PathNode,
 *       no internal edges.</li>
 *   <li><b>Group</b> – transparent wrapper: delegates to its child, does not add its
 *       own PathNode.</li>
 *   <li><b>Sequence</b> – chains child fragments left-to-right with {@code [1..1]}
 *       edges.</li>
 *   <li><b>Choice</b> – creates a synthetic {@code CHOICE} PathNode; fans out to each
 *       alternative with {@code [1..1]} edges; exits are the union of all alternative
 *       exits.</li>
 *   <li><b>Repeat</b> – creates a synthetic {@code REPEAT_ENTRY} PathNode.
 *       <ul>
 *         <li>Forward edge: {@code REPEAT_ENTRY → child.entries}  with {@code [min..max]}.</li>
 *         <li>Back-edges:   {@code child.exits   → REPEAT_ENTRY}  with {@code [min..max]}
 *             (same bounds – each loop-back is governed by the same repetition rule).</li>
 *         <li>The fragment exits are the exits of REPEAT_ENTRY itself (i.e. the
 *             REPEAT_ENTRY node), so the parent can connect "after the loop".</li>
 *       </ul>
 *   </li>
 * </ul>
 */
public class PathGraphBuilder implements NodeVisitor {

    // Stack of fragments produced by child visits.
    // Compound nodes pop N fragments (one per child) and push one composite fragment.
    private final Deque<Fragment> stack = new ArrayDeque<>();

    // The graph being assembled – edges are registered here as they are created.
    private final PathGraph graph = new PathGraph();

    // Monotonic counter for node IDs, scoped to this builder instance.
    // Resets to 0 for each new build(), so IDs are stable and start from 0
    // regardless of how many graphs have been built in the same JVM.
    private int nextId = 0;

    private int nextId() {
        return nextId++;
    }

    // -------------------------------------------------------------------------
    // Public entry point
    // -------------------------------------------------------------------------

    /**
     * Builds and returns the path graph for the given AST root node.
     */
    public static PathGraph build(Node root) {
        PathGraphBuilder builder = new PathGraphBuilder();
        root.visit(builder);

        Fragment rootFragment = builder.stack.pop();
        if (!builder.stack.isEmpty()) {
            throw new IllegalStateException("Fragment stack should be empty after compilation; remaining: " + builder.stack.size());
        }

        // Wrap with BEGIN and END sentinels
        PathNode begin = PathNode.begin(builder.nextId());
        PathNode end = PathNode.end(builder.nextId());

        builder.graph.addNode(begin);
        builder.graph.addNode(end);

        for (PathNode entry : rootFragment.getEntries()) {
            builder.addEdge(PathEdge.once(begin, entry));
        }
        for (PathNode exit : rootFragment.getExits()) {
            builder.addEdge(PathEdge.once(exit, end));
        }

        return builder.graph;
    }

    // -------------------------------------------------------------------------
    // Terminal nodes  (leaf → one PathNode, no internal edges)
    // -------------------------------------------------------------------------

    @Override
    public void visit(FinalSymbol node) {
        pushTerminal(node);
    }

    @Override
    public void visit(SymbolSet node) {
        pushTerminal(node);
    }

    @Override
    public void visit(NotSymbol node) {
        // NotSymbol wraps a child but is itself treated as an opaque terminal for
        // path-graph purposes (its child describes the negation set, not a sequence
        // of visited nodes during generation).
        pushTerminal(node);
    }

    @Override
    public void visit(GroupRef node) {
        // Backreference: treated as a terminal – the value is resolved at runtime.
        pushTerminal(node);
    }

    // -------------------------------------------------------------------------
    // Group  (transparent wrapper)
    // -------------------------------------------------------------------------

    @Override
    public void visit(Group node) {
        // Groups are purely structural wrappers; they do not introduce a path node.
        // Just compile the child and let its fragment bubble up.
        node.getNode().visit(this);
        // Fragment is already on the stack from the child visit – nothing more to do.
    }

    // -------------------------------------------------------------------------
    // Sequence
    // -------------------------------------------------------------------------

    @Override
    public void visit(Sequence node) {
        Node[] children = node.getNodes();

        // Visit all children – each pushes a fragment
        for (Node child : children) {
            child.visit(this);
        }

        // Pop in reverse order then reverse so we get left-to-right
        List<Fragment> childFragments = new ArrayList<>(children.length);
        for (int i = 0; i < children.length; i++) {
            childFragments.add(0, stack.pop());
        }

        // Chain: exits of fragment[i] → entries of fragment[i+1]  with [1..1]
        List<PathEdge> sequenceEdges = new ArrayList<>();
        for (int i = 0; i < childFragments.size() - 1; i++) {
            Fragment current = childFragments.get(i);
            Fragment next = childFragments.get(i + 1);
            for (PathNode exit : current.getExits()) {
                for (PathNode entry : next.getEntries()) {
                    sequenceEdges.add(addEdge(PathEdge.once(exit, entry)));
                }
            }
        }

        // Gather all internal edges from children plus the new connecting edges
        List<PathEdge> allEdges = new ArrayList<>();
        for (Fragment f : childFragments) {
            allEdges.addAll(f.getInternalEdges());
        }
        allEdges.addAll(sequenceEdges);

        Fragment composite = new Fragment(
                childFragments.get(0).getEntries(),
                childFragments.get(childFragments.size() - 1).getExits(),
                allEdges
        );
        stack.push(composite);
    }

    // -------------------------------------------------------------------------
    // Choice
    // -------------------------------------------------------------------------

    @Override
    public void visit(Choice node) {
        Node[] alternatives = node.getNodes();

        // Visit all alternatives – each pushes a fragment
        for (Node alt : alternatives) {
            alt.visit(this);
        }

        // Pop in reverse order
        List<Fragment> altFragments = new ArrayList<>(alternatives.length);
        for (int i = 0; i < alternatives.length; i++) {
            altFragments.add(0, stack.pop());
        }

        // Synthetic CHOICE dispatcher node
        PathNode choiceNode = PathNode.choice(node, nextId());
        graph.addNode(choiceNode);

        List<PathEdge> choiceEdges = new ArrayList<>();

        // Fan out: choiceNode → each alternative's entries with [1..1]
        for (Fragment alt : altFragments) {
            for (PathNode entry : alt.getEntries()) {
                choiceEdges.add(addEdge(PathEdge.once(choiceNode, entry)));
            }
        }

        // Exits of the Choice fragment = union of all alternative exits
        List<PathNode> exits = new ArrayList<>();
        for (Fragment alt : altFragments) {
            exits.addAll(alt.getExits());
            choiceEdges.addAll(alt.getInternalEdges());
        }

        Fragment composite = new Fragment(
                Collections.singletonList(choiceNode),
                exits,
                choiceEdges
        );
        stack.push(composite);
    }

    // -------------------------------------------------------------------------
    // Repeat
    // -------------------------------------------------------------------------

    @Override
    public void visit(Repeat node) {
        // Visit the body child first
        node.getNode().visit(this);
        Fragment bodyFragment = stack.pop();

        int min = node.getMin();
        int max = node.getMax();   // -1 = unbounded

        // Synthetic REPEAT_ENTRY node – marks the loop re-entry point
        PathNode repeatEntry = PathNode.repeatEntry(node, nextId());
        graph.addNode(repeatEntry);

        List<PathEdge> repeatEdges = new ArrayList<>(bodyFragment.getInternalEdges());

        // Forward edge: REPEAT_ENTRY → body entries  [min..max]
        for (PathNode bodyEntry : bodyFragment.getEntries()) {
            repeatEdges.add(addEdge(PathEdge.repeat(repeatEntry, bodyEntry, min, max)));
        }

        // Back-edges: body exits → REPEAT_ENTRY  [min..max]
        // These encode "loop again" – same bounds as the forward edge because each
        // iteration is governed by the same Repeat node.
        for (PathNode bodyExit : bodyFragment.getExits()) {
            repeatEdges.add(addEdge(PathEdge.repeat(bodyExit, repeatEntry, min, max)));
        }

        // The fragment for this Repeat has:
        //   entry = REPEAT_ENTRY  (single stable entry point)
        //   exit  = REPEAT_ENTRY  (control returns here after each iteration;
        //                          the parent connects "after the loop" from here)
        Fragment composite = new Fragment(
                Collections.singletonList(repeatEntry),
                Collections.singletonList(repeatEntry),
                repeatEdges
        );
        stack.push(composite);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void pushTerminal(Node astNode) {
        PathNode pathNode = PathNode.forAst(astNode, nextId());
        graph.addNode(pathNode);
        stack.push(Fragment.terminal(pathNode));
    }

    /**
     * Registers an edge in the graph and returns it (for fluent chaining).
     */
    private PathEdge addEdge(PathEdge edge) {
        graph.addEdge(edge);
        return edge;
    }
}
