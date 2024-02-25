package com.github.curiousoddman.rgxgen;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class LicenseCheckerTest {
    private static final String LICENCE = readLicenceFile();

    @ParameterizedTest
    @MethodSource("getAllSourceFiles")
    void checkLicence(Path path) throws IOException {
        assumeTrue(path.toString().endsWith(".java"));
        String contents = String.join("\n", Files.readAllLines(path));
        assertTrue(contents.contains(LICENCE));
    }

    private static String readLicenceFile() {
        try {
            List<String> allLines = Files.readAllLines(Paths.get("LICENSE.txt"));
            List<String> licenseText = allLines.subList(189, 202);
            return String.join("\n", licenseText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<Path> getAllSourceFiles() {
        try {
            try (Stream<Path> files = Files.walk(Paths.get("src/main"))) {
                return files
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList())
                        .stream();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
