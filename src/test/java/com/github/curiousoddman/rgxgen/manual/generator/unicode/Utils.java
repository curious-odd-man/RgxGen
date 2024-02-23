package com.github.curiousoddman.rgxgen.manual.generator.unicode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {
    public static String charAsString(int c) {
        if (c == '\\' || c == '\'') {
            return "\\" + (char) c;
        }
        return String.valueOf((char) c);
    }

    static void silentDeleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
