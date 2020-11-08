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

import com.github.curiousoddman.rgxgen.util.Util;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.NoSuchElementException;

public class CaseVariationIterator extends StringIterator {
    private final String        aOriginalValue;
    private final StringBuilder aValue;
    private final int           aLastCaseSensitiveCharacter;
    private final BitSet aBitSet;

    private int     aCurrentPos;
    private int     aNextPos;
    private boolean hasNext;



    public CaseVariationIterator(String value) {
        aOriginalValue = value;
        aValue = new StringBuilder(value.toLowerCase());
        aLastCaseSensitiveCharacter = Util.indexOfLastCaseSensitiveCharacter(aValue);
        BigInteger bigInteger = Util.countCaseInsensitiveVariations(aValue);
        aBitSet = new BitSet(bigInteger.intValueExact());
        reset();
    }

    @Override
    protected String nextImpl() {
        if (!hasNext) {
            throw new NoSuchElementException();
        } else if (aLastCaseSensitiveCharacter < 0) {
            hasNext = false;
            return aOriginalValue;
        } else {
            char currentChar = aValue.charAt(aCurrentPos);
            if (Character.isLowerCase(currentChar)) {
                aValue.setCharAt(aCurrentPos, Character.toUpperCase(currentChar));
                aNextPos = Util.indexOfNextCaseSensitiveCharacter(aValue, aCurrentPos);
                return aValue.toString();
            } else {


                aValue.setCharAt(aCurrentPos, Character.toLowerCase(currentChar));
            }
        }

        return null;
    }

    @Override
    public void reset() {
        aValue.replace(0, aOriginalValue.length(), aOriginalValue.toLowerCase());
        aBitSet.clear();
        aCurrentPos = Util.indexOfNextCaseSensitiveCharacter(aValue, 0);
        hasNext = true;
    }

    @Override  // FIXME: Can we return CharSequence here?? Can we in general move from String to CharSequence internally to allow using StringBuilder and String interchangingly??
    public String current() {
        return aValue.toString();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }
}
