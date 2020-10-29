package com.github.curiousoddman.rgxgen.config;

import java.util.Optional;

/**
 * Enum of keys for available configuration options.
 */
public enum RgxGenOption {
    /**
     * For infinite patterns, such as {@code a+}, {@code a*} and {@code a{n,}}, defines limit for the repetitions.
     *
     * @expectedType Integer
     * @defaultValue 100
     */
    INFINITE_PATTERN_REPETITION("generation.infinite.repeat", 100);

    private final String aKey;
    private final Object aDefault;

    RgxGenOption(String key, Object dflt) {
        aKey = key;
        aDefault = dflt;
    }

    public <T> T getFromProperties(RgxGenProperties properties) {
        return (T) Optional.ofNullable(properties)
                           .map(p -> p.get(aKey))
                           .orElse(aDefault);
    }

}
