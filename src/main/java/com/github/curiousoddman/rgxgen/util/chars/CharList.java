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
            for (int i = symbolRange.from(); i <= symbolRange.to(); i++) {
                arr[index] = (char) (i);
                index += 1;
            }
        }
        return new CharArrayList(arr);
    }

    /**
     * Creates a {@link CharList} containing the given characters.
     *
     * @param chars the characters to include in the list
     * @return a new {@link CharList} initialized with the provided characters
     */
    public static CharList charList(char... chars) {
        return new CharArrayList(chars);
    }

    /**
     * Creates a {@link CharList} with the specified initial capacity.
     *
     * @param size the initial capacity of the list
     * @return a new {@link CharList} with the given capacity
     */
    public static CharList ofCapacity(int size) {
        return new CharArrayList(size);
    }

    /**
     * Creates a {@link CharList} from a {@link String}.
     *
     * @param characterString the string whose characters will populate the list
     * @return a new {@link CharList} containing characters from the string
     */
    public static CharList charList(String characterString) {
        return new CharArrayList(characterString.toCharArray());
    }

    /**
     * Returns an empty, unmodifiable {@link CharList}.
     *
     * @return an empty unmodifiable {@link CharList}
     */
    public static CharList emptyUnmodifiable() {
        return new EmptyUnmodifiableCharList();
    }

    /**
     * Creates an empty {@link CharList} with default capacity.
     *
     * @return a new empty {@link CharList}
     */
    public static CharList empty() {
        return new CharArrayList(10);
    }

    /**
     * Creates a copy of this {@link CharList}.
     *
     * @return a new {@link CharList} containing the same elements
     */
    public abstract CharList copy();

    /**
     * Adds a character to the list.
     *
     * @param c the character to add
     */
    public abstract void add(int c);

    /**
     * Returns a sequential {@link Stream} of characters in this list.
     *
     * @return a stream of {@link Character} elements
     */
    public abstract Stream<Character> stream();

    /**
     * Adds all elements from another {@link CharList}.
     *
     * @param originalSymbols the source list whose elements will be added
     */
    public abstract void addAll(CharList originalSymbols);

    /**
     * Adds all characters from the given array.
     *
     * @param characters the array of characters to add
     */
    public abstract void addAll(char[] characters);

    /**
     * Adds characters from the given array up to the specified length.
     *
     * @param srcArr    the source array
     * @param srcLength the number of characters to add from the array
     */
    public abstract void addAll(char[] srcArr, int srcLength);

    /**
     * Returns the number of elements in the list.
     *
     * @return the size of the list
     */
    public abstract int size();

    /**
     * Returns the character at the specified index.
     *
     * @param index the index of the character to retrieve
     * @return the character at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public abstract char get(int index);

    /**
     * Checks whether the list is empty.
     *
     * @return {@code true} if the list contains no elements, otherwise {@code false}
     */
    public abstract boolean isEmpty();

    /**
     * Sorts the elements of this list in ascending order.
     */
    public abstract void sort();

    /**
     * Returns a new {@link CharList} containing elements that do not match the given predicate.
     *
     * @param predicate the condition used to filter elements
     * @return a new {@link CharList} excluding elements that satisfy the predicate
     */
    public abstract CharList except(CharPredicate predicate);

    /**
     * Checks whether the list contains the specified character.
     *
     * @param i the character to check
     * @return {@code true} if the character is present, otherwise {@code false}
     */
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
        if (!(o instanceof CharList that)) {
            return false;
        }

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
