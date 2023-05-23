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

import lombok.Value;

/**
 * Range of symbols
 */
@Value(staticConstructor = "of")
public class SymbolRange {
    int from;
    int to;

    /**
     * Create range of symbols.
     *
     * @param from min character; shall be less than {@code to}
     * @param to   max character; shall be greater than {@code from}
     * @apiNote No verifications are done!
     */
    public static SymbolRange of(char from, char to) {
        return of((int) from, to);
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
