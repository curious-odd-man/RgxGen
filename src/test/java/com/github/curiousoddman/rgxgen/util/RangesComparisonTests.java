package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.model.SymbolRange;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class RangesComparisonTests {

    public static final  int         REFERENCE_START = 10;
    public static final  int         REFERENCE_END   = 20;
    private static final SymbolRange RANGE_10_20     = range(REFERENCE_START, REFERENCE_END);

    public static Stream<Arguments> getRangeComparisonTestData() {
        int startBefore = 0;
        int startOnStart = REFERENCE_START;
        int startInRange = REFERENCE_START + 5;
        int startOnEnd = REFERENCE_END;
        int startAfterEnd = REFERENCE_END + 5;

        int endsBefore = 1;
        int endsOnStart = REFERENCE_START;
        int endsInRange = REFERENCE_START + 6;
        int endsOnEnd = REFERENCE_END;
        int endsAfterEnd = REFERENCE_END + 6;

        BiFunction<SymbolRange, SymbolRange, Boolean> isRightCanContinueLeft = Util::isRightCanContinueLeft;
        BiFunction<SymbolRange, SymbolRange, Boolean> isRightWithinLeft = Util::isRightWithinLeft;

        return Stream.of(
                arguments("isRightCanContinueLeft: starts before ends before", range(startBefore, endsBefore), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts before ends on start", range(startBefore, endsOnStart), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts before ends in range", range(startBefore, endsInRange), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts before ends on end", range(startBefore, endsOnEnd), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts before ends after range", range(startBefore, endsAfterEnd), isRightCanContinueLeft, false),

                arguments("isRightCanContinueLeft: starts on start ends on start", range(startOnStart, endsOnStart), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts on start ends in range", range(startOnStart, endsInRange), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts on start ends on end", range(startOnStart, endsOnEnd), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts on start ends after range", range(startOnStart, endsAfterEnd), isRightCanContinueLeft, true),

                arguments("isRightCanContinueLeft: starts in range ends in range", range(startInRange, endsInRange), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts in range ends on end", range(startInRange, endsOnEnd), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts in range ends after range", range(startInRange, endsAfterEnd), isRightCanContinueLeft, true),

                arguments("isRightCanContinueLeft: starts on end ends on end", range(startOnEnd, endsOnEnd), isRightCanContinueLeft, false),
                arguments("isRightCanContinueLeft: starts on end ends after range", range(startOnEnd, endsAfterEnd), isRightCanContinueLeft, true),

                arguments("isRightCanContinueLeft: starts right after end ends right after end", range(startOnEnd + 1, endsOnEnd + 1), isRightCanContinueLeft, true),
                arguments("isRightCanContinueLeft: starts right after end ends after range", range(startOnEnd + 1, endsAfterEnd), isRightCanContinueLeft, true),

                arguments("isRightCanContinueLeft: starts after end ends after range", range(startAfterEnd, endsAfterEnd), isRightCanContinueLeft, false),

                // ---------------------------------------------------------------------------------------------------------

                arguments("isRightWithinLeft: starts before ends before", range(startBefore, endsBefore), isRightWithinLeft, false),
                arguments("isRightWithinLeft: starts before ends on start", range(startBefore, endsOnStart), isRightWithinLeft, false),
                arguments("isRightWithinLeft: starts before ends in range", range(startBefore, endsInRange), isRightWithinLeft, false),
                arguments("isRightWithinLeft: starts before ends on end", range(startBefore, endsOnEnd), isRightWithinLeft, false),
                arguments("isRightWithinLeft: starts before ends after range", range(startBefore, endsAfterEnd), isRightWithinLeft, false),

                arguments("isRightWithinLeft: starts on start ends on start", range(startOnStart, endsOnStart), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts on start ends in range", range(startOnStart, endsInRange), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts on start ends on end", range(startOnStart, endsOnEnd), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts on start ends after range", range(startOnStart, endsAfterEnd), isRightWithinLeft, false),

                arguments("isRightWithinLeft: starts in range ends in range", range(startInRange, endsInRange), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts in range ends on end", range(startInRange, endsOnEnd), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts in range ends after range", range(startInRange, endsAfterEnd), isRightWithinLeft, false),

                arguments("isRightWithinLeft: starts on end ends on end", range(startOnEnd, endsOnEnd), isRightWithinLeft, true),
                arguments("isRightWithinLeft: starts on end ends after range", range(startOnEnd, endsAfterEnd), isRightWithinLeft, false),

                arguments("isRightWithinLeft: starts after end ends after range", range(startAfterEnd, endsAfterEnd), isRightWithinLeft, false)
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getRangeComparisonTestData")
    void rangesComparisonTests(String name, SymbolRange b, BiFunction<SymbolRange, SymbolRange, Boolean> func, boolean expected) {
        assertEquals(expected, func.apply(RANGE_10_20, b));
    }
}
