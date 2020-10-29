package com.github.curiousoddman.rgxgen.config;

import java.util.Properties;

/**
 * Configuration object for RgxGen.
 *
 * @see com.github.curiousoddman.rgxgen.config.RgxGenOption for available options
 */
public class RgxGenProperties extends Properties {
    private static final long serialVersionUID = 3352045589564244181L;

    public void setDefaults(Properties properties) {
        defaults = properties;
    }
}
