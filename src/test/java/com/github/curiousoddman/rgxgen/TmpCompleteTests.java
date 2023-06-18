package com.github.curiousoddman.rgxgen;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.testutil.TestingUtilities.newRandom;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


//@Disabled("Temporary not working tests")
public class TmpCompleteTests {

    public static Stream<Arguments> getTestData() {
        return Arrays.stream(new Object[][]{
                             {"Domain name", Boolean.TRUE, "(?!w{1,}\\.)(\\w+\\.?)([a-zA-Z]+)(\\.\\w+)"}, // Fails infrequently...
                             {"ISO-8601 Date", Boolean.TRUE, "^(?![+-]?\\d{4,5}-?(?:\\d{2}|W\\d{2})T)(?:|(\\d{4}|[+-]\\d{5})-?(?:|(0\\d|1[0-2])(?:|-?([0-2]\\d|3[0-1]))|([0-2]\\d{2}|3[0-5]\\d|36[0-6])|W([0-4]\\d|5[0-3])(?:|-?([1-7])))(?:(?!\\d)|T(?=\\d)))(?:|([01]\\d|2[0-4])(?:|:?([0-5]\\d)(?:|:?([0-5]\\d)(?:|\\.(\\d{3})))(?:|[zZ]|([+-](?:[01]\\d|2[0-4]))(?:|:?([0-5]\\d)))))$"}, // Something totally wrong
                             {"Unix Path", Boolean.TRUE, "/|((?=/)|\\.|\\.\\.|~|~(?=/))(/(?=[^/])[^/]+)*/?"},  // Not matching generation fails
                             {"Hashtags", Boolean.TRUE, "\\B#([a-z0-9]{2,})(?![~!@#$%^&*()=+_`\\-\\|\\/'\\[\\]\\{\\}]|[?.,]*\\w)"}, // This partially fails
                             // {"HTML Tags", Boolean.FALSE, "(<script(\\s|\\S)*?</script>)|(<style(\\s|\\S)*?</style>)|(<!--(\\s|\\S)*?-->)|(</?(\\s|\\S)*?>)"}, // This hangs
                     })
                     .flatMap(arr -> IntStream.range(0, 100)
                                              .mapToObj(index -> Arguments.of(arr[0], arr[1], arr[2], index)));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getTestData")
    public void generateTest(String name, boolean useFind, String pattern, int seed) {
        RgxGen rgxGen = RgxGen.parse(pattern);
        String s = rgxGen.generate(newRandom(seed));
        assertTrue(matches(s, pattern, useFind), "Text: '" + s + "' does not match pattern " + pattern);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getTestData")
    public void generateNotMatchingTest(String name, boolean useFind, String pattern, int seed) {
        RgxGen rgxGen = RgxGen.parse(pattern);
        String s = rgxGen.generateNotMatching(newRandom(seed));
        assertFalse(matches(s, pattern, useFind), "Text: '" + s + "' does not match pattern " + pattern);
    }

    private static boolean matches(String text, String pattern, boolean useFind) {
        Matcher matcher = Pattern.compile(pattern)
                                 .matcher(text);
        return useFind ? matcher.find() : matcher.matches();
    }
}
