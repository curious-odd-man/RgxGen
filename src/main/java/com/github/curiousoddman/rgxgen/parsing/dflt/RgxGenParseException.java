package com.github.curiousoddman.rgxgen.parsing.dflt;

public class RgxGenParseException extends RuntimeException {
    public RgxGenParseException(String s) {
        super(s);
    }

    public RgxGenParseException(String s, Throwable t) {
        super(s, t);
    }
}
