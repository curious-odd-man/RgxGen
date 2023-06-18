package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.data.TestPattern;
import com.github.curiousoddman.rgxgen.data.TestPatternCaseInsensitive;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.testutil.TestingUtilities.newRandom;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * The aim of these test is to show that the result of generation was changed by writing values generated with same seed into a text file.
 * When a developer makes a change to a generation algorithm - the generated values will change and will pop up during commit.
 * This is an indication that the generated values changed - there are 2 possible ways to act:
 * 1. Change generation algorithm so that the values stay the same
 * 2. When the change goes to release - make a note, that the generation algorithm changed and the expected values may change as well.
 */
@SuppressWarnings({"TestMethodWithoutAssertion"})

public class GenerationConsistencyTests {
    private static final int  NUM_ITERATIONS      = 100;
    private static final Path caseInsensitivePath = Paths.get("testdata/caseinsensitive/");
    private static final Path caseSensitivePath   = Paths.get("testdata/casesensitive/");

    public static Stream<TestPatternCaseInsensitive> getCaseInsensitivePatterns() {
        return Arrays.stream(TestPatternCaseInsensitive.values());
    }

    public static Stream<TestPattern> getPatterns() {
        return Arrays.stream(TestPattern.values());
    }

    @ParameterizedTest
    @MethodSource("getCaseInsensitivePatterns")
    void verifyThatAllCaseInsensitivePatternsStaysTheSameTest(TestPatternCaseInsensitive data) throws IOException {
        String name = data.name();
        Path fileName = caseInsensitivePath.resolve("matching").resolve(name + ".txt").toAbsolutePath();
        Files.deleteIfExists(fileName);
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        RgxGen rgxGen = RgxGen.parse(properties, data.getPattern());
        Random random = newRandom(17);
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            String generated = rgxGen.generate(random);
            Files.write(fileName, singletonList(generated), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    @ParameterizedTest
    @MethodSource("getCaseInsensitivePatterns")
    void verifyThatAllCaseInsensitivePatternsStaysTheSameNotMatchingTest(TestPatternCaseInsensitive data) throws IOException {
        String name = data.name();
        Path fileName = caseInsensitivePath.resolve("notmatching").resolve(name + ".txt").toAbsolutePath();
        Files.deleteIfExists(fileName);
        RgxGenProperties properties = new RgxGenProperties();
        RgxGenOption.CASE_INSENSITIVE.setInProperties(properties, true);
        RgxGen rgxGen = RgxGen.parse(properties, data.getPattern());
        Random random = newRandom(17);
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            String generated = rgxGen.generateNotMatching(random);
            Files.write(fileName, singletonList(generated), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    @ParameterizedTest
    @MethodSource("getPatterns")
    void verifyThatAllCaseSensitivePatternsStaysTheSameTest(TestPattern data) throws IOException {
        String name = data.name();
        Path fileName = caseSensitivePath.resolve("matching").resolve(name + ".txt").toAbsolutePath();
        Files.deleteIfExists(fileName);
        RgxGen rgxGen = RgxGen.parse(data.getPattern());
        Random random = newRandom(17);
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            String generated = rgxGen.generate(random);
            Files.write(fileName, singletonList(generated), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }


    @ParameterizedTest
    @MethodSource("getPatterns")
    void verifyThatAllCaseSensitivePatternsStaysTheSameNotMatchingTest(TestPattern data) throws IOException {
        String name = data.name();
        Path fileName = caseSensitivePath.resolve("notmatching").resolve(name + ".txt").toAbsolutePath();
        Files.deleteIfExists(fileName);
        RgxGen rgxGen = RgxGen.parse(data.getPattern());
        Random random = newRandom(17);
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            String generated = rgxGen.generateNotMatching(random);
            try {
                Files.write(fileName, singletonList(generated), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (MalformedInputException e) {
                System.out.println("Failed to write a text " + e.getMessage());
                System.out.println(generated.chars().mapToObj(String::valueOf).collect(Collectors.toList()));
                System.out.println(generated);
                fail(e);
            }
        }
    }
}
