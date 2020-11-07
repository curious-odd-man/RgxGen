package com.github.curiousoddman.rgxgen.visitors;

import com.github.curiousoddman.rgxgen.config.RgxGenProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenerationVisitorBuilder {

    @FunctionalInterface
    public interface GenerationVisitorConstructor {
        GenerationVisitor construct(Random random, Map<Integer, String> groupsValues, RgxGenProperties properties);
    }

    private final GenerationVisitorConstructor aConstructor;

    private Random               aRandom;
    private Map<Integer, String> aGroupsValues;
    private RgxGenProperties     aProperties;

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

        return aConstructor.construct(aRandom, aGroupsValues, aProperties);
    }

    public GenerationVisitorBuilder withProperties(RgxGenProperties properties) {
        aProperties = properties;
        return this;
    }
}
