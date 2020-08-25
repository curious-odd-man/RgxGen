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

public class Group implements Node {
    private final Node aNode;
    private final int  aGroupIndex;

    public Group(int index, Node node) {
        aNode = node;
        aGroupIndex = index;
    }

    public int getIndex() {
        return aGroupIndex;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Node getNode() {
        return aNode;
    }

    @Override
    public String toString() {
        return "Group[" + aGroupIndex +
                "]{" + aNode +
                '}';
    }
}
