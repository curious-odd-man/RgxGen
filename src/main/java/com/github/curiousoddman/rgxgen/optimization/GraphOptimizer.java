package com.github.curiousoddman.rgxgen.optimization;

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

import com.github.curiousoddman.rgxgen.lineages.PathGraph;
import com.github.curiousoddman.rgxgen.lineages.PathGraphBuilder;
import com.github.curiousoddman.rgxgen.lineages.PathNode;
import com.github.curiousoddman.rgxgen.lineages.PathEdge;
import com.github.curiousoddman.rgxgen.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.nodes.Node;

/**
 * Optimises the path graph by pruning impossible paths caused by {@code ^} and {@code $}
 * anchor nodes in semantically invalid positions.
 *
 * <p>The optimisation works at the AST level via {@link AstOptimizer}, then rebuilds the
 * {@link PathGraph} from the pruned AST.  This guarantees that both the graph-based diagram
 * output ({@code toPlantUml()}) and the AST-visitor-based generation ({@code iterateUnique()},
 * {@code generate()}) are consistent.
 *
 * <p>When the entire pattern is impossible (all branches dead), the returned graph contains
 * only {@code BEGIN → END}.
 */
public class GraphOptimizer {

    /**
     * The original pattern string (preserved for the graph title).
     */
    private final String originalPattern;

    public GraphOptimizer(String originalPattern) {
        this.originalPattern = originalPattern;
    }

    /**
     * Optimises the AST and returns the matching PathGraph.
     *
     * @param root the AST root produced by the parser
     * @return the optimised AST root (never {@code null}; may be a sentinel {@link FinalSymbol})
     *         and the corresponding PathGraph
     */
    public OptimizationResult optimize(Node root) {
        AstOptimizer astOpt = new AstOptimizer();
        Node optimisedRoot = astOpt.optimize(root);

        boolean isAllDead = (optimisedRoot == AstOptimizer.DEAD || optimisedRoot == null);

        PathGraph graph;
        Node effectiveRoot;

        if (isAllDead) {
            // Pattern matches nothing → minimal graph: BEGIN → END only
            graph = buildEmptyGraph(originalPattern);
            effectiveRoot = null;
        } else {
            effectiveRoot = optimisedRoot;
            graph = PathGraphBuilder.build(effectiveRoot, originalPattern);
        }

        return new OptimizationResult(effectiveRoot, graph, isAllDead);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a minimal graph containing only BEGIN and END with a direct edge between them.
     */
    private static PathGraph buildEmptyGraph(String pattern) {
        PathGraph graph = new PathGraph(pattern);
        PathNode begin = PathNode.begin(0);
        PathNode end = PathNode.end(1);
        graph.addNode(begin);
        graph.addNode(end);
        graph.addEdge(PathEdge.once(begin, end));
        return graph;
    }

    // -------------------------------------------------------------------------
    // Result holder
    // -------------------------------------------------------------------------

    /**
     * Holds the result of an optimisation pass.
     */
    public static class OptimizationResult {
        /** The optimised AST root, or {@code null} if the pattern matches nothing. */
        public final Node optimisedRoot;
        /** The PathGraph built from the optimised AST. */
        public final PathGraph graph;
        /** True when the entire pattern is impossible. */
        public final boolean allDead;

        public OptimizationResult(Node optimisedRoot, PathGraph graph, boolean allDead) {
            this.optimisedRoot = optimisedRoot;
            this.graph = graph;
            this.allDead = allDead;
        }
    }
}
