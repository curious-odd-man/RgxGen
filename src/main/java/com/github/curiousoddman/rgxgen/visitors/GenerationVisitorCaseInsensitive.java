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
import com.github.curiousoddman.rgxgen.nodes.FinalSymbol;
import com.github.curiousoddman.rgxgen.nodes.AsciiSymbolSet;
import com.github.curiousoddman.rgxgen.nodes.UnicodeSymbolSet;
import com.github.curiousoddman.rgxgen.util.Util;

import java.util.Map;
import java.util.Random;

public class GenerationVisitorCaseInsensitive extends GenerationVisitor {
    protected GenerationVisitorCaseInsensitive(Random random, Map<Integer, String> groupValues, RgxGenProperties properties) {
        super(random, groupValues, properties);
    }

    @Override
    public void visit(AsciiSymbolSet node) {
        Character[] allSymbols = node.getSymbolsCaseInsensitive();
        int idx = aRandom.nextInt(allSymbols.length);
        aStringBuilder.append(allSymbols[idx]);
    }

    @Override
    public void visit(UnicodeSymbolSet node) {
        Character[] allSymbols = node.getSymbolsCaseInsensitive();
        int idx = aRandom.nextInt(allSymbols.length);
        aStringBuilder.append(allSymbols[idx]);
    }

    @Override
    public void visit(FinalSymbol node) {
        String original = node.getValue();
        aStringBuilder.append(Util.randomlyChangeCase(aRandom, original));
    }
}
