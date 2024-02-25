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

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.ZERO_LENGTH_CHARACTER_ARRAY;

public class EmptyUnmodifiableCharList extends CharList {
    @Override
    public CharList copy() {
        return this;
    }

    @Override
    public void add(int c) {
        throw new UnsupportedOperationException("Unmodifiable CharList!");
    }

    @Override
    public Stream<Character> stream() {
        return Stream.empty();
    }

    @Override
    public void addAll(CharList originalSymbols) {
        throw new UnsupportedOperationException("Unmodifiable CharList!");
    }

    @Override
    public void addAll(char[] characters) {
        throw new UnsupportedOperationException("Unmodifiable CharList!");
    }

    @Override
    public void addAll(char[] srcArr, int srcLength) {
        throw new UnsupportedOperationException("Unmodifiable CharList!");
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public char get(int index) {
        throw new NoSuchElementException("Empty unmodifiable CharList");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void sort() {

    }

    @Override
    public CharList except(CharPredicate predicate) {
        return this;
    }

    @Override
    public boolean contains(char i) {
        return false;
    }

    @Override
    public void appendTo(CharList targetList) {

    }

    @Override
    public String toString() {
        return Arrays.toString(ZERO_LENGTH_CHARACTER_ARRAY);
    }
}
