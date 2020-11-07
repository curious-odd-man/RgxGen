package com.github.curiousoddman.rgxgen.config;

import java.util.Optional;

/**
 * Enum of keys for available configuration options.
 */
public enum RgxGenOption {
    /**
     * For infinite patterns, such as {@code a+}, {@code a*} and {@code a{n,}}, defines limit for the repetitions.
     *
     * @defaultValue 100
     */
    INFINITE_PATTERN_REPETITION("generation.infinite.repeat", "100"),

    /**
     * Flag to use case insensitive matching.
     *
     * @defaultValue false
     */
    CASE_INSENSITIVE("matching.case.insensitive", "false");

    private final String aKey;
    private final String aDefault;

    /**
     * Create an option with specific key and default value
     *
     * @param key  unique identifier of the option
     * @param dflt default value
     */
    RgxGenOption(String key, String dflt) {
        aKey = key;
        aDefault = dflt;
    }

    /**
     * Get unique identifier of the property
     *
     * @return unique key
     */
    public String getKey() {
        return aKey;
    }

    /**
     * Get default value associated with the option
     *
     * @return default value
     */
    public String getDefault() {
        return aDefault;
    }

    /**
     * Get value from the properties object.
     *
     * @param properties object to get value from
     * @return value from properties, if present. Default otherwise.
     */
    public String getFromProperties(RgxGenProperties properties) {
        return Optional.ofNullable(properties)
                       .map(p -> p.getProperty(aKey))
                       .orElse(aDefault);
    }

    /**
     * Associates {@code value} for this option in the properties
     *
     * @param properties properties to add to
     * @param value      a value
     */
    public void setInProperties(RgxGenProperties properties, String value) {
        properties.setProperty(aKey, value);
    }

    /**
     * Convenience method. Returns value of the property transformed to an integer
     *
     * @param properties properties to get value from
     * @return integer value associated with property, or default.
     */
    public int getIntFromProperties(RgxGenProperties properties) {
        return Integer.parseInt(getFromProperties(properties));
    }

    @Override
    public String toString() {
        return aKey;
    }
}
