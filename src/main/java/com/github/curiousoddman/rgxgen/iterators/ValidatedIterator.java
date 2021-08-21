package com.github.curiousoddman.rgxgen.iterators;

import com.github.curiousoddman.rgxgen.validation.Validator;

import java.util.List;
import java.util.NoSuchElementException;

public class ValidatedIterator implements StringIterator {
    private final List<Validator> aValidatorList;
    private final StringIterator  aStringIterator;

    private boolean isInitialized = false;
    private String  aCurrentValue = null;
    private boolean hasNext       = true;

    public ValidatedIterator(List<Validator> validatorList, StringIterator stringIterator) {
        aValidatorList = validatorList;
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
            if (aValidatorList.stream()
                              .allMatch(v -> v.validate(next))) {
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
