package com.github.curiousoddman.rgxgen.data;

import java.nio.file.Path;
import java.util.List;

public class DollarAndCaretPatterns {

    public enum MatchingNothing implements FileTestUtils {
        DEAD_PATTERN_DOLLAR(
                "a$x"
        ),
        DEAD_PATTERN_CARET(
                "a^x"
        ),
        ALL_DEAD_BRANCHES(
                "x(a$|^b)c"
        );

        private final String pattern;

        MatchingNothing(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }

        @Override
        public Path rootPath() {
            return Path.of("testdata/matches-nothing");
        }

        @Override
        public String extension() {
            return ".puml";
        }
    }

    public enum EdgeCasesPotentialTrouble implements FileTestUtils {
        REUSE_IN_GROUP_CARET("(a|^x)+ --> (\\1)"),
        REUSE_IN_GROUP_DOLLAR("(a$|x)+ --> (\\1)"),
        NO_GROUP_REPEAT("^x+");
        private final String pattern;

        EdgeCasesPotentialTrouble(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }

        @Override
        public Path rootPath() {
            return Path.of("testdata/edge-cases");
        }

        @Override
        public String extension() {
            return ".puml";
        }
    }

    /**
     * Optimization rules:
     * 1. Dead paths are removed
     * ....1.1. Dead paths are those that contain dollar or caret in the middle of expression
     * 2. Caret nodes are rewired from BEGIN, if not dead
     * 3. Dollar nodes are rewired to END, if not dead
     */
    public enum Optimizable implements FileTestUtils {
        DEAD_BRANCH_DOLLAR(
                "(a$|b)c",      // TODO: Mark branch as dead --> Since only 1 option remains - convert to group with only `b`
                "(b)c",         // b is still in group in case there are group references
                List.of("bc")
        ),
        DEAD_BRANCH_DOLLAR_KEEP_CHOICE(
                "(a$|b|x)c",      // TODO: Mark branch as dead --> Keep choice with one less option
                "(b|x)c",         // b is still in group in case there are group references
                List.of("bc", "xc")
        ),
        DEAD_BRANCH_CARET(
                "c(b|^a)",
                "c(a)",         // b is still in group in case there are group references
                List.of("ca")
        ),
        DEAD_BRANCH_CARET_KEEP_CHOICE(
                "c(b|^a|x)",
                "c(a|x)",         // b is still in group in case there are group references
                List.of("ca", "cx")
        ),
        DEAD_BRANCH_CARET_DOLLAR(
                "(^a$|b)c",
                "(b)c",         // b is still in group in case there are group references
                List.of("bc")
        ),
        LIVE_BRANCHES_REPEAT_DOLLAR(
                "(1$|x,){0,1}(2$|y,){0,1}",     // TODO: After 1$ there is a path without additional characters --> rewire 1$ to end directly, $2 should also be pointed to end directly
                "(1$|x,){0,1}(2$|y,){0,1}", // Pattern does not change, though $ nodes must point directly to END
                List.of("", "1", "x,", "x,2", "x,y,", "2", "y,")
        ),
        LIVE_BRANCHES_REPEAT_CARET(
                "(^1|x,){0,1}(^2|y,){0,1}",     // TODO: same, though START node should point to caret nodes directly
                "(^1|x,){0,1}(^2|y,){0,1}", // Pattern does not change, though ^ nodes are only accessible from START
                List.of("1", "1,", "1,2", "1,2,", "2", "2,")
        ),
        DEAD_BRANCHES_REPEAT_DOLLAR(
                "(1$|x,){0,1}(2$|y,)",      // TODO: 1$ is dead (removed) -->  group is created (x,)
                "(x,){0,1}(2$|y,)", // 1$ may never be used, because of mandatory second group
                List.of("2", "y,", "x,2", "x,y,")
        ),
        DEAD_BRANCHES_REPEAT_CARET(
                "(^1|x,)(^2|y,){0,1}",
                "(^1|x,)(y,){0,1}", // ^2 may never be used, because of mandatory first group
                List.of("1", "x,", "1y,", "x,y,")
        ),
        // FIXME: UNCOMMENT THOSE 2

