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

public class ArrayIterator implements StringIterator {

    private final int         aMaxIndex;
    private final Character[] aStrings;

    private int aIndex = -1;

    public ArrayIterator(Character[] strings) {
        aStrings = strings;
        aMaxIndex = aStrings.length - 1;        // Because of prefix increment in nextImpl()
    }

    @Override
    public boolean hasNext() {
        return aIndex < aMaxIndex;
    }

    @Override
    public String next() {
        ++aIndex;
        if (aIndex >= aStrings.length) {
            throw new NoSuchElementException("Not enough elements in arrays");
        } else {
            return aStrings[aIndex].toString();
        }
    }

    @Override
    public void reset() {
        aIndex = -1;
    }

    @Override
    public String current() {
        return aStrings[aIndex].toString();
    }
}
