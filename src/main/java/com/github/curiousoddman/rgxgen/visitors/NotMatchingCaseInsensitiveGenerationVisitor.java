package com.github.curiousoddman.rgxgen.visitors;

import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.nodes.SymbolSet;

import java.util.Map;
import java.util.Random;

// TODO: Implement tests for Case Insensitive
public class NotMatchingCaseInsensitiveGenerationVisitor extends NotMatchingGenerationVisitor {
    public NotMatchingCaseInsensitiveGenerationVisitor(Random random, Map<Integer, String> groupValues, RgxGenProperties properties) {
        super(random, groupValues, properties);
    }

    @Override
    public void visit(SymbolSet node) {
        visitSymbolSet(node, SymbolSet::getSymbolsCaseInsensitive);
    }

    @Override
    protected boolean equalsFinalSymbolRandomString(String s1, String s2) {
        return s1.equalsIgnoreCase(s2);
    }
}
