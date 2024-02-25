package com.github.curiousoddman.rgxgen.visitors.helpers;

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


import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.nodes.SymbolSet;
import com.github.curiousoddman.rgxgen.util.chars.CharList;

import java.util.Arrays;
import java.util.List;

public class SymbolSetIndexer {
    private final CharList          symbols;
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
                int rangeSize = symbolRange.size();
                tmpSize += rangeSize;
                int offset = currentOffset + rangeSize;
                rangeOffsets[i + 1] = offset;
                currentOffset = offset;
            }
            tmpSize += symbolRanges.get(rangesCount - 1).size();
        } else {
            rangeOffsets = new int[0];
        }
        size = tmpSize;
    }

    public int size() {
        return size;
    }

    public char[] getAll() {
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            chars[i] = get(i);
        }
        return chars;
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
