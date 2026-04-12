package com.github.curiousoddman.rgxgen.data;

import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface FileTestUtils {
    String name();

    Path rootPath();

    String extension();

    default Path getExpectedFilePath(String suffix) {
        String resolvablePath = name() + suffix + (suffix.contains(".") ? "" : extension());
        Path path = rootPath().resolve(resolvablePath);
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    default String getExpectedFromFile(String suffix) throws IOException {
        Path expectedFileName = getExpectedFilePath(suffix);
        return Files.readString(expectedFileName);
    }

    default void assertFileContents(String actual) throws IOException {
        assertFileContents(actual, "");
    }

    default void assertFileContents(String actual, String fileSuffix) throws IOException {
        Path expectedFilePath = getExpectedFilePath(fileSuffix);

        try {
            assertEquals(
                    getExpectedFromFile(fileSuffix),
                    actual
            );
        } catch (AssertionFailedError | NoSuchFileException e) {
            Files.writeString(expectedFilePath, actual);
            throw e;
        }
    }
}
