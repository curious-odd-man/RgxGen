package com.github.curiousoddman.rgxgen.generator.visitors;

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

import com.github.curiousoddman.rgxgen.generator.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;

import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class NotMatchingGenerationVisitor extends GenerationVisitor {

    public static GenerationVisitorBuilder builder() {
        return new GenerationVisitorBuilder(NotMatchingGenerationVisitor::new);
    }

    private static final String[] allSymbols = SymbolSet.getAllSymbols();

    public NotMatchingGenerationVisitor(Random random, Map<Integer, String> groupValues) {
        super(random, groupValues);
    }

    @Override
    public void visit(SymbolSet node) {
        // There is only one way to generate not matching for any character - is to not generate anything
        String pattern = node.getPattern();
        SymbolSet symbolSet = new SymbolSet("[^" + pattern.substring(1), node.getSymbols(), SymbolSet.TYPE.NEGATIVE);
        if (!symbolSet.isEmpty()) {
            super.visit(symbolSet);
        }
    }

    @Override
    public void visit(Choice node) {
        Node[] nodes = node.getNodes();
        int pos = aStringBuilder.length();
        // We need to add existing group values, so that we could later use it in matching pattern
        StringBuilder groupsBuilder = new StringBuilder();
        StringBuilder valuePrefixBuilder = new StringBuilder();
        int groupValuesUsed = 0;
        for (int i = 1; groupValuesUsed < aGroupValues.size(); i++) {
            String s = aGroupValues.get(i);
            groupsBuilder.append('(');
            // In complex expressions we might skip some groups (due to inlined choices/groups/whatever).
            // But still we should properly generate this test
            if (s != null) {
                groupsBuilder.append(Pattern.quote(s));
                ++groupValuesUsed;
            }
            groupsBuilder.append(')');
            valuePrefixBuilder.append(s);
        }

        // Add groups values to pattern - in case there are group refs used inside the node.getPattern()
        Pattern pattern = Pattern.compile(groupsBuilder + node.getPattern());

        do {
            aStringBuilder.delete(pos, Integer.MAX_VALUE);
            int i = aRandom.nextInt(nodes.length);
            nodes[i].visit(this);
            // To match group values along with generated values - we need to prepend groups values before the generated
        } while (pattern.matcher(valuePrefixBuilder + aStringBuilder.substring(pos))
                        .matches());
    }

    @Override
    public void visit(FinalSymbol node) {
        String nodeValue = node.getValue();
        if (nodeValue.isEmpty()) {
            aStringBuilder.append(allSymbols[aRandom.nextInt(allSymbols.length)].charAt(0));
        } else {
            StringBuilder builder = new StringBuilder(nodeValue.length());
            do {
                builder.delete(0, Integer.MAX_VALUE);
                nodeValue.chars()
                         .map(c -> allSymbols[aRandom.nextInt(allSymbols.length)].charAt(0))
                         .forEachOrdered(c -> builder.append((char) c));
            } while (nodeValue.equals(builder.toString()));
            aStringBuilder.append(builder);
        }
    }

    @Override
    public void visit(Repeat node) {
        // Zero length repeat will match pattern despite what node is repeated.
        if (node.getMin() == 0) {
            super.visit(new Repeat(node.getPattern(), node.getNode(), 1, node.getMax()));
        } else {
            super.visit(node);
        }
    }

    @Override
    public void visit(NotSymbol node) {
        NodeTreeBuilder builder = new DefaultTreeBuilder(node.getPattern());
        Node subNode = builder.get();
        GenerationVisitor generationVisitor = new GenerationVisitor(aRandom, aGroupValues);
        subNode.visit(generationVisitor);
        aStringBuilder.append(generationVisitor.getString());
    }

    @Override
    public void visit(GroupRef node) {
        // Note: How will this work if we will change only some of the nodes???
        FinalSymbol finalSymbol = new FinalSymbol(aGroupValues.get(node.getIndex()));
        visit(finalSymbol);
    }
}
