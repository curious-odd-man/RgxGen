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

import java.util.Random;

public class NotMatchingGenerationVisitor extends GenerationVisitor {
    private static final String[] allSymbols = SymbolSet.getAllSymbols();

    public NotMatchingGenerationVisitor() {
    }

    public NotMatchingGenerationVisitor(Random random) {
        super(random);
    }

    @Override
    public void visit(SymbolSet node) {
        super.visit(new SymbolSet(node.getSymbols(), SymbolSet.TYPE.NEGATIVE));
    }

    @Override
    public void visit(Choice node) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void visit(FinalSymbol node) {
        String nodeValue = node.getValue();
        StringBuilder builder = new StringBuilder(nodeValue.length());

        do {
            builder.delete(0, builder.length());
            nodeValue.chars()
                     .map(c -> allSymbols[aRandom.nextInt(allSymbols.length)].charAt(0))
                     .forEachOrdered(c -> builder.append((char) c));
        } while (nodeValue.equals(builder.toString()));
        aStringBuilder.append(builder);
    }

    @Override
    public void visit(Repeat node) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void visit(Sequence node) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void visit(NotSymbol node) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void visit(GroupRef node) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void visit(Group node) {
        throw new RuntimeException("not implemented");
    }
}
