package com.github.curiousoddman.rgxgen.lineages;

/**
 * A directed edge in the path graph.
 * <p>
 * {@code min} and {@code max} describe how many times this edge may be traversed
 * in a single generation pass.  {@code max == -1} means unbounded (∞).
 */
public class PathEdge {

    public static final int UNBOUNDED = -1;

    private final PathNode from;
    private final PathNode to;
    private final int min;
    private final int max;   // -1 = unbounded

    public PathEdge(PathNode from, PathNode to, int min, int max) {
        this.from = from;
        this.to = to;
        this.min = min;
        this.max = max;
    }

    // -------------------------------------------------------------------------
    // Factory helpers
    // -------------------------------------------------------------------------

    /**
     * Edge that is always traversed exactly once.
     */
    public static PathEdge once(PathNode from, PathNode to) {
        return new PathEdge(from, to, 1, 1);
    }

    /**
     * Edge that carries the repetition bounds of a {@code Repeat} node.
     */
    public static PathEdge repeat(PathNode from, PathNode to, int min, int max) {
        return new PathEdge(from, to, min, max);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public PathNode getFrom() {
        return from;
    }

    public PathNode getTo() {
        return to;
    }

    public int getMin() {
        return min;
    }

    /**
     * Returns {@link #UNBOUNDED} (-1) when there is no upper limit.
     */
    public int getMax() {
        return max;
    }

    // -------------------------------------------------------------------------
    // PlantUML / DOT label
    // -------------------------------------------------------------------------

    /**
     * Human-readable label: {@code "1..1"}, {@code "0..∞"}, etc.
     */
    public String label() {
        if (min == max) {
            if (min == 1) {
                return "";
            } else {
                return String.valueOf(min);
            }
        }
        String maxStr = (max == UNBOUNDED) ? "∞" : String.valueOf(max);
        return min + ".." + maxStr;
    }

    @Override
    public String toString() {
        return from.getId() + " -[" + label() + "]-> " + to.getId();
    }
}
