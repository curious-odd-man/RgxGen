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

import com.github.curiousoddman.rgxgen.nodes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Optimises a parsed regex AST by removing branches that are semantically impossible
 * due to {@code ^} (caret) and {@code $} (dollar) anchor nodes appearing in positions
 * where they can never match.
 *
 * <p>Anchor semantics:
 * <ul>
 *   <li>{@code ^} — asserts start-of-string; invalid if any real content precedes it.</li>
 *   <li>{@code $} — asserts end-of-string; invalid if any real content follows it.</li>
 * </ul>
 *
 * <p>The optimizer operates on the AST recursively.  Each call to
 * {@link #optimizeNode(Node, boolean, boolean)} receives the node to optimise plus two
 * boolean flags indicating the "context":
 * <ul>
 *   <li>{@code atStart} — true when nothing in the generated string can precede this node.</li>
 *   <li>{@code atEnd}   — true when nothing in the generated string can follow this node.</li>
 * </ul>
 *
 * <p>The return value is the optimised replacement node, or {@code null} if the node is
 * completely dead (produces nothing / is impossible).  Callers must handle {@code null}.
 */
public class AstOptimizer {

    /**
     * Sentinel node representing "this branch is impossible / dead".
     * Never inserted into the output AST; used only as an internal marker.
     */
    static final Node DEAD = new Node("DEAD") {
        @Override
        public void visit(com.github.curiousoddman.rgxgen.visitors.NodeVisitor visitor) {
            throw new UnsupportedOperationException("DEAD sentinel should never be visited");
        }
    };

    /**
     * Optimise the given AST root with full context (it is both at start and at end of the string).
     *
     * @param root the root of the AST produced by the parser
     * @return the optimised root, or {@code null} if the entire pattern matches nothing
     */
    public Node optimize(Node root) {
        return optimizeNode(root, true, true);
    }

    // -------------------------------------------------------------------------
    // Core recursive optimiser
    // -------------------------------------------------------------------------

    /**
     * Optimise a node given its positional context.
     *
     * @param node    node to optimise
     * @param atStart true if no content can appear before this node
     * @param atEnd   true if no content can appear after this node
     * @return optimised node (may be the same instance, a new node, or {@code null} = dead)
     */
    Node optimizeNode(Node node, boolean atStart, boolean atEnd) {
        if (node instanceof AnchorNode anchor) {
            return optimizeAnchor(anchor, atStart, atEnd);
        } else if (node instanceof Sequence seq) {
            return optimizeSequence(seq, atStart, atEnd);
        } else if (node instanceof Choice choice) {
            return optimizeChoice(choice, atStart, atEnd);
        } else if (node instanceof Repeat repeat) {
            return optimizeRepeat(repeat, atStart, atEnd);
        } else if (node instanceof Group group) {
            return optimizeGroup(group, atStart, atEnd);
        } else {
            // Leaf nodes (FinalSymbol, SymbolSet, NotSymbol, GroupRef) — always live
            return node;
        }
    }

    // -------------------------------------------------------------------------
    // AnchorNode
    // -------------------------------------------------------------------------

    private Node optimizeAnchor(AnchorNode anchor, boolean atStart, boolean atEnd) {
        if (anchor.isCaret()) {
            // ^ is valid only when atStart
            return atStart ? null : DEAD;  // null = "valid, produces nothing"
        } else {
            // $ is valid only when atEnd
            return atEnd ? null : DEAD;
        }
    }

    // -------------------------------------------------------------------------
    // Sequence
    // -------------------------------------------------------------------------

    /**
     * Optimises a Sequence by visiting each child with the correct positional context.
     *
     * <p>A child at position i is {@code atStart} only if all children before it are
     * zero-width (i.e., anchors or zero-width alternatives). Similarly {@code atEnd} only if
     * all children after it are zero-width.
     *
     * <p>If any child is DEAD the entire sequence is DEAD.
     */
    private Node optimizeSequence(Sequence seq, boolean atStart, boolean atEnd) {
        Node[] children = seq.getNodes();
        // First pass: determine which children can produce content (non-zero-width)
        // We need to know: for child[i], is anything before it capable of producing content?
        // and is anything after it capable of producing content?
        //
        // "produces content" = is not an anchor node (anchors are zero-width)
        // We treat null-return (anchor consumed) as zero-width.

        // Optimise children left to right; track whether we've seen any content-producing node.
        List<Node> optimised = new ArrayList<>(children.length);
        // Precompute: can each child produce content? We use the original children for this
        // (conservatively: any non-anchor node might produce content).
        boolean[] canProduceContent = computeCanProduceContent(children);

        for (int i = 0; i < children.length; i++) {
            boolean childAtStart = atStart && !anyContentBefore(canProduceContent, i);
            boolean childAtEnd = atEnd && !anyContentAfter(canProduceContent, i);

            Node result = optimizeNode(children[i], childAtStart, childAtEnd);
            if (result == DEAD) {
                // This child is impossible => entire sequence is dead
                return DEAD;
            }
            if (result != null) {
                // result == null means "anchor consumed cleanly" (zero-width, no node emitted)
                optimised.add(result);
            }
            // null => consumed zero-width (anchor that was valid); nothing added
        }

        if (optimised.isEmpty()) {
            // All children were valid anchors that produced nothing → empty sequence
            // Return a FinalSymbol("") to represent empty string
            return new FinalSymbol("");
        }
        if (optimised.size() == 1) {
            return optimised.get(0);
        }
        // Rebuild with surviving nodes and updated pattern
        String newPattern = buildSequencePattern(optimised);
        return new Sequence(newPattern, optimised.toArray(new Node[0]));
    }

    /**
     * Returns true if any child at index < i can produce content.
     */
    private boolean anyContentBefore(boolean[] canProduce, int i) {
        for (int j = 0; j < i; j++) {
            if (canProduce[j]) return true;
        }
        return false;
    }

    /**
     * Returns true if any child at index > i can produce content.
     */
    private boolean anyContentAfter(boolean[] canProduce, int i) {
        for (int j = i + 1; j < canProduce.length; j++) {
            if (canProduce[j]) return true;
        }
        return false;
    }

    /**
     * Conservatively determines which children can produce actual content (not purely anchors).
     */
    private boolean[] computeCanProduceContent(Node[] children) {
        boolean[] result = new boolean[children.length];
        for (int i = 0; i < children.length; i++) {
            result[i] = canProduceContent(children[i]);
        }
        return result;
    }

    /**
     * Returns true if the node might produce characters (i.e., is not purely an anchor or
     * a sequence/choice/repeat exclusively consisting of anchors).
     * This is a conservative approximation.
     */
    private boolean canProduceContent(Node node) {
        if (node instanceof AnchorNode) return false;
        if (node instanceof Repeat repeat) {
            if (repeat.getMin() == 0) return false; // optional, may produce nothing
            return canProduceContent(repeat.getNode());
        }
        if (node instanceof Group group) return canProduceContent(group.getNode());
        if (node instanceof Sequence seq) {
            for (Node child : seq.getNodes()) {
                if (canProduceContent(child)) return true;
            }
            return false;
        }
        if (node instanceof Choice choice) {
            for (Node alt : choice.getNodes()) {
                if (canProduceContent(alt)) return true;
            }
            return false;
        }
        return true;
    }

    // -------------------------------------------------------------------------
    // Choice
    // -------------------------------------------------------------------------

    /**
     * Optimises a Choice by pruning DEAD alternatives and removing anchors from live ones.
     *
     * <p>Each alternative is optimised with the same positional context as the choice itself.
     * If all alternatives die, the choice is DEAD.
     */
    private Node optimizeChoice(Choice choice, boolean atStart, boolean atEnd) {
        Node[] alternatives = choice.getNodes();
        List<Node> survivors = new ArrayList<>(alternatives.length);

        for (Node alt : alternatives) {
            Node result = optimizeNode(alt, atStart, atEnd);
            if (result != DEAD) {
                // result may be null (anchor-only alternative that produces empty string)
                // We map null → FinalSymbol("") to keep the choice structure
                survivors.add(result != null ? result : new FinalSymbol(""));
            }
        }

        if (survivors.isEmpty()) {
            return DEAD;
        }
        if (survivors.size() == 1) {
            return survivors.get(0);
        }
        String newPattern = buildChoicePattern(survivors);
        return new Choice(newPattern, survivors.toArray(new Node[0]));
    }

    // -------------------------------------------------------------------------
    // Repeat
    // -------------------------------------------------------------------------

    /**
     * Optimises a Repeat node.
     *
     * <p>Anchors inside a repeat are only valid on the first ({@code ^}) or last ({@code $})
     * iteration. The optimizer handles several cases:
     *
     * <ul>
     *   <li>If the body after stripping anchors is a Choice with some DEAD alternatives removed,
     *       we may need to split the repeat.</li>
     *   <li>{@code (^a)+} → exactly {@code a} once (repeat removed).</li>
     *   <li>{@code (b$)*} → {@code (b){0,1}} (max clamped to 1).</li>
     *   <li>{@code (a$|x){1,2}} → a$ exits to END on any iteration; x loops back.</li>
     *   <li>{@code (a$|x){2,2}} → split: x{1,1} then Choice(a|x).</li>
     * </ul>
     */
    private Node optimizeRepeat(Repeat repeat, boolean atStart, boolean atEnd) {
        int min = repeat.getMin();
        int max = repeat.getMax(); // -1 = unbounded
        Node body = repeat.getNode();

        // A caret inside a repeat is only valid on the first iteration (atStart=true, but only once).
        // A dollar inside a repeat is only valid on the last iteration (atEnd=true, but only once).
        //
        // We optimise the body for the "middle" iteration context (not at start, not at end of string).
        // Then for the first iteration (atStart context) and last iteration (atEnd context) separately.

        // Body for middle iterations: not at start (unless outer is atStart AND this is the first
        // visit which only happens once), not at end.
        Node bodyForMiddle = optimizeNode(body, false, false);
        // Body for first iteration (only): atStart context, not at end
        Node bodyForFirst = optimizeNode(body, atStart, false);
        // Body for last iteration: not at start, atEnd context
        Node bodyForLast = optimizeNode(body, false, atEnd);
        // Body for both first AND last (single iteration): atStart and atEnd
        Node bodyForSingle = optimizeNode(body, atStart, atEnd);

        // Determine structure:
        // - Does a caret anchor exist inside the body?
        boolean hasCaretInBody = containsAnchor(body, true);
        // - Does a dollar anchor exist inside the body?
        boolean hasDollarInBody = containsAnchor(body, false);

        // Case 1: No anchors in body → straightforward
        if (!hasCaretInBody && !hasDollarInBody) {
            if (bodyForMiddle == DEAD) return DEAD;
            if (bodyForMiddle == null) {
                // Body produces nothing → repeat of nothing → empty string
                return new FinalSymbol("");
            }
            if (bodyForMiddle == body) return repeat; // unchanged
            String newPattern = buildRepeatPattern(bodyForMiddle, min, max);
            return new Repeat(newPattern, bodyForMiddle, min, max);
        }

        // Case 2: Only caret in body (no dollar)
        if (hasCaretInBody && !hasDollarInBody) {
            return optimizeRepeatWithCaret(repeat, body, bodyForFirst, bodyForMiddle, bodyForSingle, atStart, atEnd, min, max);
        }

        // Case 3: Only dollar in body (no caret)
        if (!hasCaretInBody) {
            return optimizeRepeatWithDollar(repeat, body, bodyForLast, bodyForMiddle, bodyForSingle, atStart, atEnd, min, max);
        }

        // Case 4: Both caret and dollar in body → very complex; handle like dollar for now
        // (caret + dollar inside a repeat: the single-pass body is bodyForSingle)
        return optimizeRepeatWithDollar(repeat, body, bodyForLast, bodyForMiddle, bodyForSingle, atStart, atEnd, min, max);
    }

    /**
     * Handles repeat where body contains {@code ^}.
     *
     * <p>^ is only valid on the first iteration. So:
     * <ul>
     *   <li>If min > 1 or max > 1: second+ iterations cannot use ^-containing paths.
     *       Split into first-iteration (with ^-paths live) + rest (without ^-paths).</li>
     *   <li>If max == 1 or (min == 1 && max == 1): ^ is only used once, no problem.</li>
     *   <li>If bodyForMiddle is DEAD (only ^ paths exist, nothing else): collapse to 1 iteration.</li>
     * </ul>
     */
    private Node optimizeRepeatWithCaret(Repeat repeat, Node originalBody,
                                          Node bodyForFirst, Node bodyForMiddle, Node bodyForSingle,
                                          boolean atStart, boolean atEnd,
                                          int min, int max) {
        // If nothing can loop (bodyForMiddle is DEAD or null), repeat collapses to single iteration
        boolean middleIsDead = (bodyForMiddle == DEAD || bodyForMiddle == null);

        if (middleIsDead) {
            // Only the first iteration is possible
            // Result: exactly 1 occurrence using bodyForSingle
            if (bodyForSingle == DEAD) return DEAD;
            if (bodyForSingle == null) return new FinalSymbol("");
            return bodyForSingle;
        }

        // Middle iterations exist: we need to handle the ^ only on first iteration
        if (!atStart) {
            // We're not at the actual start of the string → ^ is always dead in this context
            // Use bodyForMiddle for all iterations (^ branches are already stripped by bodyForMiddle)
            if (bodyForMiddle == DEAD) return DEAD;
            String newPattern = buildRepeatPattern(bodyForMiddle, min, max);
            return new Repeat(newPattern, bodyForMiddle, min, max);
        }

        // atStart == true: first iteration can use bodyForFirst (with ^ live),
        // subsequent iterations use bodyForMiddle (^ stripped).
        // Only need to split if max != 1 (more than one possible iteration)
        if (max == 1 || (min == 1 && max == 1)) {
            // Only one iteration, bodyForFirst (= bodyForSingle since atEnd might not matter here)
            // Actually we need bodyForSingle to account for atEnd as well
            if (bodyForSingle == DEAD) return DEAD;
            if (bodyForSingle == null) return new FinalSymbol("");
            String newPat = buildRepeatPattern(bodyForSingle, min, max);
            return new Repeat(newPat, bodyForSingle, min, max);
        }

        // Multiple iterations possible.
        // First iteration: bodyForFirst (^ live)
        // Subsequent iterations (min-1 to max-1): bodyForMiddle (^ stripped)
        //
        // Structure: bodyForFirst then Repeat(bodyForMiddle){min-1, max-1}
        // But if bodyForFirst == bodyForMiddle (no caret effect), just return normal repeat
        if (bodyForFirst == DEAD) return DEAD;
        if (bodyForFirst == null) bodyForFirst = new FinalSymbol("");

        // Build: firstBody + repeat(middleBody){min-1, max-1}
        int newMin = Math.max(0, min - 1);
        int newMax = max == Repeat.UNBOUNDED ? Repeat.UNBOUNDED : max - 1;

        List<Node> seqParts = new ArrayList<>();
        seqParts.add(bodyForFirst);

        if (newMax != 0) {
            // There can be more iterations
            String middleRepeatPattern = buildRepeatPattern(bodyForMiddle, newMin, newMax);
            Node middleRepeat = new Repeat(middleRepeatPattern, bodyForMiddle, newMin, newMax);
            seqParts.add(middleRepeat);
        }
        // else: max was 1, no more iterations; handled above

        if (seqParts.size() == 1) return seqParts.get(0);
        String seqPattern = buildSequencePattern(seqParts);
        return new Sequence(seqPattern, seqParts.toArray(new Node[0]));
    }

    /**
     * Handles repeat where body contains {@code $}.
     *
     * <p>$ is only valid on the last iteration. So:
     * <ul>
     *   <li>If max > 1: non-last iterations cannot use $-containing paths.
     *       Split into body-without-$ repeated, then final choice with $.</li>
     *   <li>If max == 1: $ is only used on the only iteration, no problem.</li>
     *   <li>If bodyForMiddle is DEAD (only $ paths exist, nothing else): clamp max to 1.</li>
     * </ul>
     */
    private Node optimizeRepeatWithDollar(Repeat repeat, Node originalBody,
                                           Node bodyForLast, Node bodyForMiddle, Node bodyForSingle,
                                           boolean atStart, boolean atEnd,
                                           int min, int max) {
        boolean middleIsDead = (bodyForMiddle == DEAD || bodyForMiddle == null);

        if (middleIsDead) {
            // Only one effective iteration possible (the last one)
            // Clamp to {0,1} or {1,1}
            if (bodyForSingle == DEAD) return DEAD;
            if (bodyForSingle == null) return new FinalSymbol("");
            int newMin = Math.min(min, 1);
            int newMax = 1;
            if (newMin == newMax && newMin == 1) {
                return bodyForSingle; // Just the body, no repeat needed
            }
            String newPat = buildRepeatPattern(bodyForSingle, newMin, newMax);
            return new Repeat(newPat, bodyForSingle, newMin, newMax);
        }

        // Middle can loop. $ only valid on last iteration.
        if (!atEnd) {
            // Not at end of string → $ is always dead in this context
            if (bodyForMiddle == DEAD) return DEAD;
            String newPattern = buildRepeatPattern(bodyForMiddle, min, max);
            return new Repeat(newPattern, bodyForMiddle, min, max);
        }

        // atEnd == true: last iteration can use bodyForLast (with $ live),
        // non-last iterations use bodyForMiddle ($ stripped).

        if (max == 1 || (min == 1 && max == 1)) {
            // Only one iteration possible → use bodyForSingle
            if (bodyForSingle == DEAD) return DEAD;
            if (bodyForSingle == null) return new FinalSymbol("");
            String newPat = buildRepeatPattern(bodyForSingle, min, max);
            return new Repeat(newPat, bodyForSingle, min, max);
        }

        // Multiple iterations. Split:
        // - First (min-1) iterations: bodyForMiddle ($ stripped), repeat {min-1, max-1}
        //   But if min==max (exact), then exactly min-1 middle iterations + 1 last iteration
        // - Last iteration: bodyForLast ($ live)

        if (bodyForLast == DEAD) {
            // Last iteration is dead, so $ alternative is not accessible at end
            // Use only middle body
            String newPattern = buildRepeatPattern(bodyForMiddle, min, max);
            return new Repeat(newPattern, bodyForMiddle, min, max);
        }
        if (bodyForLast == null) bodyForLast = new FinalSymbol("");

        // For exact repeat {n,n}: split into {n-1} middle + 1 last
        if (min == max && min > 1) {
            // Split: Repeat(middleBody){min-1, min-1} + bodyForLast
            int n = min - 1;
            List<Node> seqParts = new ArrayList<>();
            if (n > 0) {
                String midPat = buildRepeatPattern(bodyForMiddle, n, n);
                seqParts.add(new Repeat(midPat, bodyForMiddle, n, n));
            }
            seqParts.add(bodyForLast);
            if (seqParts.size() == 1) return seqParts.get(0);
            String seqPat = buildSequencePattern(seqParts);
            return new Sequence(seqPat, seqParts.toArray(new Node[0]));
        }

        // For {min, max} where max > min or max is unbounded:
        // Middle iterations: {min-1, max-1} or {min, max-1}... 
        // Actually for {min, max}, the last iteration is mandatory only if min == max.
        // For {min, max} with min < max: the last iteration might or might not occur.
        // The $ alternative should be available in any iteration that could be the last one,
        // i.e., we need the repeat to use bodyForLast as an option that can exit to END.
        //
        // The cleanest model: use bodyForMiddle for all non-last traversals, but allow
        // bodyForLast on any iteration that could terminate.
        // This is equivalent to: Repeat(middleBody){min, max} where the exit edge goes through bodyForLast.
        //
        // For the PathGraph this means: $ alternatives connect directly to END instead of looping.
        // For the AST generation (iterateUnique/generate), we need a different structure.
        //
        // The expected unique values give us the key:
        // (1$|1,){0,1}(2$|2,){0,1} → ["1","1,","1,2","1,2,","2","2,"]
        // This means 1$ and 2$ are fully live. The optimizer should keep them but remove DEAD ones.
        //
        // For {min,max} non-exact: keep both options; the $ one can only fire on last iteration.
        // We model this as: middle body repeat {min-1, max-1} + bodyForLast (mandatory last step)
        // OR, for the optional case: optionally do {0, max-1} middle iterations then bodyForLast
        // OR just do 0..max middle iterations.
        //
        // The simplest correct model for generation purposes:
        // Repeat(bodyForMiddle){0, max-1} then bodyForLast  [if min >= 1]
        // But that changes semantics for min > 1...
        //
        // Let's look at what the spec says for DEAD_BRANCHES_REPEAT_DOLLAR: (1$|1,){0,1}(2$|2,)
        // First group: 1$ dead (followed by mandatory second group), only 1, survives → (1,){0,1}
        // Second group: 2$ is live (it's at the end), 2, also live.
        //
        // So for (1$|1,){0,1}: atEnd=false (second group follows) → 1$ dead → only 1, survives
        // For (2$|2,): atEnd=true → 2$ live, 2, live → keep both
        //
        // The key: for groups followed by something, atEnd=false, so $ is always dead in non-last groups.
        // For the last group (atEnd=true), $ is live.
        //
        // For LIVE_BRANCHES_REPEAT_DOLLAR: (1$|1,){0,1}(2$|2,){0,1}
        // Both groups are optional. atEnd=true for both (each could be the last thing).
        // 1$ is live if the second group produces nothing (which it can, since {0,1}).
        // Hmm, but then atEnd for the first group would be true only sometimes...
        //
        // The approach: treat atEnd conservatively. If the next sibling might produce nothing
        // (e.g., is optional), then the current node might be atEnd.
        //
        // This complexity means we handle the non-exact repeat case differently:
        // Keep the $ alternative in the body; it will exit to END via the PathGraph.
        // For AST-based generation, we keep bodyForLast as-is ($ removed but the branch lives).

        // General case: build Repeat(middleBody){min, max-1} + bodyForLast for {min,max}
        // But for optional repeats, the middle repeat should be {0, max-1}:
        int middleMin = Math.max(0, min - 1);
        int middleMax = max == Repeat.UNBOUNDED ? Repeat.UNBOUNDED : max - 1;

        // If there must be at least 1 middle iteration (min > 1), enforce it
        if (min > 1) {
            middleMin = min - 1;
        } else {
            middleMin = 0;
        }

        List<Node> seqParts = new ArrayList<>();
        if (middleMax != 0) {
            String midPat = buildRepeatPattern(bodyForMiddle, middleMin, middleMax);
            seqParts.add(new Repeat(midPat, bodyForMiddle, middleMin, middleMax));
        }
        seqParts.add(bodyForLast);

        if (seqParts.size() == 1) return seqParts.get(0);
        String seqPat = buildSequencePattern(seqParts);
        return new Sequence(seqPat, seqParts.toArray(new Node[0]));
    }

    // -------------------------------------------------------------------------
    // Group
    // -------------------------------------------------------------------------

    private Node optimizeGroup(Group group, boolean atStart, boolean atEnd) {
        Node optimisedChild = optimizeNode(group.getNode(), atStart, atEnd);
        if (optimisedChild == DEAD) return DEAD;
        if (optimisedChild == null) {
            // Group with only anchors → empty group → empty string
            return new FinalSymbol("");
        }
        if (optimisedChild == group.getNode()) return group; // unchanged
        String newPattern = "(" + optimisedChild.getPattern() + ")";
        return new Group(newPattern, group.getIndex(), optimisedChild);
    }

    // -------------------------------------------------------------------------
    // Utility: pattern-string builders
    // -------------------------------------------------------------------------

    private String buildSequencePattern(List<Node> nodes) {
        StringBuilder sb = new StringBuilder();
        for (Node n : nodes) {
            sb.append(n.getPattern());
        }
        return sb.toString();
    }

    private String buildChoicePattern(List<Node> survivors) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < survivors.size(); i++) {
            if (i > 0) sb.append("|");
            sb.append(survivors.get(i).getPattern());
        }
        sb.append(")");
        return sb.toString();
    }

    private String buildRepeatPattern(Node body, int min, int max) {
        String bodyPat = body.getPattern();
        String quantifier;
        if (min == 0 && max == Repeat.UNBOUNDED) {
            quantifier = "*";
        } else if (min == 1 && max == Repeat.UNBOUNDED) {
            quantifier = "+";
        } else if (min == 0 && max == 1) {
            quantifier = "?";
        } else if (min == max) {
            quantifier = "{" + min + "," + min + "}";
        } else {
            String maxStr = (max == Repeat.UNBOUNDED) ? "" : String.valueOf(max);
            quantifier = "{" + min + "," + maxStr + "}";
        }
        return bodyPat + quantifier;
    }

    // -------------------------------------------------------------------------
    // Utility: anchor detection
    // -------------------------------------------------------------------------

    /**
     * Returns true if the given node contains an anchor of the specified type anywhere in its tree.
     *
     * @param node    node to inspect
     * @param isCaret true to look for {@code ^}; false to look for {@code $}
     */
    boolean containsAnchor(Node node, boolean isCaret) {
        if (node instanceof AnchorNode anchor) {
            return isCaret ? anchor.isCaret() : anchor.isDollar();
        }
        if (node instanceof Sequence seq) {
            for (Node child : seq.getNodes()) {
                if (containsAnchor(child, isCaret)) return true;
            }
        } else if (node instanceof Choice choice) {
            for (Node alt : choice.getNodes()) {
                if (containsAnchor(alt, isCaret)) return true;
            }
        } else if (node instanceof Repeat repeat) {
            return containsAnchor(repeat.getNode(), isCaret);
        } else if (node instanceof Group group) {
            return containsAnchor(group.getNode(), isCaret);
        }
        return false;
    }
}
