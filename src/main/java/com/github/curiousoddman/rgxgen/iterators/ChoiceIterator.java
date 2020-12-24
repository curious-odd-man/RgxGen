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

import java.util.NoSuchElementException;

public class ChoiceIterator implements StringIterator {
    private final StringIterator[] aIterators;

    private int aCurrentIteratorIndex;

    public ChoiceIterator(StringIterator[] iterators) {
        aIterators = iterators;
    }

    @Override
    public boolean hasNext() {
        return aIterators[aCurrentIteratorIndex].hasNext() || aCurrentIteratorIndex + 1 < aIterators.length;
    }

    @Override
    public String next() {
        if (!aIterators[aCurrentIteratorIndex].hasNext()) {
            ++aCurrentIteratorIndex;
            if (aCurrentIteratorIndex >= aIterators.length) {
                throw new NoSuchElementException("No more values");
            }
        }

        return aIterators[aCurrentIteratorIndex].next();
    }

    @Override
    public String current() {
        return aIterators[aCurrentIteratorIndex].current();
    }

    @Override
    public void reset() {
        aCurrentIteratorIndex = 0;
        for (StringIterator iterator : aIterators) {
            iterator.reset();
        }
    }
}