        //        LIVEDEAD_REPEAT_CARET(
//                "(^a)+",        // TODO: impossible repeat detected --> replaced repeat with group
//                "(a)",      // Any subsequent repetition would break the match , 1+ repetitions transforms into group
//                List.of("a")
//        ),
//        LIVEDEAD_REPEAT_DOLLAR(
//                "(b$)*",
//                "(b)?", // Any more repetitions would break the match, 0+ repetitions transforms into 0 or 1
//                List.of("", "b")
//        ),
        DEAD_ON_REPEAT_CARET(
                "(a|^x){1,2}",      // TODO: impossible repeat is detected --> repeat of a choice with valid paths --> create non-capture group for first iteration, create partial group for remaining iterations
                "(?:a|^x)(a)?", // Non-capture group to keep correct group indexes
                List.of("a", "x", "aa", "xa")
        ),
        DEAD_ON_REPEAT_DOLLAR(
                "(a$|x){1,2}",
                "(?:x)?(a$|x)", // Non-capture group to keep correct group indexes
                List.of("a", "x", "xx", "xa")
        ),
        DEAD_ON_REPEAT_DOLLAR_CARET(
                "(a$|x|^y){1,3}",   // TODO: impossible paths found --> rewire ^ from start only, rewire $ to end directly. Only traveling from START to ^y should reduce counter from repeat .. looks very complex
                null,  // Note, I cannot now show equivalent pattern, this one is clearly wrong "(?:x|^y)?(?:x)(a$|x)",
                List.of("a", "x", "y", "xx", "yx", "xa", "ya", "xxx", "xxa", "yxx", "yxa")
        ),
        DEAD_ON_REPEAT_WITHOUT_REPEAT_DOLLAR(
                "(a$|x){2,2}",
                "(?:x){1}(a$|x)",    // Non-capture group to keep correct group indexes
                List.of("xa", "xx")
        ),
        DEAD_ON_REPEAT_WITHOUT_REPEAT_CARET(
                "(a|^x){2,2}",
                "(?:a|^x)(a){1}",    // Non-capture group to keep correct group indexes
                List.of("xa", "xx")
        );

        private final String pattern;
        private final String optimizedPattern;
        private final List<String> uniqueValues;

        Optimizable(String pattern, String optimizedPattern, List<String> uniqueValues) {
            this.pattern = pattern;
            this.optimizedPattern = optimizedPattern;
            this.uniqueValues = uniqueValues;
        }

        public String getPattern() {
            return pattern;
        }

        public String getOptimizedPattern() {
            return optimizedPattern;
        }

        public List<String> getUniqueValues() {
            return uniqueValues;
        }

        @Override
        public Path rootPath() {
            return Path.of("testdata/optimizable");
        }

        @Override
        public String extension() {
            return ".puml";
        }
    }

    public enum Optimal implements FileTestUtils {

        LIVE_BRANCH_DOLLAR(
                "c(a$|b)",
                List.of("ca", "cb")
        ),
        LIVE_BRANCH_CARET(
                "(^a|b)c",
                List.of("ac", "bc")
        ),
        LIVE_DOUBLE_START(
                "^(a|^b)",
                List.of("a", "b")
        ),
        LIVE_DOUBLE_END(
                "(a$|b)$",
                List.of("a", "b")
        );

        private final String pattern;
        private final List<String> uniqueValues;

        Optimal(String pattern, List<String> uniqueValues) {
            this.pattern = pattern;
            this.uniqueValues = uniqueValues;
        }

        public String getPattern() {
            return pattern;
        }

        public List<String> getAllUniqueValues() {
            return uniqueValues;
        }

        @Override
        public Path rootPath() {
            return Path.of("testdata/optimal");
        }

        @Override
        public String extension() {
            return ".puml";
        }
    }
}
