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

import com.github.curiousoddman.rgxgen.validation.Validator;

import java.util.NoSuchElementException;

public class ValidatedIterator implements StringIterator {
    private final Validator      aValidator;
    private final StringIterator aStringIterator;

    private boolean isInitialized = false;
    private String  aCurrentValue = null;
    private boolean hasNext       = true;

    public ValidatedIterator(Validator validator, StringIterator stringIterator) {
        aValidator = validator;
        aStringIterator = stringIterator;
    }

    @Override
    public void reset() {
        isInitialized = false;
        hasNext = true;
        aCurrentValue = null;
        aStringIterator.reset();
    }

    @Override
    public String current() {
        return aCurrentValue;
    }

    private boolean findNextValid() {
        while (aStringIterator.hasNext()) {
            String next = aStringIterator.next();
            if (aValidator.isValid(next)) {
                return true;
            }
        }

        return false;
    }

    private void initialize() {
        isInitialized = true;
        boolean hasValid = findNextValid();
        if (hasValid) {
            aCurrentValue = aStringIterator.current();
            hasNext = findNextValid();
        }
    }

    @Override
    public boolean hasNext() {
        if (isInitialized) {
            return hasNext;
        } else {
            initialize();
            return aCurrentValue != null;
        }
    }

    @Override
    public String next() {
        if (isInitialized) {
            aCurrentValue = aStringIterator.current();
            hasNext = findNextValid();
        } else {
            initialize();
            if (aCurrentValue == null) {
                throw new NoSuchElementException("No texts can be produced by this pattern.");
            }
        }
        return aCurrentValue;
    }
}
