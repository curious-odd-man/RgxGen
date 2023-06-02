package com.github.curiousoddman.rgxgen.visitors.helpers;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.nodes.SymbolSet;

import java.util.Arrays;
import java.util.List;

public class SymbolSetIndexer {
    private final List<Character>   symbols;
    private final List<SymbolRange> symbolRanges;
    private final int[]             rangeOffsets;
    private final int               size;

    public SymbolSetIndexer(SymbolSet symbolSet) {
        symbols = symbolSet.getSymbols();
        symbolRanges = symbolSet.getSymbolRanges();

        int tmpSize = symbols.size();

        int rangesCount = symbolRanges.size();
        if (rangesCount > 0) {
            rangeOffsets = new int[rangesCount];
            int currentOffset = 0;
            rangeOffsets[0] = currentOffset;
            for (int i = 0; i < rangesCount - 1; ++i) {
                SymbolRange symbolRange = symbolRanges.get(i);
                int rangeSize = getRangeSize(symbolRange);
                tmpSize += rangeSize;
                int offset = currentOffset + rangeSize;
                rangeOffsets[i + 1] = offset;
                currentOffset = offset;
            }
            tmpSize += getRangeSize(symbolRanges.get(rangesCount - 1));
        } else {
            rangeOffsets = new int[0];
        }
        size = tmpSize;
    }

    private static int getRangeSize(SymbolRange symbolRange) {
        return symbolRange.getTo() - symbolRange.getFrom() + 1;
    }

    public int size() {
        return size;
    }

    public char get(int seed) {
        if (seed < symbols.size()) {
            return symbols.get(seed);
        }

        seed -= symbols.size();

        if (seed == 0) {
            return (char) (symbolRanges.get(0).getFrom());
        }
        int i = findRangeIndex(seed);
        int offset = rangeOffsets[i];
        return (char) (symbolRanges.get(i).getFrom() + seed - offset);
    }

    private int findRangeIndex(int seed) {
        int i = Arrays.binarySearch(rangeOffsets, seed);
        if (i > 0) {
            return i;
        } else {
            return -i - 2;
        }
    }
}
