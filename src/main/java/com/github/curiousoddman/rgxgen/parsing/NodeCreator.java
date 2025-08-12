package com.github.curiousoddman.rgxgen.parsing;

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

import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.model.MatchType;
import com.github.curiousoddman.rgxgen.model.RgxGenCharsDefinition;
import com.github.curiousoddman.rgxgen.model.SymbolRange;
import com.github.curiousoddman.rgxgen.model.UnicodeCategory;
import com.github.curiousoddman.rgxgen.nodes.*;
import com.github.curiousoddman.rgxgen.util.chars.CharList;

import java.util.List;

public interface NodeCreator {
    FinalSymbol makeFinalSymbol(String pattern);

    NotSymbol makeNotSymbol(Node node);

    GroupRef makeGroupRef(String pattern, int groupIndex);

    Repeat makeRepeat(String pattern, Node repeatNode, int repeatTimes);

    Repeat makeRepeat(String pattern, Node repeatNode, int min, int max);

    Repeat makeRepeatMinimum(String pattern, Node repeatNode, int repeatMinTimes);

    Choice makeChoice(String pattern, Node[] alternatives);

    Sequence makeSequence(String pattern, Node[] nodes);

    Group makeGroup(String pattern, Integer captureGroupIndex, Node node);

    SymbolSet ofDotPattern(RgxGenProperties properties);

    SymbolSet ofAscii(String pattern, RgxGenCharsDefinition positiveMatchDefinitions, RgxGenCharsDefinition negativeMatchDefinitions, MatchType matchType);

    SymbolSet ofUnicode(String pattern, RgxGenCharsDefinition positiveMatchDefinitions, RgxGenCharsDefinition negativeMatchDefinitions, MatchType matchType);

    SymbolSet ofAsciiRanges(String pattern, List<SymbolRange> symbolRanges, MatchType matchType);

    SymbolSet ofAscii(String pattern, List<SymbolRange> asciiWordCharRanges, CharList charList, MatchType matchType);

    SymbolSet ofUnicodeCharacterClass(String pattern, UnicodeCategory unicodeCategory, MatchType matchType);
}
