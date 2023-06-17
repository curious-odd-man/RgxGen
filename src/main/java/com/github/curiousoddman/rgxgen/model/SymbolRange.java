package com.github.curiousoddman.rgxgen.model;

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

/**
 * Range of symbols
 */
public class SymbolRange {
    private final int from;
    private final int to;

    /**
     * Create inclusive range of symbols.
     *
     * @param from min character; shall be less than {@code to}
     * @param to   max character; shall be greater than {@code from}
     * @apiNote No verifications are done!
     */
    public static SymbolRange range(int from, int to) {
        return new SymbolRange(from, to);
    }

    /**
     * Create inclusive range of symbols.
     *
     * @param from min character; shall be less than {@code to}
     * @param to   max character; shall be greater than {@code from}
     * @apiNote No verifications are done!
     */
    public static SymbolRange range(char from, char to) {
        return range((int) from, to);
    }

    private SymbolRange(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int size() {
        return to - from + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SymbolRange range = (SymbolRange) o;

        if (from != range.from) {
            return false;
        }
        return to == range.to;
    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        return result;
    }

    @Override
    public String toString() {
        return "SymbolRange{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
