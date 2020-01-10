package com.github.curiousoddman.rgxgen.generator.nodes;

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

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Sequence implements Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sequence.class);

    private final Node[] aNodes;

    public Sequence(Node... nodes) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Creating from {} ", Arrays.asList(nodes));
        }
        aNodes = nodes;
    }

    public Node[] getNodes() {
        return aNodes;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Sequence" + Arrays.toString(aNodes);
    }
}
