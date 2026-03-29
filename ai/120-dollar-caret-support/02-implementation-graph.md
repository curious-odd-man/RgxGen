I want to create a graph of paths. A path is a list of nodes in the order that those are visited.
Let's call it lineages.
Graph would contain all possible sequences in which nodes could be used during generation - i.e. get a graph for each path.
From the node tree that is created as a result of parsing I want to create a graph of paths.

For example.

`(a$|c)x` would result in following paths
1. `begin` -> `a$` -> `x` -> `end`
2. `begin` -> `c` -> `x` -> `end`

for more complex situation `(a|^x)+`
                
1. `begin` -> `a` -> `^x` -> `end`
               ^_______| 
2. `begin` -> `a` -> `a` -> `end`
               ^_______|
3. `begin` -> `^x` -> `^x` -> `end`
               ^_______|
4. `begin` -> `^x` -> `a` -> `end`
               ^_______|

Other requirements:
1. Graph should be dumpable as plantuml dot text
2. The edges must contain limit of how many times it can be followed.
3. Repetition patterns should always be condensed into 2 steps. Even infinite ones.

No code yet. Tell me how would you implement it.

---

# Response

## High-Level Design

This is a graph construction problem where you need to transform a **node tree** (the parsed regex AST) into a **path graph** that captures all possible traversal sequences, with edges that encode repetition constraints.

---

### Core Concepts

**What you're building:** A directed graph where:
- Nodes are regex AST nodes (plus synthetic `begin`/`end` sentinels)
- Edges represent "can be followed by" relationships
- Edge metadata encodes repetition limits
- The graph is a **summary** of all possible execution paths, not an enumeration of them

---

### Node Classification

First, classify every AST node by how it affects path flow:

| Category | Examples | Path behavior |
|---|---|---|
| **Terminal** | `FinalSymbol`, `SymbolRange` | Single step, passes through |
| **Sequence** | `Sequence` | Children visited in order, compose linearly |
| **Choice** | `Choice` (alternation `\|`) | Each child is an independent branch |
| **Repetition** | `Repeat` | Creates a back-edge with min/max bounds |
| **Anchor** | `GroupRef`, anchors | Treated as terminals for path purposes |
| **Group** | `Group` | Transparent wrapper, delegate to child |

---

### Graph Model

```
PathGraph
  nodes: Set<PathNode>         // wraps each AST node + BEGIN + END
  edges: List<PathEdge>

PathNode
  id: unique stable id
  label: string                // e.g. "FinalSymbol('a')", "Choice", etc.
  astNode: Node                // back-reference

PathEdge
  from: PathNode
  to: PathNode
  minTraversals: int           // 0 or 1
  maxTraversals: int | ∞
```

---

### Construction Algorithm

The key insight is to define a recursive function with this signature:

```
compile(astNode) -> Fragment
```

Where a **Fragment** is not a complete graph, but a *partial graph with open endpoints*:

```
Fragment
  entry: PathNode              // where control flows in
  exits: Set<PathNode>         // where control can flow out
  internalEdges: List<PathEdge>
```

This is the **Thompson construction** pattern (same idea used in NFA construction from regex).

#### Fragment rules per node type:

**Terminal node** (`FinalSymbol`, `SymbolRange`, anchors)
- Create one `PathNode`
- Fragment: `entry = exits = {thisNode}`, no internal edges

**Sequence node**
- Compile each child into a fragment
- Chain them: connect each fragment's `exits` to the next fragment's `entry` with a `[1,1]` edge
- Fragment: `entry = first.entry`, `exits = last.exits`

**Choice node** (alternation)
- Compile each child into a fragment
- Create a synthetic `ChoiceNode` as dispatcher (or leave implicit)
- Fragment: `entry = choiceNode`, `exits = union of all child.exits`
- Each child's entry gets an edge from the choice node

**Repeat node** `{min, max}`
- Compile the child into a fragment
- **The "condense into 2 steps" requirement means:** always represent as exactly one "body node" with a self-loop or back-edge, regardless of the actual min/max
- Concretely: create a synthetic `RepeatNode`, connect it to `child.entry` with `[min, max]` edge, connect `child.exits` back to `RepeatNode` (the back-edge is what encodes the loop)
- Fragment: `entry = RepeatNode`, `exits = {RepeatNode}` (can exit after any iteration)

**Group node**
- Transparent — just return the child's fragment directly (or wrap with a labeled node if you want groups visible in the graph)

#### Assembly

Once you have the root fragment:
1. Create `BEGIN` node, connect it to `rootFragment.entry` with a `[1,1]` edge
2. Create `END` node, connect all `rootFragment.exits` to it with `[1,1]` edges

