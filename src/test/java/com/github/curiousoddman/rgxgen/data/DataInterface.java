package com.github.curiousoddman.rgxgen.data;

import java.math.BigInteger;
import java.util.List;

public interface DataInterface extends Named {

    boolean hasEstimatedCount();

    boolean hasAllUniqueValues();

    boolean useFindForMatching();

    boolean isUsableWithJavaPattern();

    String getPattern();

    BigInteger getEstimatedCount();

    List<String> getAllUniqueValues();
}
