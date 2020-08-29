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

import com.github.curiousoddman.rgxgen.iterators.ChoiceIterator;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ChoiceIteratorSupplier implements Supplier<StringIterator> {

    private final List<List<Supplier<StringIterator>>> aStringIteratorsSuppliers;

    public ChoiceIteratorSupplier(List<List<Supplier<StringIterator>>> suppliers) {
        aStringIteratorsSuppliers = suppliers;
    }

    @Override
    public StringIterator get() {
        StringIterator[] stringIterators = aStringIteratorsSuppliers.stream()
                                                                          .flatMap(Collection::stream)
                                                                          .map(Supplier::get)
                                                                          .toArray(StringIterator[]::new);
        return new ChoiceIterator(stringIterators);
    }

    @Override
    public String toString() {
        return "ChoiceIteratorSupplier{" +
                aStringIteratorsSuppliers +
                '}';
    }
}
