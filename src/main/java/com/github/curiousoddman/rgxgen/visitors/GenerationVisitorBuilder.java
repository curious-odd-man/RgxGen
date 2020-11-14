package com.github.curiousoddman.rgxgen.visitors;

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

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenerationVisitorBuilder {
    private final boolean aGenerateMatching;

    private Random               aRandom;
    private Map<Integer, String> aGroupsValues;
    private RgxGenProperties     aProperties;


    public GenerationVisitorBuilder(boolean generateMatching) {
        aGenerateMatching = generateMatching;
    }

    public GenerationVisitorBuilder withRandom(Random random) {
        aRandom = random;
        return this;
    }

    public GenerationVisitor get() {
        if (aRandom == null) {
            aRandom = new Random();
        }

        if (aGroupsValues == null) {
            aGroupsValues = new HashMap<>();
        }

        if (aGenerateMatching) {
            if (RgxGenOption.CASE_INSENSITIVE.getBooleanFromProperties(aProperties)) {
                return new GenerationVisitorCaseInsensitive(aRandom, aGroupsValues, aProperties);
            } else {
                return new GenerationVisitor(aRandom, aGroupsValues, aProperties);
            }
        } else {
            if (RgxGenOption.CASE_INSENSITIVE.getBooleanFromProperties(aProperties)) {
                return new NotMatchingCaseInsensitiveGenerationVisitor(aRandom, aGroupsValues, aProperties);
            } else {
                return new NotMatchingGenerationVisitor(aRandom, aGroupsValues, aProperties);
            }
        }
    }

    public GenerationVisitorBuilder withProperties(RgxGenProperties properties) {
        aProperties = properties;
        return this;
    }
}
