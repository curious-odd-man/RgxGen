package com.github.curiousoddman.rgxgen;

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

import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.parsing.NodeCreator;

public class RgxGenBuilder {
    private final String pattern;

    private RgxGenProperties properties;
    private NodeCreator nodeCreator;

    public RgxGenBuilder(String pattern) {
        this.pattern = pattern;
    }

    public RgxGenBuilder withProps(RgxGenProperties properties) {
        this.properties = properties;
        return this;
    }

    public RgxGenBuilder withNodeCreator(NodeCreator nodeCreator) {
        this.nodeCreator = nodeCreator;
        return this;
    }

    public RgxGen parse() {
        return new RgxGen(properties, nodeCreator, pattern);
    }
}
