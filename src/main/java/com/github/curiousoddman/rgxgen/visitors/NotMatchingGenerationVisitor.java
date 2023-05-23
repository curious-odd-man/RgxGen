package com.github.curiousoddman.rgxgen.visitors;

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

import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Pattern;


public class NotMatchingGenerationVisitor extends GenerationVisitor {

    public static GenerationVisitorBuilder builder() {
        return new GenerationVisitorBuilder(false);
    }

    private static final Character[] allSymbols = SymbolSet.getAllSymbols();

    public NotMatchingGenerationVisitor(Random random, Map<Integer, String> groupValues, RgxGenProperties properties) {
        super(random, groupValues, properties);
    }

    @Override
    public void visit(SymbolSet node) {
        visitSymbolSet(node, SymbolSet::getSymbols);
    }

    protected void visitSymbolSet(SymbolSet node, Function<SymbolSet, Character[]> getSymbols) {
        // There is only one way to generate not matching for any character - is to not generate anything
        String pattern = node.getPattern();
        SymbolSet symbolSet = new SymbolSet("[^" + pattern.substring(1), getSymbols.apply(node), MatchType.NEGATIVE);
        if (!symbolSet.isEmpty()) {
            super.visit(symbolSet);
        }
    }

    @Override
    public void visit(Choice node) {
        StringBuilder groupsBuilder = new StringBuilder();
        StringBuilder valuePrefixBuilder = new StringBuilder();
        buildGroupsStringAndValuePrefix(groupsBuilder, valuePrefixBuilder);

        // Add groups values to pattern - in case there are group refs used inside the node.getPattern()
        Pattern pattern = Pattern.compile(groupsBuilder + node.getPattern());
        int pos = aStringBuilder.length();
        Node[] nodes = node.getNodes();
        do {
            aStringBuilder.delete(pos, Integer.MAX_VALUE);
            int i = aRandom.nextInt(nodes.length);
            nodes[i].visit(this);
            // To match group values along with generated values - we need to prepend groups values before the generated
        } while (pattern.matcher(valuePrefixBuilder + aStringBuilder.substring(pos))
                        .matches());
    }

    /**
     * We need to add existing group values, so that we could later use it in matching pattern
     *
     * @param groupsBuilder
     * @param valuePrefixBuilder
     */
    private void buildGroupsStringAndValuePrefix(StringBuilder groupsBuilder, StringBuilder valuePrefixBuilder) {
        int groupValuesUsed = 0;
        for (int i = 1; groupValuesUsed < aGroupValues.size(); i++) {
            String s = aGroupValues.get(i);
            groupsBuilder.append('(');
            // In complex expressions we might skip some groups (due to inlined choices/groups/whatever).
            // But still we should properly generate this test
            if (s != null) {
                groupsBuilder.append(Pattern.quote(s));
                ++groupValuesUsed;
                valuePrefixBuilder.append(s);
            }
            groupsBuilder.append(')');
        }
    }

    @Override
    public void visit(FinalSymbol node) {
        String nodeValue = node.getValue();
        if (nodeValue.isEmpty()) {
            aStringBuilder.append(allSymbols[aRandom.nextInt(allSymbols.length)]);
        } else {
            StringBuilder builder = new StringBuilder(nodeValue.length());
            do {
                builder.delete(0, Integer.MAX_VALUE);
                nodeValue.chars()
                         .map(c -> allSymbols[aRandom.nextInt(allSymbols.length)])
                         .forEachOrdered(c -> builder.append((char) c));
            } while (equalsFinalSymbolRandomString(nodeValue, builder.toString()));
            aStringBuilder.append(builder);
        }
    }

    protected boolean equalsFinalSymbolRandomString(String s1, String s2) {
        return s1.equals(s2);
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
        GenerationVisitor generationVisitor = new GenerationVisitor(aRandom, aGroupValues, aProperties);
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
