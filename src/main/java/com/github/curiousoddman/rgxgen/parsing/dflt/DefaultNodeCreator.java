package com.github.curiousoddman.rgxgen.parsing.dflt;

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
import com.github.curiousoddman.rgxgen.parsing.NodeCreator;
import com.github.curiousoddman.rgxgen.util.chars.CharList;

import java.util.List;

public class DefaultNodeCreator implements NodeCreator {
    @Override
    public FinalSymbol makeFinalSymbol(String pattern) {
        return new FinalSymbol(pattern);
    }

    @Override
    public NotSymbol makeNotSymbol(Node node) {
        return new NotSymbol(node.getPattern(), node);
    }

    @Override
    public GroupRef makeGroupRef(String pattern, int groupIndex) {
        return new GroupRef(pattern, groupIndex);
    }

    @Override
    public Repeat makeRepeat(String pattern, Node repeatNode, int repeatTimes) {
        return new Repeat(pattern, repeatNode, repeatTimes);
    }

    @Override
    public Repeat makeRepeat(String pattern, Node repeatNode, int min, int max) {
        return new Repeat(pattern, repeatNode, min, max);
    }

    @Override
    public Repeat makeRepeatMinimum(String pattern, Node repeatNode, int repeatMinTimes) {
        return new Repeat(pattern, repeatNode, repeatMinTimes, -1);
    }

    @Override
    public Choice makeChoice(String pattern, Node[] alternatives) {
        return new Choice(pattern, alternatives);
    }

    @Override
    public Sequence makeSequence(String pattern, Node[] nodes) {
        return new Sequence(pattern, nodes);
    }

    @Override
    public Group makeGroup(String pattern, Integer captureGroupIndex, Node node) {
        return new Group(pattern, captureGroupIndex, node);
    }

    @Override
    public SymbolSet ofDotPattern(RgxGenProperties properties) {
        return SymbolSet.ofDotPattern(properties);
    }

    @Override
    public SymbolSet ofAscii(String pattern, RgxGenCharsDefinition positiveMatchDefinitions, RgxGenCharsDefinition negativeMatchDefinitions, MatchType matchType) {
        return SymbolSet.ofAscii(pattern, positiveMatchDefinitions, negativeMatchDefinitions, matchType);
    }

    @Override
    public SymbolSet ofUnicode(String pattern, RgxGenCharsDefinition positiveMatchDefinitions, RgxGenCharsDefinition negativeMatchDefinitions, MatchType matchType) {
        return SymbolSet.ofUnicode(pattern, positiveMatchDefinitions, negativeMatchDefinitions, matchType);
    }

    @Override
    public SymbolSet ofAsciiRanges(String pattern, List<SymbolRange> symbolRanges, MatchType matchType) {
        return SymbolSet.ofAsciiRanges(pattern, symbolRanges, matchType);
    }

    @Override
    public SymbolSet ofAscii(String pattern, List<SymbolRange> asciiWordCharRanges, CharList charList, MatchType matchType) {
        return SymbolSet.ofAscii(pattern, asciiWordCharRanges, charList, matchType);
    }

    @Override
    public SymbolSet ofUnicodeCharacterClass(String pattern, UnicodeCategory unicodeCategory, MatchType matchType) {
        return SymbolSet.ofUnicodeCharacterClass(pattern, unicodeCategory, matchType);
    }
}
