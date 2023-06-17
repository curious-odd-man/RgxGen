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


import com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider;
import com.github.curiousoddman.rgxgen.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RgxGenCharsDefinition {
    private final List<SymbolRange> rangeList;
    private final List<Character>   characters;

    public static RgxGenCharsDefinition of(UnicodeCategory category) {
        return new RgxGenCharsDefinition(category.getSymbolRanges(), Arrays.asList(category.getSymbols()));
    }

    public static RgxGenCharsDefinition of(SymbolRange... ranges) {
        return new RgxGenCharsDefinition(Arrays.asList(ranges), Collections.emptyList());
    }

    public static RgxGenCharsDefinition of(Character... characters) {
        return new RgxGenCharsDefinition(Collections.emptyList(), Arrays.asList(characters));
    }

    public static RgxGenCharsDefinition of(CharSequence characterString) {
        List<Character> characterList = characterString.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        List<SymbolRange> compactedRanges = new ArrayList<>();
        List<Character> compactedSymbols = new ArrayList<>();
        Util.compactOverlappingRangesAndSymbols(Collections.emptyList(), characterList, compactedRanges, compactedSymbols);
        return new RgxGenCharsDefinition(compactedRanges, compactedSymbols);
    }

    public static RgxGenCharsDefinition of(List<SymbolRange> symbolRanges, Character[] symbols) {
        return of(symbolRanges, Arrays.asList(symbols));
    }

    public static RgxGenCharsDefinition of(List<SymbolRange> symbolRanges, List<Character> symbols) {
        return new RgxGenCharsDefinition(symbolRanges, symbols);
    }

    public static RgxGenCharsDefinition of(RgxGenCharsDefinition other) {
        return of(other.rangeList, other.characters);
    }

    public RgxGenCharsDefinition withRanges(SymbolRange... ranges) {
        rangeList.addAll(Arrays.asList(ranges));
        return this;
    }

    public RgxGenCharsDefinition withRanges(List<SymbolRange> ranges) {
        rangeList.addAll(ranges);
        return this;
    }

    public RgxGenCharsDefinition withCharacters(Character... characters) {
        this.characters.addAll(Arrays.asList(characters));
        return this;
    }

    public RgxGenCharsDefinition withCharacters(List<Character> characters) {
        this.characters.addAll(characters);
        return this;
    }

    private RgxGenCharsDefinition(List<SymbolRange> rangeList, List<Character> characters) {
        this.rangeList = new ArrayList<>(rangeList);
        this.characters = new ArrayList<>(characters);
    }

    public boolean isAsciiOnly() {
        return Stream.concat(
                             rangeList.stream().map(SymbolRange::getTo),
                             characters.stream().map(c -> (int) c)
                     )
                     .noneMatch(i -> i >= ConstantsProvider.DEL_ASCII_CODE);
    }

    public List<SymbolRange> getRangeList() {
        return rangeList;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void addAll(RgxGenCharsDefinition other) {
        rangeList.addAll(other.rangeList);
        characters.addAll(other.characters);
    }

    @Override
    public String toString() {
        return "RgxGenCharsDefinition{" +
                "rangeList=" + rangeList +
                ", characters=" + characters +
                '}';
    }
}
