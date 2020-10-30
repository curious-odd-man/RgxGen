package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RgxGenConfigurationTests {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {RgxGenOption.INFINITE_PATTERN_REPETITION}
        });
    }

    @Parameterized.Parameter
    public RgxGenOption aOption;

    @After
    public void afterEach() {
        RgxGen.setDefaultProperties(null);
    }

    @AfterClass
    public static void afterAll() {
        RgxGen.setDefaultProperties(null);
    }

    @Test
    public void defaultValueTest() {
        RgxGen.setDefaultProperties(null);
        RgxGen rgxGen = new RgxGen("xxx");
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(aOption.getDefault(),
                     aOption.getFromProperties(localProperties));
    }

    @Test
    public void globalConfigOnlyTest() {
        RgxGenProperties properties = new RgxGenProperties();
        aOption.setInProperties(properties, "20");
        RgxGen.setDefaultProperties(properties);
        RgxGen rgxGen = new RgxGen("xxx");
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("20", aOption.getFromProperties(localProperties));
    }

    @Test
    public void localConfigOnlyTest() {
        RgxGenProperties properties = new RgxGenProperties();
        aOption.setInProperties(properties, "20");
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(properties);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("20", aOption.getFromProperties(localProperties));
    }

    @Test
    public void localOverridesGlobalTest() {
        RgxGenProperties globProp = new RgxGenProperties();
        aOption.setInProperties(globProp, "20");
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        aOption.setInProperties(localProp, "10");
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("10", aOption.getFromProperties(localProperties));
    }

    @Test
    public void localMissingGlobalPresentTest() {
        RgxGenProperties globProp = new RgxGenProperties();
        aOption.setInProperties(globProp, "20");
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("20", aOption.getFromProperties(localProperties));
    }

    @Test
    public void localAndGlobalMissingUseDefaultTest() {
        RgxGenProperties globProp = new RgxGenProperties();
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(aOption.getDefault(),
                     aOption.getFromProperties(localProperties));
    }

    @Test
    public void localResetToGlobalTest() {
        RgxGenProperties globProp = new RgxGenProperties();
        aOption.setInProperties(globProp, "20");
        RgxGen.setDefaultProperties(globProp);
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        rgxGen.setProperties(null);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals("20", aOption.getFromProperties(localProperties));
    }

    @Test
    public void localResetToDefaultTest() {
        RgxGenProperties localProp = new RgxGenProperties();
        RgxGen rgxGen = new RgxGen("xxx");
        rgxGen.setProperties(localProp);
        rgxGen.setProperties(null);
        RgxGenProperties localProperties = getLocalProperties(rgxGen);
        assertEquals(aOption.getDefault(), aOption.getFromProperties(localProperties));
    }

    private static RgxGenProperties getLocalProperties(RgxGen o) {
        try {
            Field localProperties = RgxGen.class.getDeclaredField("aLocalProperties");
            localProperties.setAccessible(true);
            return (RgxGenProperties) localProperties.get(o);
        } catch (ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get local properties", e);
        }
    }
}
