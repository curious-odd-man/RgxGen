package com.github.curiousoddman.rgxgen.data;

import com.github.curiousoddman.rgxgen.RgxGen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

public enum DollarAndCaretPatterns implements AnalysisDataInterface {
    DEAD_BRANCH_DOLLAR("(a$|b)c", List.of("bc")),
    LIVE_BRANCH_DOLLAR("c(a$|b)", List.of("ca", "cb")),
    LIVE_BRANCH_CARET("(^a|b)c", List.of("ac", "bc")),
    DEAD_BRANCH_CARET("c(a|^b)", List.of("ca")),
    DEAD_BRANCH_CARET_DOLLAR("(^a$|b)c", List.of("bc")),
    ALL_DEAD_BRANCHES("x(a$|^b)c", List.of()),
    LIVE_BRANCHES_REPEAT_DOLLAR("(1$|1,){0,1}(2$|2,){0,1}", List.of("1", "1,", "1,2", "1,2,", "2", "2,")),
    LIVE_BRANCHES_REPEAT_CARET("(^1|1,){0,1}(^2|2,){0,1}", List.of("1", "1,", "1,2", "1,2,", "2", "2,")),
    DEAD_BRANCHES_REPEAT_DOLLAR("(1$|1,){0,1}(2$|2,)", List.of("1,2", "2", "2,")),
    DEAD_BRANCHES_REPEAT_CARET("(^1|1,)(^2|2,){0,1}", List.of("1", "1,", "1,2,", "2,")),
    LIVEDEAD_REPEAT_CARET("(^a)+", List.of("a")),
    LIVEDEAD_REPEAT_DOLLAR("(b$)*", List.of("", "b")),
    DEAD_ON_REPEAT_CARET("(a|^x){1,2}", List.of("a", "x", "aa")),
    DEAD_ON_REPEAT_DOLLAR("(a$|x){1,2}", List.of("a", "x", "xx")),
    DEAD_ON_REPEAT_WIHTOUT_REPEAT_DOLLAR("(a$|x){2,2}", List.of("xx")),
    ;

    private final String pattern;
    private final String optimizedGraphPuml;
    private final List<String> uniqueValues;

    DollarAndCaretPatterns(String pattern, List<String> uniqueValues) {
        this.pattern = pattern;
        this.uniqueValues = uniqueValues;
        Path path = Path.of("src/test/resources/" + name() + ".puml");
        optimizedGraphPuml = getOptimizedGraphPuml(pattern, path);
    }

    private String getOptimizedGraphPuml(String pattern, Path path) {
        try {
            return Files.readString(path);
        } catch (NoSuchFileException e) {
            String optimizedGraphPuml = RgxGen.parse(pattern).getPathGraph().toPlantUml();
            try {
                Files.writeString(path, optimizedGraphPuml);
                return optimizedGraphPuml;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public String getOptimizedGraph() {
        return optimizedGraphPuml;
    }

    @Override
    public List<String> getAllUniqueValues() {
        return uniqueValues;
    }
}

