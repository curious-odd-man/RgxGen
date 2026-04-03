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

import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;

import java.util.*;

/**
 * Compiles a parsed RgxGen AST into a {@link PathGraph}.
 *
 * <h2>Usage</h2>
 * <pre>
 *   Node root = RgxGen.parse("(a|b)+x").getNode();
 *   PathGraph graph = PathGraphBuilder.build(root);
 *   System.out.println(graph.toPlantUml());
 * </pre>
 *
 * <h2>Algorithm</h2>
 * The builder implements {@link NodeVisitor}.  Each {@code visit} method compiles its
 * AST node into a {@link Fragment} and pushes it onto an internal stack.  Compound
 * nodes (Sequence, Choice, Repeat, Group) pop their children's fragments off the stack
 * and wire them together before pushing the composite fragment back.
 *
 * <h2>Cluster tracking</h2>
 * In addition to building the graph, the builder maintains a <em>cluster scope
 * stack</em>.  Before visiting the children of a compound node the builder pushes a
 * fresh {@link PathGraphCluster} onto the scope stack.  Every newly created
 * {@link PathNode} is registered as a direct member of whichever cluster is on top of
 * the scope stack (or treated as unclustered if the stack is empty, which only happens
 * for the BEGIN/END sentinels).  After all children have been visited the cluster is
 * popped and attached as a child of the now-top cluster (or stored as a root cluster on
 * the graph if the stack becomes empty).
 *
 * <p>This guarantees that nested compound nodes produce nested {@link PathGraphCluster}s,
 * which are then rendered as nested {@code rectangle} blocks in PlantUML.
 *
 * <h2>Node treatment</h2>
 * <ul>
 *   <li><b>FinalSymbol, SymbolSet, NotSymbol, GroupRef</b> – terminals.</li>
 *   <li><b>Group</b> – transparent wrapper; its cluster groups its single child's nodes.</li>
 *   <li><b>Sequence</b> – chains child fragments left-to-right with {@code [1..1]} edges.</li>
 *   <li><b>Choice</b> – synthetic {@code CHOICE} node fans out to alternatives.</li>
 *   <li><b>Repeat</b> – synthetic {@code REPEAT_ENTRY} node with forward + back edges.</li>
 * </ul>
 */
public class PathGraphBuilder implements NodeVisitor {

    // -------------------------------------------------------------------------
    // Fragment stack (unchanged from original)
    // -------------------------------------------------------------------------

    private final Deque<Fragment> stack = new ArrayDeque<>();
    private final PathGraph graph;
    private int nextId = 0;

    // -------------------------------------------------------------------------
    // Cluster scope stack
    //
    // Invariant: the TOP of this stack is the "current" cluster.  Every PathNode
    // created via createPathNode() is registered as a direct member of the top
    // cluster.  Compound visit methods bracket their child visits with
    //   pushCluster(label) … popCluster()
    // so that all nodes created inside a compound node end up inside its cluster.
    // -------------------------------------------------------------------------

    private final Deque<PathGraphCluster> clusterStack = new ArrayDeque<>();

