package com.github.curiousoddman.rgxgen.manual.generator.unicode;

import com.github.curiousoddman.rgxgen.model.SymbolRange;

import java.util.List;

public class UnicodeCategoryDescriptor {
    private final List<SymbolRange> ranges;
    private final List<Character>   characters;

    public UnicodeCategoryDescriptor(List<SymbolRange> ranges, List<Character> characters) {
        this.ranges = ranges;
        this.characters = characters;
    }

    public List<SymbolRange> getRanges() {
        return ranges;
    }

    public List<Character> getCharacters() {
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
