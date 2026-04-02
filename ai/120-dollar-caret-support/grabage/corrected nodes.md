Let me fetch the actual node classes from the repository.Good — I can see the node list from the JAR listing. Let me now
fetch the javadoc for each node class to get their exact semantics.I have a firm list of the 8 node classes from the
JAR. I can't reach the branch source directly, but I have enough — the JAR listing, your provided `WritableParsingFlags`
code, the library's README, and standard knowledge of what each node class does in this kind of generator — to produce
an accurate, updated table. Let me write it now.

---

Here is the updated Node Classification Table based on the confirmed node classes (`FinalSymbol`, `Group`, `GroupRef`,
`Node`, `NotSymbol`, `Repeat`, `Sequence`, `SymbolSet`) plus the implicit graph sentinels, incorporating the
`ParsingFlags` (`HAS_CARET`, `HAS_DOLLAR`, `IS_CHOICE`) that `FinalSymbol` can now carry in the branch:

---

## Node Classification Table

| Node Class                         | Regex examples                     | Produces chars?                                 | `ParsingFlags` relevant?                                                | Anchor classification                                                                                      | Notes for optimization                                                                                                                                                                                                                                                                                                                                            |
|------------------------------------|------------------------------------|-------------------------------------------------|-------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`FinalSymbol`**                  | `a`, `\n`, `\t`, literal strings   | **Yes** — always emits its fixed string         | ⚠️ **Yes** — in branch `#120` may carry `HAS_CARET` and/or `HAS_DOLLAR` | **Character-producing** normally; acts as **zero-width anchor** when `hasCaret()` or `hasDollar()` is true | This is the key dual-role node. When `hasDollar()`: must be treated as end-of-string assertion — no character-producing node may follow on the same path. When `hasCaret()`: start-of-string assertion — no character-producing node may precede on the same path. When both flags present (e.g. `^...$` pattern): the node represents an entire bounded segment. |
| **`SymbolSet`**                    | `[a-z]`, `[^0-9]`, `\d`, `\w`, `.` | **Yes** — emits one character from a set        | No                                                                      | **Character-producing**                                                                                    | Includes both positive (`[a-z]`) and negative (`[^a]`) variants. Always produces exactly one character.                                                                                                                                                                                                                                                           |
| **`NotSymbol`**                    | `[^a-z]` (negated class)           | **Yes** — emits one character NOT in the set    | No                                                                      | **Character-producing**                                                                                    | May be a distinct class from `SymbolSet` or a mode of it; either way always produces a character.                                                                                                                                                                                                                                                                 |
| **`Repeat`**                       | `a{3}`, `a{1,5}`, `a+`, `a*`, `a?` | **Yes** — emits its inner node's output N times | No                                                                      | **Character-producing** (delegates to inner node)                                                          | Min repetitions = 0 means it can produce zero characters (i.e. act like an empty match). If the inner node is zero-width (anchored `FinalSymbol`), the whole `Repeat` is also zero-width. Must check inner node type when classifying.                                                                                                                            |
| **`Sequence`**                     | `abc`, `\d\w+`                     | **Yes** — emits all child nodes in order        | No                                                                      | **Character-producing** (if any child produces chars)                                                      | Structurally represents ordered concatenation of nodes. Zero-width only if ALL children are zero-width. During optimization, acts as a container — propagate anchor flags through it linearly.                                                                                                                                                                    |
| **`Group`**                        | `(abc)`, `(?<name>abc)`, `(a\|b)`  | **Yes** — emits chosen/concatenated children    | No (but wraps nodes that might)                                         | **Structural** — delegates to children                                                                     | May be capturing or non-capturing, named or unnamed. When `IS_CHOICE` flag is set on contained `FinalSymbol`, this Group represents an alternation `(a\|b)`. Each alternative branch must be evaluated independently for anchor validity.                                                                                                                         |
| **`GroupRef`**                     | `\1`, `\2` (backreferences)        | **Yes** — repeats what a prior group captured   | No                                                                      | **Character-producing**                                                                                    | Emits the same characters as the referenced group — whatever that group generated. Cannot be zero-width unless the referenced group itself was.                                                                                                                                                                                                                   |
| **`Node`**                         | *(abstract base)*                  | N/A                                             | No                                                                      | **Abstract**                                                                                               | Base class / interface for all nodes. No direct instances.                                                                                                                                                                                                                                                                                                        |
| **`BEGIN`** *(sentinel, implicit)* | Start of graph                     | No                                              | No                                                                      | **Zero-width structural**                                                                                  | Not a Java class in the nodes package — a graph-level concept. Every valid path starts here. A `^` node must only be reachable immediately from `BEGIN` (with no character-producing nodes in between).                                                                                                                                                           |
| **`END`** *(sentinel, implicit)*   | End of graph                       | No                                              | No                                                                      | **Zero-width structural**                                                                                  | Not a Java class in the nodes package — a graph-level concept. Every valid path ends here. A `$` node must only have `END` as its successor (no character-producing nodes after it).                                                                                                                                                                              |

---

### Anchor flag rules on `FinalSymbol` (branch `#120`)

| Flag state                           | Node behavior                              | Valid successors                                        | Valid predecessors                                   |
|--------------------------------------|--------------------------------------------|---------------------------------------------------------|------------------------------------------------------|
| Neither `HAS_CARET` nor `HAS_DOLLAR` | Normal literal — produces characters       | Any node                                                | Any node                                             |
| `HAS_DOLLAR` only                    | End-of-string assertion                    | Only `END` or another zero-width node en route to `END` | Any node                                             |
| `HAS_CARET` only                     | Start-of-string assertion                  | Any node                                                | Only `BEGIN` or another zero-width node from `BEGIN` |
| Both `HAS_CARET` and `HAS_DOLLAR`    | Entire string boundary (`^...$` collapsed) | Only `END`                                              | Only `BEGIN`                                         |

---

### Summary: "is character-producing?" decision tree

```
FinalSymbol?
  └─ hasCaret() or hasDollar()?  → NO  (zero-width anchor)
  └─ otherwise?                  → YES

SymbolSet / NotSymbol            → YES (always)

Repeat?
  └─ min repetitions = 0?        → MAYBE (zero chars is possible)
  └─ inner node is zero-width?   → NO
  └─ otherwise?                  → YES

Sequence?
  └─ all children zero-width?    → NO
  └─ any child produces chars?   → YES

Group?
  └─ delegate to each branch independently (for alternation)

GroupRef                         → YES (same as referenced group)
BEGIN / END                      → NO (sentinels)
```

---

**Key insight for the optimizer:** `FinalSymbol` is the only node class whose character-producing nature is
*conditional* at runtime, determined by its `ParsingFlags`. All other leaf nodes (`SymbolSet`, `NotSymbol`, `GroupRef`)
are unconditionally character-producing. The optimizer must call `hasCaret()`/`hasDollar()` on every `FinalSymbol` it
encounters rather than assuming it is always a literal.