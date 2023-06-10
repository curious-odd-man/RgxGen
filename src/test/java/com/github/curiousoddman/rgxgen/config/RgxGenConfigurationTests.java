package com.github.curiousoddman.rgxgen.config;

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


import com.github.curiousoddman.rgxgen.RgxGen;
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
    public void defaultValueTest(RgxGenOption<Integer> option) {
        RgxGen.setDefaultProperties(null);
        RgxGen rgxGen = new RgxGen("xxx");
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals((Object) option.getDefault(), option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void globalConfigOnlyTest(RgxGenOption<Integer> option) {
        RgxGenProperties properties = new RgxGenProperties();
        option.setInProperties(properties, 20);
        RgxGen.setDefaultProperties(properties);
        RgxGen rgxGen = new RgxGen("xxx");
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(20, option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localConfigOnlyTest(RgxGenOption<Integer> option) {
        RgxGenProperties properties = new RgxGenProperties();
        option.setInProperties(properties, 20);
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(properties);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(20, option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localOverridesGlobalTest(RgxGenOption<Integer> option) {
        RgxGenProperties globProp = new RgxGenProperties();
        option.setInProperties(globProp, 20);
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        option.setInProperties(localProp, 10);
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(10, option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localMissingGlobalPresentTest(RgxGenOption<Integer> option) {
        RgxGenProperties globProp = new RgxGenProperties();
        option.setInProperties(globProp, 20);
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(20, option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localAndGlobalMissingUseDefaultTest(RgxGenOption<Integer> option) {
        RgxGenProperties globProp = new RgxGenProperties();
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals((Object) option.getDefault(), option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localResetToGlobalTest(RgxGenOption<Integer> option) {
        RgxGenProperties globProp = new RgxGenProperties();
        option.setInProperties(globProp, 20);
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        rgxGen.setProperties(null);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(20, option.getFromProperties(localProperties));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void localResetToDefaultTest(RgxGenOption<Integer> option) {
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        rgxGen.setProperties(null);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals((Object) option.getDefault(), option.getFromProperties(localProperties));
    }

    private static RgxGenProperties getLocalProperties(RgxGen o) {
        try {
            Field localProperties = RgxGen.class.getDeclaredField("localProperties");
            localProperties.setAccessible(true);
            return (RgxGenProperties) localProperties.get(o);
        } catch (ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to get local properties", e);
        }
    }
}
