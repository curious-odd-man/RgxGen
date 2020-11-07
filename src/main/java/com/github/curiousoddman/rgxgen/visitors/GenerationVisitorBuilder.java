package com.github.curiousoddman.rgxgen.visitors;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenerationVisitorBuilder {
    private Random               aRandom;
    private Map<Integer, String> aGroupsValues;
    private RgxGenProperties     aProperties;
    private boolean              aGenerateMatching;

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
