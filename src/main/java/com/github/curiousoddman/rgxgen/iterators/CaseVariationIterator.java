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

import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.TreeMap;

public class CaseVariationIterator extends StringIterator {
    private final String                    aOriginalValue;
    private final StringBuilder             aValue;
    private final TreeMap<Integer, Boolean> aSwitchableCharPositions;       // true - lower, false - upper case

    private int     aCurrentPos;
    private boolean hasNext;


    public CaseVariationIterator(String value) {
        aOriginalValue = value;
        aValue = new StringBuilder(value.toLowerCase());
        aSwitchableCharPositions = new TreeMap<>();
        OptionalInt currentPos = Util.indexOfNextCaseSensitiveCharacter(aValue, 0);
        while (currentPos.isPresent()) {
            aSwitchableCharPositions.put(currentPos.getAsInt(), true);
            currentPos = Util.indexOfNextCaseSensitiveCharacter(aValue, currentPos.getAsInt() + 1);
        }
        reset();
    }

    @Override
    protected String nextImpl() {
        if (!hasNext) {
            throw new NoSuchElementException("No more variations");
        }

        if (aSwitchableCharPositions.isEmpty()) {        // Only one possible value. No variations...
            hasNext = false;
            return aOriginalValue;
        } else if (aCurrentPos < 0) {       // First time return all lower-case value
            aCurrentPos = aSwitchableCharPositions.firstKey();
            return aValue.toString();
        } else {        // All other
            char currentChar = aValue.charAt(aCurrentPos);
            if (Character.isLowerCase(currentChar)) {
                aValue.setCharAt(aCurrentPos, Character.toUpperCase(currentChar));
                aSwitchableCharPositions.put(aCurrentPos, false);

                hasNext = aSwitchableCharPositions.values()
                                                  .stream()
                                                  .anyMatch(v -> v);

            } else {
                while (Character.isUpperCase(aValue.charAt(aCurrentPos))) {
                    aValue.setCharAt(aCurrentPos, Character.toLowerCase(aValue.charAt(aCurrentPos)));
                    aSwitchableCharPositions.put(aCurrentPos, true);
                    aCurrentPos = aSwitchableCharPositions.ceilingKey(aCurrentPos + 1);
                }
                aValue.setCharAt(aCurrentPos, Character.toUpperCase(aValue.charAt(aCurrentPos)));
                aSwitchableCharPositions.put(aCurrentPos, false);
                aCurrentPos = aSwitchableCharPositions.firstKey();
            }
            return aValue.toString();
        }
    }

    @Override
    public void reset() {
        aValue.replace(0, aOriginalValue.length(), aOriginalValue.toLowerCase());
        hasNext = true;
        aCurrentPos = -1;
    }

    @Override
    public String current() {
        return aValue.toString();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }
}
