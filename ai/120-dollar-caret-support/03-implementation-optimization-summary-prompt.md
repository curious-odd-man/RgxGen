# RgxGen — Graph Optimization Reference

> **Branch:** `120.Generate-produces-invalid-strings-when-dollar-and-caret-inside-pattern`  
> **Purpose:** Reference document for implementing `GraphOptimizer` — a class that prunes impossible
> paths from the regex node graph caused by `^` (caret) and `$` (dollar) anchors appearing in the
> middle of a pattern.

---

## 1. Project Overview

**RgxGen** is an open-source Java library that parses a regex pattern into a node tree (DAG), then
walks it to generate strings. Core entry point:

```java
RgxGen rgxGen = RgxGen.parse("[^0-9]*[12]?[0-9]{1,2}[^0-9]*");
String s = rgxGen.generate();          // random matching string
String nm = rgxGen.generateNotMatching(); // random non-matching string
StringIterator it = rgxGen.iterateUnique(); // unique values iterator
```

**Repository:** https://github.com/curious-odd-man/RgxGen

---

## 2. Architecture

```
regex string
    │
    ▼[03-implementation-optimization-summary-prompt.md](03-implementation-optimization-summary-prompt.md)
DefaultTreeBuilder          (parsing/dflt/DefaultTreeBuilder.java)
    │  parses regex → builds Node tree
    ▼
Node tree (DAG)             (nodes/ package)
    │  visited by Visitors
    ▼
GenerationVisitor           (visitors/)
    │  walks the tree, generates output
    ▼
String
```

Key packages:

| Package                                        | Role                                        |
|------------------------------------------------|---------------------------------------------|
| `com.github.curiousoddman.rgxgen`              | Entry point `RgxGen`                        |
| `com.github.curiousoddman.rgxgen.nodes`        | Node types that form the DAG                |
| `com.github.curiousoddman.rgxgen.parsing.dflt` | Tree builder, parser, constants             |
| `com.github.curiousoddman.rgxgen.visitors`     | Generation, counting, not-matching visitors |
| `com.github.curiousoddman.rgxgen.config`       | `RgxGenOption`, `RgxGenProperties`          |
| `com.github.curiousoddman.rgxgen.model`        | `SymbolRange`, `UnicodeCategory`            |

---

## 3. Node Types

The graph vertices are instances of node classes from `src/main/java/com/github/curiousoddman/rgxgen/nodes/`.

From the JAR class listing (v1.4 as baseline; later versions add more):

| Class         | Regex concept                                                  | Notes                                                 |
|---------------|----------------------------------------------------------------|-------------------------------------------------------|
| `Node`        | Abstract base                                                  | All nodes extend this                                 |
| `FinalSymbol` | Literal character(s)                                           | e.g. `a`, `abc`, `\t`                                 |
| `Choice`      | Alternation `(a\|b\|c)`                                        | Holds array of child nodes, one chosen per generation |
| `Group`       | Group `(...)` / named group `(?<n>...)`                        | Wraps a child node; may be referenced via `GroupRef`  |
| `GroupRef`    | Back-reference `\1`                                            | Points to a previously generated `Group` value        |
| `NotSymbol`   | Not matching wrapper node                                      | Generates a string NOT matching underlying nodes      |
| `Repeat`      | Quantifiers `?`, `+`, `*`, `{n}`, `{n,m}`                      | Wraps a child node; tracks min/max repetitions        |
| `SymbolSet`   | Character class `[...]`, dot '.', char classes (`\d`, `\s`...) | Generates a character from the specified set          |
| `AnchorNode`  | `^` caret  or  `$` dollar                                      | Asserts position at start or end of line/string       |
| `Sequence`    | A container for sequence of nodes                              | Sequentially placed nodes                             | 

### Graph edges

An edge `A → B` means **"B is used after A"** (sequential flow). In terms of the node tree this
means `B` is the next sibling in a sequence, or the next node following a group/repeat.

Implicit sentinel nodes (may exist in the graph):

- **`begin`** — virtual start node representing position before the first character
- **`end`** — virtual end node representing position after the last character

---

## 4. The Core Problem (Issue #120)

### What goes wrong

The library currently **ignores `^` and `$`** during generation.
When they appear inside an alternation, the parser still produces all syntactic branches — including branches that are *
*semantically impossible** at
generation time.

### Worked example

Pattern: `(a$|c)x`

The parser builds the following paths through the graph:

```
Path 1:  begin → 'a' → '$' → 'x' → end
Path 2:  begin → 'c' → 'x' → end
```

**Path 1 is impossible.** After `$` (end-of-string anchor) there cannot follow `x`. Generating
from Path 1 produces the string `"ax"` which does NOT match the original regex. This is the bug.

### Other problematic cases

