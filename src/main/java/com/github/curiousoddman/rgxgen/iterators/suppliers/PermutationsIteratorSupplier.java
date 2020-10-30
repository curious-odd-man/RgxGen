package com.github.curiousoddman.rgxgen.iterators.suppliers;

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

import com.github.curiousoddman.rgxgen.iterators.PermutationsIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.List;
import java.util.function.Supplier;

public class PermutationsIteratorSupplier implements Supplier<StringIterator> {

    private final List<Supplier<StringIterator>> aSuppliers;

    public PermutationsIteratorSupplier(List<Supplier<StringIterator>> suppliers) {
        aSuppliers = suppliers;
    }

    @Override
    public StringIterator get() {
        if (aSuppliers.size() == 1) {
            return aSuppliers.get(0)
                             .get();
        } else {
            return new PermutationsIterator(aSuppliers);
        }
    }
}
