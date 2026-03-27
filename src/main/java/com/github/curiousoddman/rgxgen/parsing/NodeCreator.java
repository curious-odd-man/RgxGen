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
import com.github.curiousoddman.rgxgen.parsing.dflt.flags.ParsingFlags;
import com.github.curiousoddman.rgxgen.util.chars.CharList;

import java.util.List;

public interface NodeCreator {
    FinalSymbol finalSymbol(String pattern, ParsingFlags parsingFlags);

    NotSymbol notSymbol(Node node);

    GroupRef groupRef(String pattern, int groupIndex);

    Repeat repeat(String pattern, Node repeatNode, int repeatTimes);

    Repeat repeat(String pattern, Node repeatNode, int min, int max);

    Repeat repeatMinimum(String pattern, Node repeatNode, int repeatMinTimes);

    Choice choice(String pattern, Node[] alternatives);

    Sequence sequence(String pattern, Node[] nodes);

    Group group(String pattern, Integer captureGroupIndex, Node node);

    SymbolSet dotPatternSymbolSet(RgxGenProperties properties);

    SymbolSet asciiSymbolSet(String pattern, RgxGenCharsDefinition positiveMatchDefinitions, RgxGenCharsDefinition negativeMatchDefinitions, MatchType matchType);

    SymbolSet unicodeSymbolSet(String pattern, RgxGenCharsDefinition positiveMatchDefinitions, RgxGenCharsDefinition negativeMatchDefinitions, MatchType matchType);

    SymbolSet asciiRangesSymbolSet(String pattern, List<SymbolRange> symbolRanges, MatchType matchType);

    SymbolSet asciiSymbolSet(String pattern, List<SymbolRange> asciiWordCharRanges, CharList charList, MatchType matchType);

    SymbolSet unicodeCharacterClassSymbolSet(String pattern, UnicodeCategory unicodeCategory, MatchType matchType);
}
