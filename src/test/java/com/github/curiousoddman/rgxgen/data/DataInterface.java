package com.github.curiousoddman.rgxgen.data;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;

public interface DataInterface extends FileTestUtils {

    boolean hasEstimatedCount();

    boolean hasAllUniqueValues();

    boolean useFindForMatching();

    boolean isUsableWithJavaPattern();

    String getPattern();

    BigInteger getEstimatedCount();

    List<String> getAllUniqueValues();

    @Override
    default Path rootPath() {
        return Path.of("testdata/patterns");
    }

    @Override
    default String extension() {
        return ".txt";
    }
}
