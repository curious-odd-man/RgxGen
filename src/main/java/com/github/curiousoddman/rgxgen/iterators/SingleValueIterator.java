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

public class SingleValueIterator extends StringIterator {
    private final String aValue;

    private boolean hasNext;

    public SingleValueIterator() {
        this("");
    }

    public SingleValueIterator(String s) {
        aValue = s;
        hasNext = true;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public String nextImpl() {
        hasNext = false;
        return aValue;
    }

    @Override
    public void reset() {
        hasNext = true;
    }

    @Override
    public String current() {
        return aValue;
    }

    @Override
    public String toString() {
        return "SingleValueIterator{" +
                "aValue='" + aValue + '\'' +
                '}';
    }
}
