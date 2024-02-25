package com.github.curiousoddman.rgxgen.manual.generator.unicode;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.util.chars.CharList;

import java.util.List;

public class UnicodeCategoryDescriptor {
    private final List<SymbolRange> ranges;
    private final CharList   characters;

    public UnicodeCategoryDescriptor(List<SymbolRange> ranges, CharList characters) {
        this.ranges = ranges;
        this.characters = characters;
    }

    public List<SymbolRange> getRanges() {
        return ranges;
    }

    public CharList getCharacters() {
        return characters;
    }

    @Override
    public String toString() {
        return "UnicodeCategoryDescriptor{" +
                "ranges=" + ranges +
                ", characters=" + characters +
                '}';
    }
}
