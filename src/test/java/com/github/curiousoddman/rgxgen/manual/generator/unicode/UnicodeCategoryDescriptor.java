package com.github.curiousoddman.rgxgen.manual.generator.unicode;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.util.chars.CharList;

import java.util.List;

public record UnicodeCategoryDescriptor(List<SymbolRange> ranges, CharList characters) {

    @Override
    public String toString() {
        return "UnicodeCategoryDescriptor{" +
                "ranges=" + ranges +
                ", characters=" + characters +
                '}';
    }
}
