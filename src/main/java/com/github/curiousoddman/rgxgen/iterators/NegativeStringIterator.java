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

import java.util.regex.Pattern;

public class NegativeStringIterator extends StringIterator {
    private final StringIterator aIterator;
    private final Pattern        aPattern;

    private String aValue;

    public NegativeStringIterator(StringIterator iterator, Pattern pattern) {
        aIterator = iterator;
        aPattern = pattern;
    }

    @Override
    protected String nextImpl() {
        do {
            aValue = aIterator.next();
        } while (aPattern.matcher(aValue)
                         .find());
        return aValue;
    }

    @Override
    public void reset() {
        // Nothing to do
    }

    @Override
    public String current() {
        return aValue;
    }

    @Override
    public boolean hasNext() {
        return true;
    }
}
