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
import java.util.stream.Stream;

/**
 * implementation copied from java.util.ArrayList
 */
public class CharArrayList extends CharList {
    private char[] elementData;
    private int    size;

    public CharArrayList(char[] arr) {
        elementData = arr;
        size = arr.length;
    }

    public CharArrayList(int size) {
        elementData = new char[size];
        this.size = 0;
    }

    public CharArrayList(char[] arr, int size) {
        elementData = arr;
        this.size = size;
    }

    @Override
    public CharList copy() {
        char[] arr = new char[size];
        System.arraycopy(elementData, 0, arr, 0, size);
        return new CharArrayList(arr);
    }

    @Override
    public void add(int c) {
        if (size == elementData.length) {
            grow();
        }
        elementData[size] = (char) c;
        size += 1;
    }

    @Override
    public Stream<Character> stream() {
        return new String(elementData, 0, size).chars().mapToObj(i -> (char) i);
    }

    @Override
    public void addAll(CharList charList) {
        charList.appendTo(this);
    }

    @Override
    public void addAll(char[] srcArr) {
        addAll(srcArr, srcArr.length);
    }

    public void addAll(char[] srcArr, int srcLength) {
        if (srcLength > elementData.length - size) {
            grow(size + srcLength);
        }
        System.arraycopy(srcArr, 0, elementData, size, srcLength);
        size += srcLength;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public char get(int index) {
        return elementData[index];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void sort() {
        Arrays.sort(elementData, 0, size);
    }

    @Override
    public CharList except(CharPredicate predicate) {
        char[] arr = new char[size];
        int filteredSize = 0;
        for (char e : elementData) {
            if (!predicate.test(e)) {
                arr[filteredSize] = e;
                filteredSize += 1;
            }
        }
        return new CharArrayList(arr, filteredSize);
    }

    @Override
    public boolean contains(char ch) {
        for (char e : elementData) {
            if (e == ch) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendTo(CharList targetList) {
        targetList.addAll(elementData, size);
    }

    private void grow() {
        grow(size + 1);
    }

    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = newLength(oldCapacity, minCapacity - oldCapacity, oldCapacity >> 1);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    @Override
    public String toString() {
        if (elementData == null) {
            return "null";
        }
        int iMax = size - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(elementData[i]);
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    private static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
        if (prefLength <= Integer.MAX_VALUE - 8) {
            return prefLength;
        } else {
            return oldLength + Math.min(minGrowth, prefLength);
        }
    }
}
