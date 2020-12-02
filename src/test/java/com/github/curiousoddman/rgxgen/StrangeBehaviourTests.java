package com.github.curiousoddman.rgxgen;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

public class StrangeBehaviourTests {

    @Test
    @Ignore
    public void randomIsNotSoRandomTest() {
        // Note! This works only when LIMIT_VALUE is a power of 2!
        final int LIMIT_VALUE = 32;
        for (int seed = 0; seed < 10; seed++) {
            // Each time have different seed!!!!
            Random random = new Random(seed);
            // NOTE: This value will be always the same
            System.out.println(random.nextInt(LIMIT_VALUE));
            random = new Random(seed);
            // Here again - first value is always the same, while second value changes.
            System.out.println(random.nextInt(LIMIT_VALUE) + "->" + random.nextInt(LIMIT_VALUE));
        }
    }
}
