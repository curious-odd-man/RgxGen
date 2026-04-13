package com.github.curiousoddman.rgxgen.parsing.dflt;

import com.github.curiousoddman.rgxgen.data.FileTestUtils;
import com.github.curiousoddman.rgxgen.nodes.Node;
import com.github.curiousoddman.rgxgen.visitors.PrettyPrintVisitor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class SquareBracketsParsingTests {

    public enum Data implements FileTestUtils {
        SQBP_A_TO_C("[a-c]"),
        SQBP_A_TO_C_DASH("[a-c-]"),
        SQBP_A_TO_C_TO_X("[a-c-x]"),
        SQBP_DASH_A_TO_C("[-a-c]"),
        SQBP_HEX_CHARS("[\\x30-\\x{0032}]"),
        SQBP_SPECIAL_DASH("[\\s-]"),
        SQBP_DASH("[-]"),
        SQBP_SPECIAL_DASH_A("[\\s-a]"),
        SQBP_SPECIAL("[\\s]"),
        SQBP_A_DASH("[a-]"),

        SQBP_PARSING_ERROR("[\\s-a-\\s]", new RgxGenParseException("""
                Cannot make range with a shorthand escape sequences before '
                's-a-\\s]'
                      ^'"""));

        private final String pattern;
        private final Exception e;

        Data(String pattern, Exception e) {
            this.pattern = pattern;
            this.e = e;
        }

        Data(String pattern) {
            this.pattern = pattern;
            e = null;
        }

        public String getPattern() {
            return pattern;
        }

        public Exception getException() {
            return e;
        }

        @Override
        public Path rootPath() {
            return Path.of("testdata/patterns");
        }

        @Override
        public String extension() {
            return ".puml";
        }
    }

    @ParameterizedTest
    @EnumSource(Data.class)
    public void parsingTest(Data data) throws IOException {
        String prettyPrintedNodes = "";
        try {
            DefaultTreeBuilder builder = new DefaultTreeBuilder(data.getPattern(), new DefaultNodeCreator(), null);
            Node node = builder.get();
            PrettyPrintVisitor prettyPrintVisitor = new PrettyPrintVisitor();
            node.visit(prettyPrintVisitor);
            prettyPrintedNodes = prettyPrintVisitor.getResult();
            data.assertFileContents(prettyPrintedNodes);
        } catch (RgxGenParseException e) {
            if (data.getException() != null) {
                assertEquals(e.getMessage(), data.getException().getMessage(), e.getMessage());
            } else {
                fail("Got exception when expected SymbolSet. ", e);
            }
        }
    }
}