| Pattern        | Impossible path      | Reason                                   |
|----------------|----------------------|------------------------------------------|
| `(a$\|c)x`     | `a` → `$` → `x`      | `$` followed by non-end node             |
| `x(a$\|b)y`    | `a` → `$` → `y`      | same                                     |
| `x(^a\|b)`     | `x` → `^` → `a`      | `^` preceded by non-begin node           |
| `(^a)+`        | `^` → `a` → `^`→ `a` | when more than 1 repetition is generated | 
| `(a$\|x){1,2}` | `a` → `$` → `x`      | repetition with alterations              |

---

## 5. Test Infrastructure

### Test class

```
src/test/java/com/github/curiousoddman/rgxgen/lineages/GraphOptimizationTests.java
```

This class presumably:

- Parses various regex patterns
- Builds the node graph
- Runs the graph optimizer
- Compares the resulting optimized graph against expected graphs stored as resource files

### Expected graph resources

```
testdata/dollar-and-caret/<enum name>.puml
```

Each file describes the expected optimized graph for a test case. The format is PlantUml file.
The comment in the brief says *"Some of them are not entirely correct — I
need more thoughts on that"* — meaning some expected outputs may need revision once the optimizer
logic is firmed up.

---

## 6. Graph Model for Optimization

Before implementing, it helps to think of the node tree as a directed graph:

```
Nodes  = regex expression parts (Node instances)
Edges  = "used after" relation: A → B means B follows A in the generated string
Paths  = sequences from 'begin' to 'end'
```

### Anchor semantics in graph terms

| Anchor          | Valid predecessors                                                   | Valid successors                     |
|-----------------|----------------------------------------------------------------------|--------------------------------------|
| `^` (LineStart) | Only `begin` (or nothing — it must be the first meaningful position) | Any node                             |
| `$` (LineEnd)   | Any node                                                             | Only `end` (nothing real may follow) |

A path is **impossible** if:

- It contains `$` and has any non-`end` node after it, OR
- It contains `^` at a position that is not the graph start (i.e., has real predecessors before it)

---

## 7. Approaches to Graph Optimization

### Approach A — Post-parse Graph Pruning (Recommended starting point)

**Idea:** After the tree is built, traverse it and mark or remove nodes/edges that create
impossible paths due to anchor placement.

**Algorithm sketch:**

```
1. Build the full node graph as normal (existing behavior).
2. Run GraphOptimizer.optimize(rootNode):
   a. DFS/BFS through all paths from 'begin' to 'end'.
   b. For each path, check for violations:
      - A '$' node that has a non-terminal successor → mark path as DEAD.
      - A '^' node that has a non-begin predecessor → mark path as DEAD.
   c. For each DEAD path:
      - Remove or disable the branch (e.g., remove the alternative from a Choice node).
3. If a Choice node has all its alternatives removed → the whole Choice is impossible
   (this itself may cascade upward).
```

**Pros:**

- Cleanest separation from parsing
- Works on already-built tree; no parser changes needed
- Fits naturally into the visitor pattern already used in the codebase

**Cons:**

- Need to re-represent the tree as a graph with explicit edges, or annotate nodes with
  successor/predecessor info

---

### Approach C — Rewrite as Explicit Graph + Reachability Analysis

**Idea:** Fully convert the node tree into an explicit directed graph (adjacency list), then run
standard graph algorithms (e.g., dead-end elimination, reachability from `begin`, reverse
reachability from `end`) to find and remove impossible nodes.

**Algorithm sketch:**

```
1. Convert Node tree → DirectedGraph<Node> with explicit edges.
2. Add virtual 'begin' and 'end' nodes.
3. For '$':
   - Remove all outgoing edges except to 'end'.
4. For '^':
   - Remove all incoming edges except from 'begin'.
5. Run forward reachability from 'begin' → nodes not reachable are dead.
6. Run backward reachability from 'end' → nodes with no path to 'end' are dead.
7. Dead nodes and their edges are removed.
8. Convert back to Node tree (or use graph directly for generation).
```

**Pros:**

- Most correct and complete — handles chains of impossible nodes
- Graph algorithms well-understood
- Can handle complex nested cases

**Cons:**

- Most work to implement
- Requires either a round-trip tree↔graph conversion, or changing the generation
  layer to work directly on the graph

---

## 8. Recommended Approach and Implementation Plan

Based on the existing visitor pattern and the test structure, **Approach A (Post-parse Graph
Pruning)** is the best starting point, possibly upgraded toward **Approach C** if simple pruning
misses edge cases.

### Proposed class: `GraphOptimizer`

