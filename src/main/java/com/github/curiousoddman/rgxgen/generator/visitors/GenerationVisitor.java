package com.github.curiousoddman.rgxgen.generator.visitors;

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GenerationVisitor implements NodeVisitor {
    private final StringBuilder        aStringBuilder = new StringBuilder();
    private final Map<Integer, String> aGroupValues   = new HashMap<>();

    @Override
    public void visit(SymbolSet node) {
        String[] allSymbols = node.getSymbols();
        int idx = ThreadLocalRandom.current()
                                   .nextInt(allSymbols.length);
        aStringBuilder.append(allSymbols[idx]);
    }

    @Override
    public void visit(Choice node) {
        Node[] nodes = node.getNodes();
        int idx = ThreadLocalRandom.current()
                                   .nextInt(nodes.length);
        nodes[idx].visit(this);
    }

    @Override
    public void visit(FinalSymbol node) {
        aStringBuilder.append(node.getValue());
    }

    @Override
    public void visit(Repeat node) {
        int max = node.getMax() == -1 ? 100 : node.getMax();
        int repeat = node.getMin() >= max ?
                     node.getMin() :
                     ThreadLocalRandom.current()
                                      .nextInt(node.getMin(), max + 1);

        for (long i = 0; i < repeat; ++i) {
            node.getNode()
                .visit(this);
        }
    }

    @Override
    public void visit(Sequence node) {
        for (Node n : node.getNodes()) {
            n.visit(this);
        }
    }

    @Override
    public void visit(NotSymbol notSymbol) {
        String value = notSymbol.getSubPattern()
                                .pattern();
        String result = Util.randomString(value);
        while (!notSymbol.getSubPattern()
                         .matcher(value)
                         .matches()) {
            result = Util.randomString(result);
        }

        aStringBuilder.append(result);
    }

    @Override
    public void visit(GroupRef groupRef) {
        aStringBuilder.append(aGroupValues.get(groupRef.getIndex()));
    }

    @Override
    public void visit(Group group) {
        int start = aStringBuilder.length();
        group.getNode()
             .visit(this);
        aGroupValues.put(group.getIndex(), aStringBuilder.substring(start));
    }

    public String getString() {
        return aStringBuilder.toString();
    }
}
