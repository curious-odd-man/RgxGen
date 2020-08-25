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

import java.util.Iterator;

public abstract class StringIterator implements Iterator<String> {
    @SuppressWarnings("IteratorNextCanNotThrowNoSuchElementException")
    @Override
    public String next() {
        return nextImpl();
    }

    /**
     * This method returns correct value only on top level iterator.
     * For other iterators 2 steps are required - next() and then current().
     *
     * @return next String.
     */
    protected abstract String nextImpl();

    public abstract void reset();

    public abstract String current();
}
