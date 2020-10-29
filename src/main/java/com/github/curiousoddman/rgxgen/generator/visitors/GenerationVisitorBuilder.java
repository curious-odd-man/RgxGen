package com.github.curiousoddman.rgxgen.generator.visitors;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenerationVisitorBuilder {

    @FunctionalInterface
    public interface GenerationVisitorConstructor {
        GenerationVisitor construct(Random random, Map<Integer, String> groupsValues, Integer repeatLimit);
    }

    private final GenerationVisitorConstructor aConstructor;

    private Random               aRandom;
    private Map<Integer, String> aGroupsValues;
    private Integer              aRepeatLimit;

    public GenerationVisitorBuilder(GenerationVisitorConstructor constructor) {
        aConstructor = constructor;
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

        if (aRepeatLimit == null) {
            aRepeatLimit = 100;
        }

        return aConstructor.construct(aRandom, aGroupsValues, aRepeatLimit);
    }

    public GenerationVisitorBuilder withInfiniteRepetitions(int repeatLimit) {
        aRepeatLimit = repeatLimit;
        return this;
    }
}