---

### Handling the `(a|^x)+` example

- `+` is `Repeat{1, ∞}` wrapping a `Choice(a, ^x)`
- `Choice` compiles to a choice node with two exits: `PathNode(a)` and `PathNode(^x)`
- `Repeat` wraps it: creates `RepeatNode`, edge `RepeatNode → ChoiceNode [1,∞]`, back-edges from both `PathNode(a)` and `PathNode(^x)` back to `RepeatNode`
- The "2 steps" are: `RepeatNode → ChoiceNode` (enter the loop body) and `body → RepeatNode` (loop back or exit)

This naturally produces your 4 paths when you enumerate sequences — but the **graph itself** is compact.

---

### PlantUML / DOT Serialization

Walk the edge list and emit:

```
@startuml
digraph G {
  BEGIN -> Choice_1 [label="1..1"]
  RepeatNode_2 -> Choice_1 [label="1..∞"]
  Choice_1 -> a [label="1..1"]
  Choice_1 -> "^x" [label="1..1"]
  a -> RepeatNode_2 [label="1..1"]
  "^x" -> RepeatNode_2 [label="1..1"]
  RepeatNode_2 -> END [label="1..1"]
}
@enduml
```

Edge label format: `min..max` where max is `∞` for unbounded.

---

### Key Design Decisions to Make

1. **Are synthetic nodes (Choice dispatcher, RepeatNode) visible in the graph, or are they implicit?** I'd recommend keeping them visible — it makes the graph readable and gives you stable anchor points for edges.

2. **Do you merge structurally identical fragments?** For now, no — keep it one-to-one with the AST. Merging is an optimization you can add later.

3. **How do you handle nested repeats?** Each `Repeat` node independently gets its back-edge. Nesting just means one `RepeatNode`'s exit feeds into another's entry chain.

4. **`GroupRef` (backreferences)?** Treat as terminals — the path graph can't statically resolve what they'll match, so they appear as opaque nodes.

---

### Implementation Phases

1. **Define the graph model** (`PathNode`, `PathEdge`, `PathGraph`, `Fragment`) — pure data, no logic
2. **Write the `compile(Node) → Fragment` visitor** — one case per node type
3. **Write the assembler** — attach `BEGIN`/`END`, flatten fragments into a single `PathGraph`
4. **Write the PlantUML serializer** — straightforward edge walk
5. **Write tests** — verify your examples `(a$|c)x` and `(a|^x)+` produce the expected graphs


# Prompt

do I need synthetic nodes? Why not go directly to whatever nodes those would hold?

# Response 

Good challenge. Let's think through each synthetic node:

---

### Choice Dispatcher Node

For `(a|b)` you'd have edges:

**With synthetic node:** `Choice → a`, `Choice → b`

**Without:** anything that points *into* the choice points directly to both `a` and `b`.

So if you have `x(a|b)y`, instead of `x → Choice → a → y` and `x → Choice → b → y`, you get `x → a → y` and `x → b → y`.

**This works fine.** The `Choice` node carries no information that isn't already captured by the branching of edges. You can eliminate it entirely — the Fragment just returns `exits = {a, b}` as its entry points (a set, not a single node), and the parent connects to all of them.

This means `Fragment.entry` becomes `Set<PathNode>` instead of a single node, which is a small complication but a clean one.

---

### Repeat Node

For `(a|b)+` you need a back-edge. The question is: back-edge to *what*?

**With synthetic RepeatNode:** back-edges from `a` and `b` go to `RepeatNode`, which then fans out to `a` and `b` again. One target for all back-edges.

**Without:** back-edges from `a` and `b` go to... both `a` and `b`. So `a → a`, `a → b`, `b → a`, `b → b`. That's N² back-edges for N alternatives inside a repeat.

This still **correctly encodes** all possible paths. But it gets verbose fast — `(a|b|c|d)+` produces 16 back-edges instead of 4.

More importantly, it **obscures the repetition boundary**. When you read the graph, you can no longer tell at a glance where the loop restarts — the back-edges are scattered across the body nodes.

---

### Verdict

| | Choice node | Repeat node |
|---|---|---|
| Information content | Zero — pure fan-out | Real — marks loop re-entry point |
| Without it | Cleaner, Fragment.entry becomes a Set | N² back-edges, blurry loop structure |
| Recommendation | **Drop it** | **Keep it** |

The `RepeatNode` is earning its place because it gives you a single stable re-entry point for the loop. The `Choice` node is just routing that the edge structure already expresses.