# RgxGen – Graph Optimization for `^` / `$` Anchors

## Context

**Library:** [RgxGen](https://github.com/curious-odd-man/RgxGen)  
**Purpose:** Java library that generates random strings matching (or not matching) a given regex pattern.  
**Active branch:** `#120.Generate-produces-invalid-strings-when-dollar-and-caret-inside-pattern`  
**Issue:** When `^` (caret) or `$` (dollar) appear *inside* a pattern (not just at the very start/end), some generated
strings are structurally invalid because the generator can follow paths that are impossible at the regex-semantics
level.

---

## The Problem

### Regex anchors as graph nodes

RgxGen parses a regex into a **directed graph** where:

- **Vertices** = regex expression parts (literals, groups, character classes, anchors, etc.)
- **Edges** = "used after" relation: edge `A → B` means node B follows node A in the generation sequence.
- Two special implicit nodes exist: **`begin`** and **`end`** (representing the start and end of the generated string).

### What `^` and `$` mean semantically

| Symbol | Meaning (non-multiline mode)               |
|--------|--------------------------------------------|
| `^`    | Assert position at the **start of string** |
| `$`    | Assert position at the **end of string**   |

In multiline mode (`(?m)`) they match start/end of a *line*, but even then no character-generating node may follow `$`
on the same line unless the "line" ends there.

### How invalid paths arise

Consider the pattern `(a$|c)x`:

```
begin → [choice]
           ├─ a$ → x → end    ← INVALID: $ asserts end-of-string but x follows
           └─ c  → x → end    ← valid
```

Path 1 is syntactically representable in the graph but semantically impossible. The generator does not currently
understand this and may pick path 1, producing strings like `"ax"` — which does **not** match the original pattern.

Similarly for `^`:

- `(a|^b)c` has a path `begin → a → ^b → c → end` where `^b` asserts start-of-string in the middle — impossible unless
  `a` produces zero characters (and even then the caret position is after `begin`, not after `a`).

### General rule

- **After a `$` node**, no character-producing node may appear in the same path (the path must lead directly to `end`).
- **Before a `^` node**, no character-producing node may appear in the same path (the path must start directly from
  `begin`).
- Paths that violate these rules are **dead paths** and must be eliminated (or never generated).

---

## Relevant Files (branch `#120…`)

| File                                                   | Role                                                                        |
|--------------------------------------------------------|-----------------------------------------------------------------------------|
| `src/test/java/…/lineages/GraphOptimizationTests.java` | Test class defining optimization scenarios                                  |
| `src/test/resources/`                                  | Expected optimized graph snapshots (some acknowledged as not fully correct) |

---

## Key Concepts to Carry Into Implementation

### Graph model

- A graph has a set of **nodes** (each wrapping a regex element) and **directed edges**.
- There are sentinel nodes `BEGIN` and `END`.
- Paths are sequences of nodes from `BEGIN` to `END`.
- A path is **valid** if it respects all anchor constraints.

### Node classification

Nodes fall into (at minimum) these categories:

| Category            | Examples                                        | Produces chars? | Can precede `$`?    | Can follow `^`?       |
|---------------------|-------------------------------------------------|-----------------|---------------------|-----------------------|
| Character-producing | literal, `.`, `\d`, `[a-z]`, group with content | Yes             | No                  | No                    |
| Structural          | `BEGIN`, `END`, empty alternative branch        | No              | Yes                 | Yes                   |

### What "optimization" means

Remove (or mark unreachable) any node or edge that would make every path through them invalid. After optimization:

- No reachable path goes through `$` followed by a character-producing node.
- No reachable path goes through a character-producing node followed by `^`.
- If an alternative group's entire branch is invalid, the branch is removed; if all branches are invalid, the group (and
  paths through it) is removed.

---

## Approaches

### Approach 1 — Path Enumeration + Filtering (simple, may not scale)

1. Enumerate all paths from `BEGIN` to `END`.
2. For each path, check validity (no char-node after `$`, no char-node before `^`).
3. Remove edges that only appear in invalid paths.

**Pros:** Straightforward, easy to reason about correctness.  
**Cons:** Exponential in the number of alternatives; not suitable for patterns with many branches.

---

### Approach 2 — Forward/Backward Reachability Analysis (recommended)

Run two graph passes:

#### Pass A — Forward pass: propagate "has seen `$`"

- Traverse the graph forward from `BEGIN`.
- Maintain a flag: `afterDollar = true` once a `$` node is visited on the current path.
- Any character-producing node reached with `afterDollar = true` → mark that **edge** (or node, in that context) as
  invalid.

#### Pass B — Backward pass: propagate "has seen `^`"

