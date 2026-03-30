package com.github.curiousoddman.rgxgen.data;

import java.util.List;

public interface AnalysisDataInterface {
    String getPattern();

    String getOptimizedGraph();

    List<String> getAllUniqueValues();
}
