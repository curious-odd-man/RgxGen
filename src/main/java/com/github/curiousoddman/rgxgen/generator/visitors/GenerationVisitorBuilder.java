package com.github.curiousoddman.rgxgen.generator.visitors;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenerationVisitorBuilder {

    @FunctionalInterface
    public interface GenerationVisitorConstructor {
        GenerationVisitor construct(Random random, Map<Integer, String> groupsValues);
    }

    private final GenerationVisitorConstructor aConstructor;

    private Random               aRandom;
    private Map<Integer, String> aGroupsValues;

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

        return aConstructor.construct(aRandom, aGroupsValues);
    }
}
