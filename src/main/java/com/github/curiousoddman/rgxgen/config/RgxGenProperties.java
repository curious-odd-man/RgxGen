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

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration object for RgxGen.
 *
 * @see com.github.curiousoddman.rgxgen.config.RgxGenOption for available options
 */
public class RgxGenProperties {
    private final Map<String, Object> properties = new HashMap<>();
    private final Map<String, Object> defaults   = new HashMap<>();

    public void setDefaults(RgxGenProperties properties) {
        defaults.clear();
        if (properties != null) {
            defaults.putAll(properties.properties);
        }
    }

    public void put(String key, Object value) {
        properties.put(key, value);
    }

    public Object get(String key) {
        Object o = properties.get(key);
        if (o == null) {
            return defaults.get(key);
        }
        return o;
    }
}
