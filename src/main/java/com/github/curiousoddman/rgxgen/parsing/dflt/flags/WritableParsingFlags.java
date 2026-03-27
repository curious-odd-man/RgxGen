package com.github.curiousoddman.rgxgen.parsing.dflt.flags;

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

import java.util.EnumSet;
import java.util.Set;

public class WritableParsingFlags implements ParsingFlags {
    final Set<ParsingFlag> flags = EnumSet.noneOf(ParsingFlag.class);

    public static WritableParsingFlags parsingFlags() {
        return new WritableParsingFlags();
    }

    public static ParsingFlags caretAndDollar() {
        return parsingFlags().withCaret().withDollar();
    }

    public WritableParsingFlags withCaret() {
        flags.add(ParsingFlag.HAS_CARET);
        return this;
    }

    public WritableParsingFlags withDollar() {
        flags.add(ParsingFlag.HAS_DOLLAR);
        return this;
    }

    public WritableParsingFlags withChoice() {
        flags.add(ParsingFlag.IS_CHOICE);
        return this;
    }

    @Override
    public String toString() {
        if (flags.isEmpty()) {
            return "";
        }
        return "flags=" + flags;
    }

    public boolean isChoice() {
        return flags.contains(ParsingFlag.IS_CHOICE);
    }

    @Override
    public boolean hasDollar() {
        return flags.contains(ParsingFlag.HAS_DOLLAR);
    }

    @Override
    public boolean hasCaret() {
        return flags.contains(ParsingFlag.HAS_CARET);
    }

    @Override
    public ParsingFlags copy() {
        WritableParsingFlags writableParsingFlags = new WritableParsingFlags();
        writableParsingFlags.flags.addAll(flags);
        return writableParsingFlags;
    }

    @Override
    public void keepOnlyChoice() {
        flags.removeIf(f -> !f.equals(ParsingFlag.IS_CHOICE));
    }
}