- Traverse the graph backward from `END`.
- Maintain a flag: `beforeCaret = true` once a `^` node is visited on the current path.
- Any character-producing node reached (from the backward direction) with `beforeCaret = true` → mark that edge/node as
  invalid.

#### Cleanup

- Remove all edges/nodes marked invalid.
- Prune unreachable nodes (standard graph reachability from `BEGIN` and backward reachability from `END`).
- If a choice/group node has all alternatives pruned → prune the group itself.

**Pros:** Linear in graph size (edges + nodes); handles deeply nested patterns well.  
**Cons:** Requires careful handling of alternation (different paths may have different `afterDollar`/`beforeCaret`
states).

**Important detail for alternation:** Each branch of an alternation is an independent sub-path. The `afterDollar`/
`beforeCaret` flags are *per-path*, not global. This is naturally handled if the traversal is done on the graph with
proper per-path state (i.e., DFS with backtracking, or by encoding the flag into graph node states).

---

### Approach 3 — Constraint Propagation on Graph Edges

Annotate each edge with a set of **constraints** it carries:

- `AFTER_DOLLAR` — this edge was preceded by a `$` node somewhere on the path.
- `BEFORE_CARET` — this edge will be followed by a `^` node somewhere on the path.

An edge from node A to character-producing node B is **invalid** if it carries `AFTER_DOLLAR`.  
An edge from character-producing node A to node B is **invalid** if it carries `BEFORE_CARET`.

**Pros:** Modular; constraints can be extended (e.g., for future multiline handling).  
**Cons:** More complex to implement; constraint sets can grow if other constraint types are added.

---

### Approach 4 — Graph Transformation: Splitting on Anchor Context

Split the graph into two (or more) sub-graphs based on "position context":

- `NORMAL` context: between `BEGIN` and the first `$` (or after the last `^`).
- `POST_DOLLAR` context: after any `$` node.
- `PRE_CARET` context: before any `^` node.

Replicate nodes for each context they appear in; edges only exist between compatible context pairs. Remove all
character-producing nodes that appear in `POST_DOLLAR` or `PRE_CARET` contexts.

**Pros:** Clean separation of concerns; resulting graph is guaranteed valid without further checking.  
**Cons:** Can multiply the number of nodes significantly; more complex to implement and test.

---

## Recommended Starting Point

**Approach 2 (Forward/Backward Reachability)** is the best balance of correctness and implementation effort:

1. Build the existing graph normally.
2. DFS from `BEGIN`, tracking `afterDollar` state. Collect all `(edge, afterDollar=true)` pairs where the target is a
   character-producing node → these edges are **invalid**.
3. DFS backward from `END`, tracking `beforeCaret` state. Collect all `(edge, beforeCaret=true)` pairs where the source
   is a character-producing node → these edges are **invalid**.
4. Remove invalid edges.
5. Run standard dead-node elimination: remove any node with no incoming (non-BEGIN) or no outgoing (non-END) edges.
   Repeat until stable.
6. The result is an optimized graph that can only produce valid strings.

---

## Edge Cases to Handle

| Case                                     | Notes                                                                                                                                                     |
|------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `$` at the very end of pattern           | Already valid, nothing to prune.                                                                                                                          |
| `^` at the very start                    | Already valid, nothing to prune.                                                                                                                          |
| `(a$)` alone                             | The group has a single branch that ends with `$`; valid only if nothing follows the group.                                                                |
| `(a$\|b)c`                               | First branch is invalid ($ then c); second branch is valid. Prune first branch edge.                                                                      |
| `(^\|a)b`                                | First branch starts with `^` and is followed by `b` (char-producing) — if `^` is the very first node: check if the branch is reachable only from `BEGIN`. |
| Nested groups                            | Optimization must recurse into groups; inner invalid paths should cascade upward if they make an outer group entirely invalid.                            |
| Zero-width assertions other than `^`/`$` | `\b`, `\B` currently ignored; treat similarly if extended.                                                                                                |
| Multiline flag `(?m)`                    | `^` and `$` match line boundaries; different pruning rules apply — keep as future work.                                                                   |

---

## Questions / Open Points (from your note on resources)

- Some expected optimized graphs in `src/test/resources/` are acknowledged as "not entirely correct." Before coding, it
  is worth reviewing them case-by-case and deciding the correct expected output.
- Should the optimizer produce a **minimal** graph (all redundant nodes removed) or only **valid** (invalid paths
  removed, redundant nodes kept)? Minimal is cleaner but harder.
- How should the optimizer represent `^`/`$` nodes that are now "dead" (unreachable) — remove them entirely, or keep
  them as documentation of what was pruned?