package org.curious.regex.generator.nodes;

import org.curious.regex.generator.visitors.NodeVisitor;

public class Repeat implements Node {
    private final Node aNode;
    private final long aMin;
    private final long aMax;

    /*
     Noone will actually need infinite string in data.
     So i made a limit how much does start mean
     */
    private static long starMaxRepeat = 100;

    public static void setMaxRepeat(long maxRepeat) {
        starMaxRepeat = maxRepeat;
    }

    public static long getMaxRepeat() {
        return starMaxRepeat;
    }

    public static Repeat minimum(Node node, long times) {
        return new Repeat(node, times, starMaxRepeat);
    }

    public Repeat(Node node, long times) {
        this(node, times, times);
    }

    public Repeat(Node node, long min, long max) {
        aNode = node;
        aMin = min;
        aMax = max;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Node getNode() {
        return aNode;
    }

    public long getMin() {
        return aMin;
    }

    public long getMax() {
        return aMax;
    }

    @Override
    public String toString() {
        return "Repeat{" + aNode +
                ", aMin=" + aMin +
                ", aMax=" + aMax +
                '}';
    }
}
