package com.github.curiousoddman.rgxgen.util.chars;

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


import com.github.curiousoddman.rgxgen.model.SymbolRange;

import java.util.List;
import java.util.stream.Stream;

public abstract class CharList {

    public static CharList rangeClosed(int from, int to) {
        char[] arr = new char[to - from + 1];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (char) (from + i);
        }
        return new CharArrayList(arr);
    }

    public static CharList charList(List<SymbolRange> symbolRanges, char... symbols) {
        int size = symbols.length + symbolRanges.stream().mapToInt(SymbolRange::size).sum();
        char[] arr = new char[size];
        System.arraycopy(symbols, 0, arr, 0, symbols.length);
        int index = symbols.length;
        for (SymbolRange symbolRange : symbolRanges) {
            for (int i = symbolRange.getFrom(); i <= symbolRange.getTo(); i++) {
                arr[index] = (char) (i);
                index += 1;
            }
        }
        return new CharArrayList(arr);
    }

    public static CharList charList(char... chars) {
        return new CharArrayList(chars);
    }

    public static CharList ofCapacity(int size) {
        return new CharArrayList(size);
    }

    public static CharList charList(String characterString) {
        return new CharArrayList(characterString.toCharArray());
    }

    public static CharList emptyUnmodifiable() {
        return new EmptyUnmodifiableCharList();
    }

    public static CharList empty() {
        return new CharArrayList(10);
    }

    public abstract CharList copy();

    public abstract void add(int c);

    public abstract Stream<Character> stream();

    public abstract void addAll(CharList originalSymbols);

    public abstract void addAll(char[] characters);

    public abstract void addAll(char[] srcArr, int srcLength);

    public abstract int size();

    public abstract char get(int index);

    public abstract boolean isEmpty();

    public abstract void sort();

    public abstract CharList except(CharPredicate predicate);

    public abstract boolean contains(char i);

    /**
     * Add elements of this list into the targetList
     *
     * @param targetList list that should be appended with elements of this list
     */
    public abstract void appendTo(CharList targetList);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CharList)) {
            return false;
        }

        CharList that = (CharList) o;

        if (size() != that.size()) {
            return false;
        }

        for (int i = 0; i < size(); i++) {
            if (get(i) != that.get(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size(); i++) {
            result = 31 * result + get(i);
        }

        result = 31 * result + size();
        return result;
    }
}
