# Prompt 1

Current branch: #120.Generate-produces-invalid-strings-when-dollar-and-caret-inside-pattern

In this file are tests defined: `src/test/java/com/github/curiousoddman/rgxgen/lineages/GraphOptimizationTests.java`

In this folder expected optimized graphs [resources](../../src/test/resources)
Some of them are not entirely correct - I need more thoughts on that.

I need a class that would perform graph optimization.

Graph vertices are regex expression parts that are used to generate text.
Graph edges are relation `used after`, i.e. if `A -> B` means after `A` follows `B` node.

The optimization is needed to properly process `^` caret and `$` dollar signs in nodes.
Since when those symbols are used in the middle of a pattern they could create "paths" that are impossible.

For example
`(a$|c)x` would result in following paths
1. `begin` -> `a$` -> `x` -> `end`
2. `begin` -> `c` -> `x` -> `end`
where first path is impossible, because after `$` cannot follow `x`

Let's start without any code.

Summarize for me all input information that you find useful into MD file.
I will later use this file in a clean chat.
Also describe approaches that I could use to achieve desired behavior.

