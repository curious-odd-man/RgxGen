package com.github.curiousoddman.rgxgen.manual.generator.unicode;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineDescriptor {
    private static final String S_S_S_S_S = "    %s(%s, %s, %s, %s),";
    private static final String S_S_S_S   = "    %s(%s, %s, %s),";

    public UnicodeCategory   unicodeCategory;
    public List<String>      keys;
    public String            description;
    public List<SymbolRange> ranges;
    public List<Character>   characters;

    public LineDescriptor(UnicodeCategory unicodeCategory, List<String> keys, String description, List<SymbolRange> ranges, List<Character> characters) {
        this.unicodeCategory = unicodeCategory;
        this.keys = keys;
        this.description = description;
        this.ranges = ranges;
        this.characters = characters;
    }

    public String formatToText(Map<SymbolRange, String> constantNames) {
        String keysText = makeKeysText(unicodeCategory);
        String descriptionText = makeDescription(unicodeCategory);
        String rangesText = makeRanges(ranges, constantNames);
        String charactersText = makeCharacters(characters);
        if (ranges.isEmpty()) {
            return String.format(
                    S_S_S_S,
                    unicodeCategory.name(), keysText, descriptionText, charactersText
            );
        } else if (characters.isEmpty()) {
            return String.format(
                    S_S_S_S,
                    unicodeCategory.name(), keysText, descriptionText, rangesText
            );
        }

        return String.format(S_S_S_S_S, unicodeCategory.name(), keysText, descriptionText, rangesText, charactersText);
    }

    private static String makeCharacters(List<Character> characters) {
        if (characters.isEmpty()) {
            return "null";
        }
        return String.format("new Character[]{%s}",
                             characters.stream().map(Utils::charAsString).map(LineDescriptor::sq).collect(Collectors.joining(","))
        );
    }

    private static String makeRanges(List<SymbolRange> ranges, Map<SymbolRange, String> constantNames) {
        if (ranges.isEmpty()) {
            return "null";
        }
        if (ranges.size() == 1) {
            SymbolRange range = ranges.get(0);
            return rangeOrConstant(constantNames, range);
        }
        return "asList(" + ranges
                .stream()
                .map(range -> rangeOrConstant(constantNames, range))
                .collect(Collectors.joining(",")) + ')';
    }

    private static String rangeOrConstant(Map<SymbolRange, String> constantNames, SymbolRange range) {
        return constantNames.getOrDefault(range, String.format("range('%s', '%s')", Utils.charAsString(range.getFrom()), Utils.charAsString(range.getTo())));
    }

    private static String makeDescription(UnicodeCategory key) {
        if (key.getDescription() == null) {
            return "";
        }
        return q(key.getDescription());
    }

    private static String makeKeysText(UnicodeCategory key) {
        List<String> keys = key.getKeys();
        return "keys(" + keys.stream().map(LineDescriptor::q).collect(Collectors.joining(",")) + ')';
    }

    private static String q(String text) {
        return '"' + text + '"';
    }

    private static String sq(String text) {
        return '\'' + text + '\'';
    }

    public List<SymbolRange> getRanges() {
        return ranges;
    }
}
