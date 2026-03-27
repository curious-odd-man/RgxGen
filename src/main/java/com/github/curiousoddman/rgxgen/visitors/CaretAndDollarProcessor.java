package com.github.curiousoddman.rgxgen.visitors;


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
import com.github.curiousoddman.rgxgen.parsing.dflt.flags.ParsingFlags;

import java.util.ArrayList;
import java.util.List;

/**
 * A visitor that while visiting nodes should evaluate which nodes cannot be a part of generated text due to caret or dollar placement.
 * Examples:
 * (^a)+ -> only matches single `a` character. - remove `+`
 * (a$|c)x -> ax cannot be, only cx, - remove a$ and choice -> cx
 * x(a|^c) -> only xa is valid - remove ^c and choice -> xa
 */
public class CaretAndDollarProcessor implements NodeVisitor {

    /**
     * Tracks whether the subtree rooted at the last visited node is "poisoned" —
     * i.e., it contains a misplaced ^ or $ that makes it impossible to match
     * when combined with surrounding nodes.
     */
    private boolean containsStartOrEnd = false;

    /**
     * The transformed/replacement node produced by a visit. After visiting a node,
     * callers inspect this field to obtain the (possibly null) replacement.
     * null means "remove this node entirely from the tree".
     */
    private Node resultNode = null;

    // -----------------------------------------------------------------------
    // Public accessors used by the caller after visiting the root
    // -----------------------------------------------------------------------

    public Node getResultNode() {
        return resultNode;
    }

    public boolean isContainsStartOrEnd() {
        return containsStartOrEnd;
    }

    // -----------------------------------------------------------------------
    // Leaf nodes – no children to recurse into
    // -----------------------------------------------------------------------

    @Override
    public void visit(SymbolSet node) {
        // SymbolSet never contains ^ or $ anchors – propagate as-is.
        containsStartOrEnd = false;
        resultNode = node;
    }

    @Override
    public void visit(FinalSymbol node) {
        String value = node.getValue();
        ParsingFlags parsingFlags = node.getParsingFlags();
        if (parsingFlags.hasCaret() || parsingFlags.hasDollar()) {
            // This node IS the anchor – mark poisoned, remove from tree.
            containsStartOrEnd = true;
            resultNode = null;
        } else {
            containsStartOrEnd = false;
            resultNode = node;
        }
    }

    @Override
    public void visit(NotSymbol node) {
        containsStartOrEnd = false;
        resultNode = node;
    }

    @Override
    public void visit(GroupRef node) {
        containsStartOrEnd = false;
        resultNode = node;
    }

    // -----------------------------------------------------------------------
    // Composite nodes
    // -----------------------------------------------------------------------

    /**
     * Choice: (a$|c)x  →  only the branches that do NOT contain a misplaced
     * anchor survive. If only one branch survives, the Choice collapses to that
     * branch directly. If no branch survives, this whole node is removed.
     */
    @Override
    public void visit(Choice node) {
        Node[] alternatives = node.getNodes();
        List<Node> survivors = new ArrayList<>();

        for (Node alt : alternatives) {
            CaretAndDollarProcessor childProcessor = new CaretAndDollarProcessor();
            alt.visit(childProcessor);
            if (!childProcessor.isContainsStartOrEnd() && childProcessor.getResultNode() != null) {
                survivors.add(childProcessor.getResultNode());
            }
        }

        if (survivors.isEmpty()) {
            containsStartOrEnd = true;
            resultNode = null;
        } else if (survivors.size() == 1) {
            containsStartOrEnd = false;
            resultNode = survivors.get(0);
        } else {
            containsStartOrEnd = false;
            resultNode = new Choice(node.getPattern(), survivors.toArray(new Node[0]));
        }
    }

    /**
     * Sequence: visits each child in order. If any child contains a misplaced
     * anchor the whole sequence is poisoned; otherwise rebuild with surviving
     * (possibly transformed) children.
     */
    @Override
    public void visit(Sequence node) {
        Node[] children = node.getNodes();
        List<Node> rebuilt = new ArrayList<>();
        boolean anyPoisoned = false;

        for (Node child : children) {
            CaretAndDollarProcessor childProcessor = new CaretAndDollarProcessor();
            child.visit(childProcessor);

            if (childProcessor.isContainsStartOrEnd()) {
                anyPoisoned = true;
                break;
            }
            if (childProcessor.getResultNode() != null) {
                rebuilt.add(childProcessor.getResultNode());
            }
        }

        if (anyPoisoned) {
            containsStartOrEnd = true;
            resultNode = null;
        } else if (rebuilt.isEmpty()) {
            containsStartOrEnd = false;
            resultNode = null;
        } else if (rebuilt.size() == 1) {
            containsStartOrEnd = false;
            resultNode = rebuilt.get(0);
        } else {
            containsStartOrEnd = false;
            resultNode = new Sequence(node.getPattern(), rebuilt.toArray(new Node[0]));
        }
    }

    /**
     * Repeat: (^a)+ → the inner pattern can only ever match once (the ^ forces
     * start-of-input), so the repetition quantifier must be clamped to exactly 1.
     * If the inner node is poisoned entirely, remove the Repeat node too.
     */
    @Override
    public void visit(Repeat node) {
        CaretAndDollarProcessor childProcessor = new CaretAndDollarProcessor();
        node.getNode().visit(childProcessor);

        if (childProcessor.getResultNode() == null) {
            // Inner node was completely eliminated – remove Repeat too.
            containsStartOrEnd = childProcessor.isContainsStartOrEnd();
            resultNode = null;
            return;
        }

        Node innerResult = childProcessor.getResultNode();

        if (childProcessor.isContainsStartOrEnd()) {
            // The inner node has an anchor – repetition can only happen once.
            // Wrap it in a Repeat(1,1) to express "exactly once".
            containsStartOrEnd = false; // anchor is now "consumed" / neutralised
            resultNode = new Repeat(node.getPattern(), innerResult, 1, 1);
        } else {
            // No anchor inside – keep repetition unchanged.
            containsStartOrEnd = false;
            resultNode = new Repeat(node.getPattern(), innerResult, node.getMin(), node.getMax());
        }
    }

    /**
     * Group: visit the inner content and propagate. If the content is eliminated,
     * the group is eliminated too.
     */
    @Override
    public void visit(Group node) {
        CaretAndDollarProcessor childProcessor = new CaretAndDollarProcessor();
        node.getNode().visit(childProcessor);

        containsStartOrEnd = childProcessor.isContainsStartOrEnd();

        if (childProcessor.getResultNode() == null) {
            resultNode = null;
        } else {
            resultNode = new Group(node.getPattern(), node.getIndex(), childProcessor.getResultNode());
        }
    }
}