    private PathGraphBuilder(String pattern) {
        graph = new PathGraph(pattern);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private int nextId() {
        return nextId++;
    }

    /**
     * Creates a PathNode for an AST terminal, registers it with the graph and
     * with the current cluster (if any cluster scope is active).
     */
    private PathNode createPathNode(Node astNode) {
        PathNode pathNode = PathNode.forAst(astNode, nextId());
        graph.addNode(pathNode);
        registerWithCurrentCluster(pathNode.getId());
        return pathNode;
    }

    /**
     * Creates a synthetic PathNode (CHOICE or REPEAT_ENTRY), registers it with
     * the graph and with the current cluster.
     */
    private PathNode createSyntheticNode(PathNode synthetic) {
        graph.addNode(synthetic);
        registerWithCurrentCluster(synthetic.getId());
        return synthetic;
    }

    /**
     * Registers {@code nodeId} as a direct member of the top cluster, if present.
     */
    private void registerWithCurrentCluster(String nodeId) {
        if (!clusterStack.isEmpty()) {
            clusterStack.peek().addDirectNode(nodeId);
        }
    }

    /**
     * Opens a new cluster scope.  All PathNodes created until the matching
     * {@link #popCluster()} call will be direct members of this cluster.
     */
    private void pushCluster(String label) {
        PathGraphCluster cluster = new PathGraphCluster(label);
        clusterStack.push(cluster);
    }

    /**
     * Closes the current cluster scope and wires it into its parent cluster (or
     * adds it as a root-level cluster on the graph when the scope stack becomes
     * empty).
     *
     * <p>The child cluster is <em>removed</em> from the parent's direct-node set
     * for nodes that are already captured by the child cluster – PlantUML renders
     * each node reference only once, so we must not list a node both in the parent
     * and in a nested {@code rectangle}.
     */
    private void popCluster() {
        PathGraphCluster finished = clusterStack.pop();

        if (clusterStack.isEmpty()) {
            // This was a top-level cluster (e.g. the root compound node).
            graph.addRootCluster(finished);
        } else {
            PathGraphCluster parent = clusterStack.peek();

            // The nodes that belong to 'finished' were registered on 'parent' as
            // direct members (because registerWithCurrentCluster saw 'parent' as top
            // at the time they were created – WRONG if we push before visiting children).
            //
            // Actually: we push BEFORE visiting children, so clusterStack.peek() at
            // creation time IS 'finished', not 'parent'.  Therefore 'parent' does NOT
            // contain those node IDs yet – we do NOT need to scrub them.  We simply
            // attach 'finished' as a child of 'parent'.
            parent.addChild(finished);
        }
    }

    /**
     * Registers an edge in the graph and returns it (for fluent chaining).
     */
    private PathEdge addEdge(PathEdge edge) {
        graph.addEdge(edge);
        return edge;
    }

    // -------------------------------------------------------------------------
    // Public entry point
    // -------------------------------------------------------------------------

    /**
     * Builds and returns the path graph for the given AST root node.
     */
    public static PathGraph build(Node root) {
        PathGraphBuilder builder = new PathGraphBuilder(root.getPattern());
        root.visit(builder);

        Fragment rootFragment = builder.stack.pop();
        if (!builder.stack.isEmpty()) {
            throw new IllegalStateException(
                    "Fragment stack should be empty after compilation; remaining: " + builder.stack.size());
        }

        // BEGIN / END sentinels are NOT part of any cluster – they are added after
        // all cluster scopes have been closed.
        PathNode begin = PathNode.begin(builder.nextId());
        PathNode end = PathNode.end(builder.nextId());

        builder.graph.addNode(begin);
        builder.graph.addNode(end);

        for (PathNode entry : rootFragment.entries()) {
            builder.addEdge(PathEdge.once(begin, entry));
        }
        for (PathNode exit : rootFragment.exits()) {
            builder.addEdge(PathEdge.once(exit, end));
        }

        return builder.graph;
    }

    // -------------------------------------------------------------------------
    // Terminal nodes
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
        pushTerminal(node);
    }

    @Override
    public void visit(GroupRef node) {
        pushTerminal(node);
    }

    @Override
    public void visit(AnchorNode node) {
        pushTerminal(node);
    }

    // -------------------------------------------------------------------------
    // Group  (transparent wrapper – still gets its own cluster for visual grouping)
    // -------------------------------------------------------------------------

    @Override
    public void visit(Group node) {
        // Open a cluster so the group's contents are visually enclosed.
        String label = labelFor(node, "Group " + node.getIndex());
        pushCluster(label);

        node.getNode().visit(this);
        // child fragment is on the stack – leave it; popCluster does not touch the stack.

        popCluster();
        // The child's fragment is already on the stack – nothing more to push.
    }

    // -------------------------------------------------------------------------
    // Sequence
    // -------------------------------------------------------------------------

