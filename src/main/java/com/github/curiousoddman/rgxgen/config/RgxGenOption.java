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

import java.util.Objects;
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
    CASE_INSENSITIVE("matching.case.insensitive", "false"),

    /**
     * Maximum number to re-try generate value when lookaround patterns are used.
     * When this number of retries is exceeded - the exception is thrown.
     * Negative value means infinite retries. (WARNING! This might lead to infinite loop)
     *
     * @defaultValue 1000
     */
    MAX_LOOKAROUND_MATCH_RETRIES("max.lookaround.retries", "1000");

    private final String aKey;
    private final String aDefault;

    /**
     * Create an option with specific key and default value
     *
     * @param key           unique identifier of the option
     * @param default_value default value
     */
    RgxGenOption(String key, String default_value) {
        aKey = key;
        aDefault = default_value;
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
     * @param <T>        type of value
     */
    public <T> void setInProperties(RgxGenProperties properties, T value) {
        Objects.requireNonNull(value);
        properties.setProperty(aKey, Objects.toString(value));
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

    /**
     * Convenience method. Returns value of the property transformed to an integer
     *
     * @param properties properties to get value from
     * @return boolean value associated with property, or default.
     */
    public boolean getBooleanFromProperties(RgxGenProperties properties) {
        return Boolean.parseBoolean(getFromProperties(properties));
    }

    @Override
    public String toString() {
        return aKey;
    }
}
