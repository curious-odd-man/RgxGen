package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.generator.nodes.Node;
import com.github.curiousoddman.rgxgen.generator.visitors.GenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueGenerationVisitor;
import com.github.curiousoddman.rgxgen.generator.visitors.UniqueValuesCountingVisitor;
import com.github.curiousoddman.rgxgen.iterators.StringIterator;
import com.github.curiousoddman.rgxgen.parsing.NodeTreeBuilder;
import com.github.curiousoddman.rgxgen.parsing.dflt.DefaultTreeBuilder;

import java.math.BigInteger;
import java.util.stream.Stream;

/**
 * String values generator based on regular expression pattern
 */
public class RgxGen {
    private final Node aNode;

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
     */
    public RgxGen(NodeTreeBuilder builder) {
        aNode = builder.get();
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
     * @see RgxGen#generate()
     * @return stream of randomly generated strings
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
     * @return generated string.
     */
    public String generate() {
        GenerationVisitor gv = new GenerationVisitor();
        aNode.visit(gv);
        return gv.getString();
    }
}
