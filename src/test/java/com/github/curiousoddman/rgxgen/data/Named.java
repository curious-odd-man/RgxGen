package com.github.curiousoddman.rgxgen.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Named {
    String name();

    default Path getExpectedFilePath() {
        return Path.of("testdata/patterns").resolve(name() + ".txt");
    }

    default String getExpectedFromFile() throws IOException {
        Path expectedFileName = getExpectedFilePath();
        return Files.readString(expectedFileName);
    }
}
