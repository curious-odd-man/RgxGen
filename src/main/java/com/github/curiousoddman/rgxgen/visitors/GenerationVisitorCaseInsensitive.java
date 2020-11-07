package com.github.curiousoddman.rgxgen.visitors;

import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.nodes.SymbolSet;
import com.github.curiousoddman.rgxgen.util.Util;

import java.util.Map;
import java.util.Random;

public class GenerationVisitorCaseInsensitive extends GenerationVisitor {
    protected GenerationVisitorCaseInsensitive(Random random, Map<Integer, String> groupValues, RgxGenProperties properties) {
        super(random, groupValues, properties);
    }

    @Override
    public void visit(SymbolSet node) {
        String[] allSymbols = node.getSymbolsCaseInsensitive();
        int idx = aRandom.nextInt(allSymbols.length);
        aStringBuilder.append(allSymbols[idx]);
    }

    @Override
    public void visit(FinalSymbol node) {
        String original = node.getValue();
        aStringBuilder.append(Util.randomlyChangeCase(aRandom, original));
    }
}
