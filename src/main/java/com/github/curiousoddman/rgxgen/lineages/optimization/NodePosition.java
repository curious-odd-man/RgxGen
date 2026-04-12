package com.github.curiousoddman.rgxgen.lineages.optimization;

public class NodePosition {
    public enum First {
        /**
         * Does not have AST nodes between BEGIN and this node in any path
         */
        ALWAYS,
        /**
         * has at least 2 paths from BEGIN to this node
         * - at least one path does not have AST nodes between BEGIN and this node
         * - at least one path has AST nodes between BEGIN and this node
         */
        SOMETIMES,
        /**
         * Neither of above
         */
        NEVER
    }

    public enum Last {
        /**
         * Does not have AST nodes between END and this node in any path
         */
        ALWAYS,
        /**
         * has at least 2 paths from END to this node
         * - at least one path does not have AST nodes between END and this node
         * - at least one path has AST nodes between END and this node
         */
        SOMETIMES,
        /**
         * Neither of above
         */
        NEVER
    }
}
