package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generate Any printable character.
 */
public class SymbolSet implements Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(SymbolSet.class);

    private static final String[] ALL_SYMBOLS = new String[127 - 32];

    public static String[] getAllSymbols() {
        return ALL_SYMBOLS.clone();
    }

    static {
        for (char c = 32; c < 127; ++c) {
            ALL_SYMBOLS[c - 32] = Character.valueOf(c)
                                           .toString();
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("All symbols {} ", Arrays.asList(ALL_SYMBOLS));
        }
    }

    public static class SymbolRange {
        private final int aFrom;
        private final int aTo;

        public SymbolRange(char from, char to) {
            aFrom = from;
            aTo = to;
        }

        @Override
        public String toString() {
            return "SymbolRange{" +
                    aFrom +
                    ':' + aTo +
                    '}';
        }
    }

    private final String[] aSymbols;

    /**
     * Symbol set containing all symbols
     */
    public SymbolSet() {
        this(ALL_SYMBOLS.clone(), true);
    }

    public SymbolSet(String[] symbols, boolean positive) {
        this(Collections.emptyList(), symbols, positive);
    }

    public SymbolSet(List<SymbolRange> symbolRanges, boolean positive) {
        this(symbolRanges, new String[0], positive);
    }

    public SymbolSet(List<SymbolRange> symbolRanges, String[] symbols, boolean positive) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Creating [positive = {}] from {} and {}", positive, symbolRanges, Arrays.asList(symbols));
        }

        List<String> initial = new ArrayList<>();
        if (!positive) {
            initial.addAll(Arrays.asList(ALL_SYMBOLS));
        }

        filterOrPut(initial, Arrays.asList(symbols), positive);
        filterOrPut(initial, symbolRanges.stream()
                                         .flatMapToInt(r -> IntStream.rangeClosed(r.aFrom, r.aTo))
                                         .mapToObj(i -> (char) i)
                                         .map(Object::toString)
                                         .collect(Collectors.toList()), positive);

        aSymbols = initial.toArray(new String[0]);
    }

    private static void filterOrPut(List<String> input, List<String> symbols, boolean put) {
        if (put) {
            input.addAll(symbols);
        } else {
            input.removeIf(symbols::contains);
        }
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public String[] getSymbols() {
        return aSymbols;
    }

    @Override
    public String toString() {
        return "SymbolSet{" + Arrays.toString(aSymbols) + '}';
    }
}
