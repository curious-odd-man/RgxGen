package com.github.curiousoddman.rgxgen.lineages;

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

/**
 * A directed edge in the path graph.
 * <p>
 * {@code min} and {@code max} describe how many times this edge may be traversed
 * in a single generation pass.  {@code max == -1} means unbounded (∞).
 *
 * @param max -1 = unbounded
 */
public record PathEdge(PathNode from, PathNode to, int min, int max) {

    public static final int UNBOUNDED = -1;

    public static PathEdge once(PathNode from, PathNode to) {
        return new PathEdge(from, to, 1, 1);
    }

    public static PathEdge repeat(PathNode from, PathNode to, int min, int max) {
        return new PathEdge(from, to, min, max);
    }

    // -------------------------------------------------------------------------
    // PlantUML / DOT label
    // -------------------------------------------------------------------------

    public String label() {
        if (min == max) {
            if (min == 1) {
                return "";
            } else {
                return String.valueOf(min);
            }
        }
        String maxStr = (max == UNBOUNDED) ? "<&infinity>" : String.valueOf(max);
        return min + ".." + maxStr;
    }

    @Override
    public String toString() {
        return from.getId() + " -[" + label() + "]-> " + to.getId();
    }
}
