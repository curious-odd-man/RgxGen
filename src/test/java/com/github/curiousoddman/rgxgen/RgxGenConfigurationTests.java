package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RgxGenConfigurationTests {

    public static Stream<Arguments> getTestData() {
        return Stream.of(Arguments.of(RgxGenOption.INFINITE_PATTERN_REPETITION));
    }


    @AfterEach
    public void afterEach() {
        RgxGen.setDefaultProperties(null);
    }

    @AfterAll
    public static void afterAll() {
        RgxGen.setDefaultProperties(null);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void defaultValueTest(RgxGenOption option) {
        RgxGen.setDefaultProperties(null);
        RgxGen rgxGen = new RgxGen("xxx");
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(option.getDefault(), option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void globalConfigOnlyTest(RgxGenOption option) {
        RgxGenProperties properties = new RgxGenProperties();
        option.setInProperties(properties, 20);
        RgxGen.setDefaultProperties(properties);
        RgxGen rgxGen = new RgxGen("xxx");
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("20", option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localConfigOnlyTest(RgxGenOption option) {
        RgxGenProperties properties = new RgxGenProperties();
        option.setInProperties(properties, 20);
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(properties);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("20", option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localOverridesGlobalTest(RgxGenOption option) {
        RgxGenProperties globProp = new RgxGenProperties();
        option.setInProperties(globProp, 20);
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        option.setInProperties(localProp, 10);
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("10", option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localMissingGlobalPresentTest(RgxGenOption option) {
        RgxGenProperties globProp = new RgxGenProperties();
        option.setInProperties(globProp, 20);
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("20", option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localAndGlobalMissingUseDefaultTest(RgxGenOption option) {
        RgxGenProperties globProp = new RgxGenProperties();
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(option.getDefault(),
                     option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localResetToGlobalTest(RgxGenOption option) {
        RgxGenProperties globProp = new RgxGenProperties();
        option.setInProperties(globProp, 20);
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        rgxGen.setProperties(null);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("20", option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localResetToDefaultTest(RgxGenOption option) {
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        rgxGen.setProperties(null);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(option.getDefault(), option.getFromProperties(localProperties));
    }

    private static RgxGenProperties getLocalProperties(RgxGen o) {
        try {
            Field localProperties = RgxGen.class.getDeclaredField("aLocalProperties");
            localProperties.setAccessible(true);
            return (RgxGenProperties) localProperties.get(o);
        } catch (ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to get local properties", e);
        }
    }
}
