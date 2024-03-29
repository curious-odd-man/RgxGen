package com.github.curiousoddman.rgxgen.iterators;

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

import com.github.curiousoddman.rgxgen.visitors.helpers.SymbolSetIndexer;

import java.util.NoSuchElementException;

public class IndexIterator implements StringIterator {
    private final int              maxIndex;
    private final SymbolSetIndexer symbolSetIndexer;

    private int index = -1;

    public IndexIterator(SymbolSetIndexer symbolSetIndexer) {
        this.symbolSetIndexer = symbolSetIndexer;
        maxIndex = symbolSetIndexer.size() - 1;        // Because of prefix increment in nextImpl()
    }

    @Override
    public boolean hasNext() {
        return index < maxIndex;
    }

    @Override
    public String next() {
        ++index;
        if (index >= symbolSetIndexer.size()) {
            throw new NoSuchElementException("Not enough elements in arrays");
        } else {
            return String.valueOf(symbolSetIndexer.get(index));
        }
    }

    @Override
    public void reset() {
        index = -1;
    }

    @Override
    public String current() {
        return String.valueOf(symbolSetIndexer.get(index));
    }
}
