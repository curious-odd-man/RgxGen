package com.github.curiousoddman.rgxgen.generator.nodes;

import com.github.curiousoddman.rgxgen.generator.visitors.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupRef implements Node {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupRef.class);

    private final int aIndex;

    public GroupRef(int index) {
        LOGGER.trace("Crating idx = '{}'", index);
        aIndex = index;
    }

    @Override
    public void visit(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public int getIndex() {
        return aIndex;
    }

    @Override
    public String toString() {
        return "GroupRef{" + aIndex + '}';
    }
}
