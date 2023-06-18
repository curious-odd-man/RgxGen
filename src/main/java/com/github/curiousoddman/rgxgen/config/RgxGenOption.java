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

import com.github.curiousoddman.rgxgen.model.RgxGenCharsDefinition;
import com.github.curiousoddman.rgxgen.model.WhitespaceChar;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Enum of keys for available configuration options.
 */
public class RgxGenOption<T> {
    /**
     * For infinite patterns, such as {@code a+}, {@code a*} and {@code a{n,}}, defines limit for the repetitions.
     *
     * @defaultValue 100
     */
    public static final RgxGenOption<Integer> INFINITE_PATTERN_REPETITION = new RgxGenOption<>("generation.infinite.repeat", 100);

    /**
     * Flag to use case-insensitive matching.
     *
     * @defaultValue false
     */
    public static final RgxGenOption<Boolean> CASE_INSENSITIVE = new RgxGenOption<>("matching.case.insensitive", false);

    /**
     * Choose which characters dot pattern could generate.
     *
     * @defaultValue null
     */
    public static final RgxGenOption<RgxGenCharsDefinition> DOT_MATCHES_ONLY = new RgxGenOption<>("dot.matches.only", null);

    /**
     * Choose which characters \s pattern could generate.
     *
     * @defaultValue SPACE, TAB
     */
    public static final RgxGenOption<List<WhitespaceChar>> WHITESPACE_DEFINITION = new RgxGenOption<>("whitespace.matches", Arrays.asList(WhitespaceChar.SPACE, WhitespaceChar.TAB));

    private final String key;
    private final T      defaultValue;

    /**
     * Create an option with specific key and default value
     *
     * @param key          unique identifier of the option
     * @param defaultValue default value
     */
    public RgxGenOption(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Get unique identifier of the property
     *
     * @return unique key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get default value associated with the option
     *
     * @return default value
     */
    public T getDefault() {
        return defaultValue;
    }

    /**
     * Get value from the properties object.
     *
     * @param properties object to get value from
     * @return value from properties, if present. Default otherwise.
     */
    public T getFromProperties(RgxGenProperties properties) {
        return Optional.ofNullable(properties)
                       .map(props -> (T) props.get(key))
                       .orElse(defaultValue);
    }

    /**
     * Associates {@code value} for this option in the properties
     *
     * @param properties properties to add to
     * @param value      a value
     */
    public void setInProperties(RgxGenProperties properties, T value) {
        Objects.requireNonNull(value);
        properties.put(key, value);
    }

    @Override
    public String toString() {
        return key;
    }
}
