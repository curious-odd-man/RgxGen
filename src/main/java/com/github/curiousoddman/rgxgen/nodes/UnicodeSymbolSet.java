package com.github.curiousoddman.rgxgen.nodes;

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

import com.github.curiousoddman.rgxgen.visitors.NodeVisitor;

import java.util.*;
import java.util.function.Consumer;

import static com.github.curiousoddman.rgxgen.util.Util.ZERO_LENGTH_CHARACTER_ARRAY;

/**
 * Generate Any character.
 */
public class UnicodeSymbolSet extends Node {
    protected UnicodeSymbolSet(String pattern) {
        super(pattern);
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