    @Override
    public void visit(Sequence node) {
        Node[] children = node.getNodes();

        String label = labelFor(node, "Sequence");
        pushCluster(label);

        List<Fragment> childFragments = getFragments(children);

        // Chain: exits of fragment[i] → entries of fragment[i+1]  with [1..1]
        List<PathEdge> sequenceEdges = new ArrayList<>();
        for (int i = 0; i < childFragments.size() - 1; i++) {
            Fragment current = childFragments.get(i);
            Fragment next = childFragments.get(i + 1);
            for (PathNode exit : current.exits()) {
                for (PathNode entry : next.entries()) {
                    sequenceEdges.add(addEdge(PathEdge.once(exit, entry)));
                }
            }
        }

        List<PathEdge> allEdges = new ArrayList<>();
        for (Fragment f : childFragments) {
            allEdges.addAll(f.internalEdges());
        }
        allEdges.addAll(sequenceEdges);

        Fragment composite = new Fragment(
                childFragments.get(0).entries(),
                childFragments.get(childFragments.size() - 1).exits(),
                allEdges
        );
        stack.push(composite);

        popCluster();
    }

    // -------------------------------------------------------------------------
    // Choice
    // -------------------------------------------------------------------------

    @Override
    public void visit(Choice node) {
        Node[] alternatives = node.getNodes();

        String label = labelFor(node, "Choice");
        pushCluster(label);

        // Synthetic CHOICE node – created inside the cluster scope
        PathNode choiceNode = createSyntheticNode(PathNode.choice(node, nextId()));

        List<Fragment> altFragments = getFragments(alternatives);
        List<PathEdge> choiceEdges = new ArrayList<>();

        for (Fragment alt : altFragments) {
            for (PathNode entry : alt.entries()) {
                choiceEdges.add(addEdge(PathEdge.once(choiceNode, entry)));
            }
        }

        List<PathNode> exits = new ArrayList<>();
        for (Fragment alt : altFragments) {
            exits.addAll(alt.exits());
            choiceEdges.addAll(alt.internalEdges());
        }

        Fragment composite = new Fragment(
                Collections.singletonList(choiceNode),
                exits,
                choiceEdges
        );
        stack.push(composite);

        popCluster();
    }

    private List<Fragment> getFragments(Node[] nodes) {
        for (Node alt : nodes) {
            alt.visit(this);
        }

        // Pop in reverse order
        List<Fragment> fragments = new ArrayList<>(nodes.length);
        for (int i = 0; i < nodes.length; i++) {
            fragments.add(0, stack.pop());
        }
        return fragments;
    }

    // -------------------------------------------------------------------------
    // Repeat
    // -------------------------------------------------------------------------

    @Override
    public void visit(Repeat node) {
        int min = node.getMin();
        int max = node.getMax(); // -1 = unbounded

        String label = labelFor(node, "Repeat") + " \t\t {" + min + ".." + (max < 0 ? "<&infinity>" : max) + "}";
        pushCluster(label);

        // Synthetic REPEAT_ENTRY node – inside the cluster scope
        PathNode repeatEntry = createSyntheticNode(PathNode.repeatEntry(node, nextId()));

        // Visit body inside the same cluster scope
        node.getNode().visit(this);
        Fragment bodyFragment = stack.pop();

        List<PathEdge> repeatEdges = new ArrayList<>(bodyFragment.internalEdges());

        for (PathNode bodyEntry : bodyFragment.entries()) {
            repeatEdges.add(addEdge(PathEdge.repeat(repeatEntry, bodyEntry, min, max)));
        }

        for (PathNode bodyExit : bodyFragment.exits()) {
            repeatEdges.add(addEdge(PathEdge.once(bodyExit, repeatEntry)));
        }

        Fragment composite = new Fragment(
                Collections.singletonList(repeatEntry),
                Collections.singletonList(repeatEntry),
                repeatEdges
        );
        stack.push(composite);

        popCluster();
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void pushTerminal(Node astNode) {
        PathNode pathNode = createPathNode(astNode);
        stack.push(Fragment.terminal(pathNode));
    }

    /**
     * Derives a cluster label from the AST node's pattern text (if available)
     * or falls back to the supplied default kind name.
     */
    private static String labelFor(Node node, String kind) {
        // Node.getPattern() returns the regex sub-expression this node was parsed from.
        // Use it verbatim if available so the cluster header is self-documenting.
        String pattern = node.getPattern();
        if (pattern != null && !pattern.isEmpty()) {
            return kind + ": " + pattern;
        }
        return kind;
    }
}