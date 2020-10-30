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
import com.github.curiousoddman.rgxgen.generator.nodes.Node;
import com.github.curiousoddman.rgxgen.generator.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.NotMatchingGenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueGenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueValuesCountingVisitor;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Stream;

/**
 * String values generator based on regular expression pattern
 */
public class RgxGen {
    private static RgxGenProperties aGlobalProperties = null;

    private final Node aNode;

    private RgxGenProperties aLocalProperties = aGlobalProperties;

    /**
     * Set default properties for RgxGen.
     * Each new instance of RgxGen will use these properties as a backup, if property is not set per instance.
     *
     * @param properties configuration properties. Set {@code null} to reset.
     * @apiNote Existing instances will not be affected. Only those, that will be created after this call.
     * @see com.github.curiousoddman.rgxgen.config.RgxGenOption
     */
    public static void setDefaultProperties(RgxGenProperties properties) {
        aGlobalProperties = properties;
    }

    /**
     * Parse pattern using DefaultTreeBuilder.
     *
     * @param pattern regex pattern for values generation
     */
    public RgxGen(String pattern) {
        this(new DefaultTreeBuilder(pattern));
    }

    /**
     * Parse regex pattern using provided builder and prepare to generate values
     *
     * @param builder node tree builder implementation
     */
    public RgxGen(NodeTreeBuilder builder) {
        aNode = builder.get();
    }

    /**
     * Set properties for the instance of RgxGen.
     * These options will override default values set by either {@code setDefaultProperties} or default hardcoded.
     *
     * @param properties configuration properties. Set {@code null} to reset.
     * @see com.github.curiousoddman.rgxgen.config.RgxGenOption
     */
    public void setProperties(RgxGenProperties properties) {
        aLocalProperties = properties;
        if (aLocalProperties == null) {
            aLocalProperties = aGlobalProperties;
        } else {
            aLocalProperties.setDefaults(aGlobalProperties);
        }
    }

    /**
     * Returns estimation of unique values that can be generated with the pattern.
     *
     * @return number of unique values or null, if infinite
     * @apiNote This might not be accurate! For example the pattern "(a{0,2}|b{0,2})" will estimate to 6,
     * though actual count is only 5, because right and left part of group can yield same value
     */
    public BigInteger numUnique() {
        UniqueValuesCountingVisitor v = new UniqueValuesCountingVisitor();
        aNode.visit(v);
        return v.getCount();
    }

    /**
     * Creates infinite stream of randomly generated values.
     *
     * @return stream of randomly generated strings
     * @see RgxGen#generate()
     */
    public Stream<String> stream() {
        return Stream.generate(this::generate);
    }

    /**
     * Creates iterator over unique values.
     *
     * @return Iterator over unique values
     */
    public StringIterator iterateUnique() {
        UniqueGenerationVisitor ugv = new UniqueGenerationVisitor();
        aNode.visit(ugv);
        return ugv.getUniqueStrings();
    }

    /**
     * Generate random string from the pattern.
     *
     * @return matching random string
     */
    public String generate() {
        return generate(new Random());
    }

    /**
     * Generate random string from the pattern.
     * Random initialized with same seed will produce same results.
     *
     * @param random random to use for the generation.
     * @return generated string.
     */
    public String generate(Random random) {
        GenerationVisitor gv = GenerationVisitor.builder()
                                                .withRandom(random)
                                                .withProperties(aLocalProperties)
                                                .get();
        aNode.visit(gv);
        return gv.getString();
    }

    /**
     * Generate random string that does not match a pattern.
     *
     * @return not matching random string.
     */
    public String generateNotMatching() {
        return generateNotMatching(new Random());
    }

    /**
     * Generate random string that does not match a pattern.
     * Random initialized with same seed will produce same results.
     *
     * @param random random to use for the generation.
     * @return generated string.
     */
    public String generateNotMatching(Random random) {
        GenerationVisitor nmgv = NotMatchingGenerationVisitor.builder()
                                                             .withRandom(random)
                                                             .get();
        aNode.visit(nmgv);
        return nmgv.getString();
    }
}