```java
package com.github.curiousoddman.rgxgen.optimization;

public class GraphOptimizer {

    /**
     * Optimize the node graph by removing impossible paths caused by
     * ^ and $ anchor nodes appearing in semantically invalid positions.
     *
     * @param root the root node of the parsed regex graph
     * @return the root of the optimized graph (may be same instance or a new tree)
     */
    public Node optimize(Node root) { ...}

    /**
     * Determine whether a given sequence (list of nodes in a path) 
     * is valid with respect to anchor placement.
     */
    private boolean isPathValid(List<Node> path) { ...}

    /**
     * Recursively prune Choice nodes whose alternatives are all impossible.
     */
    private Node pruneChoiceNode(Choice choice) { ...}
}
```

### Key rules to encode

```java
// Rule 1: $ must only be followed by end-of-path (no real nodes after it)
boolean dollarIsTerminal(List<Node> nodesAfterDollar) {
    return nodesAfterDollar.isEmpty() || allAreZeroWidth(nodesAfterDollar);
}

// Rule 2: ^ must only be preceded by start-of-path (no real nodes before it)
boolean caretIsAtStart(List<Node> nodesBeforeCaret) {
    return nodesBeforeCaret.isEmpty() || allAreZeroWidth(nodesBeforeCaret);
}
```

---

## 9. Edge Cases and Open Questions

These are the cases that may make some expected test resource files uncertain:

| Case            | Comment                                                                                                                                                                                          | Answer                                                              |
|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| `(a$\|b$)x`     | Both alternatives of Choice have `$` before `x`.                                                                                                                                                 | Whole pattern should be considered invalid - an exception is thrown |  
| `^(a\|^b)`      | Nested `^` — the inner `^` is at a position that may or may not be valid depending on whether `(a\|^b)` can ever start from begin                                                                | Valid case. Both values `a` and `b` can be produced.                |              
| `(a$)?b`        | The `?` makes the `a$` optional. If it matches zero times, `b` can follow. If it matches once, `b` cannot follow. → the optimizer must treat `{1}` occurrence of the group as an impossible path | Completely remove node with `a$`                                    |
| `(a$\|)b`       | The empty alternative makes one path valid. The optimizer should keep the empty branch.                                                                                                          |                                                                     |
| Multi-line mode | If `(?m)` is ever supported, `^` and `$` match line boundaries, not just string boundaries. This changes the semantics entirely.                                                                 | Ignore for now                                                      |
| `\b` and `\B`   | Currently ignored by the library. Similar zero-width assertion problem — leave out of scope for now.                                                                                             | Ignore for now                                                      |
| `(a\|^x){1,2}`  | First may choose, second iteration - only `a` is allowed.                                                                                                                                        | Should be transformed to `(a\|x)(a){0,1}`? or otherwise optimized   |

---

## 10. Files to Create / Modify

| File                                                                                 | Action                                             |
|--------------------------------------------------------------------------------------|----------------------------------------------------|
| `src/main/java/com/github/curiousoddman/rgxgen/optimization/GraphOptimizer.java`     | **Create** — main class                            |
| `src/test/java/com/github/curiousoddman/rgxgen/lineages/GraphOptimizationTests.java` | **Already exists** — add/verify test cases         |
| `testdata/dollar-and-caret/<pattern>.puml`                                           | **Review / fix** expected optimized graph files    |
| `src/main/java/com/github/curiousoddman/rgxgen/RgxGen.java`                          | Possibly **modify** to run optimizer after parsing |

---

## 11. Relevant Existing Patterns in Codebase

- **Visitor pattern:** The codebase uses `GenerationVisitor`, `NotMatchingGenerationVisitor`,
  `UniqueGenerationVisitor`, `UniqueValuesCountingVisitor`. These are currently used to generate patterns from Node trees.
- **`PathGraphBuilder`:** a visitor that creates a graph representation for node tree. Understanding its output
  structure is essential before writing the optimizer. 
- **`DefaultTreeBuilder`:** Builds the node tree from the regex string. 
 
---

## 12. Quick Reference: Anchor Rules Summary

```
VALID paths:
  begin → [nodes] → end
  begin → ^ → [nodes] → end          ← ^ at start is fine
  begin → [nodes] → $ → end          ← $ at end is fine
  begin → ^ → [nodes] → $ → end      ← both anchors, fully bounded

INVALID paths (must be pruned):
  begin → [nodes] → $ → [more nodes] → end   ← $ not at end
  begin → [nodes] → ^ → [nodes] → end        ← ^ not at start

In a Choice (a$|c)x:
  Alternative 1:  ... → 'a' → '$' → 'x' ...   INVALID ($ followed by 'x')
  Alternative 2:  ... → 'c' → 'x' ...          VALID
  → Prune alternative 1, keep alternative 2
```

---

*Document prepared for branch `#120.Generate-produces-invalid-strings-when-dollar-and-caret-inside-pattern`.*  
*Node type names should be verified against actual source files
in `src/main/java/com/github/curiousoddman/rgxgen/nodes/` before implementation.*