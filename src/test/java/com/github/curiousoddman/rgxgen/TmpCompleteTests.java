package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


//@Disabled("Temporary not working tests")
public class TmpCompleteTests {

    public static Stream<Arguments> getTestData() {
        return Arrays.stream(new Object[][]{
                             //{"Test", Boolean.FALSE, "a\nb"}
                             {"Morse code", Boolean.TRUE, "^[.-]{1,5}(?:[ \t]+[.-]{1,5})*(?:[ \t]+[.-]{1,5}(?:[ \t]+[.-]{1,5})*)*$"},       // Very slow not matching generation. + java.regex fails to parse..
                             // {"Morse code", Boolean.TRUE, "^[.-]{1,5}(?: +[.-]{1,5})*(?: +[.-]{1,5}(?: +[.-]{1,5})*)*$"},       // Very slow not matching generation. + java.regex fails to parse..
                             // {"Domain name", Boolean.TRUE, "(?!w{1,}\\.)(\\w+\\.?)([a-zA-Z]+)(\\.\\w+)"}, // Fails infrequently...
                             // {"ISO-8601 Date", Boolean.TRUE, "^(?![+-]?\\d{4,5}-?(?:\\d{2}|W\\d{2})T)(?:|(\\d{4}|[+-]\\d{5})-?(?:|(0\\d|1[0-2])(?:|-?([0-2]\\d|3[0-1]))|([0-2]\\d{2}|3[0-5]\\d|36[0-6])|W([0-4]\\d|5[0-3])(?:|-?([1-7])))(?:(?!\\d)|T(?=\\d)))(?:|([01]\\d|2[0-4])(?:|:?([0-5]\\d)(?:|:?([0-5]\\d)(?:|\\.(\\d{3})))(?:|[zZ]|([+-](?:[01]\\d|2[0-4]))(?:|:?([0-5]\\d)))))$"}, // Something totally wrong
                             // {"Unix Path", Boolean.TRUE, "/|((?=/)|\\.|\\.\\.|~|~(?=/))(/(?=[^/])[^/]+)*/?"},  // Not matching generation fails
                             // {"Hashtags", Boolean.TRUE, "\\B#([a-z0-9]{2,})(?![~!@#$%^&*()=+_`\\-\\|\\/'\\[\\]\\{\\}]|[?.,]*\\w)"}, // This partially fails
                             // {"HTML Tags", Boolean.FALSE, "(<script(\\s|\\S)*?</script>)|(<style(\\s|\\S)*?</style>)|(<!--(\\s|\\S)*?-->)|(</?(\\s|\\S)*?>)"}, // This hangs
                             // {"JWT", Boolean.TRUE,"^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"},       // Parsing failure in handleRange()!!
                     })
                     .flatMap(arr -> IntStream.range(0, 100)
                                              .mapToObj(index -> Arguments.of(arr[0], arr[1], arr[2], index)));
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void generateTest(String name, boolean useFind, String pattern, int seed) {
        RgxGen rgxGen = new RgxGen(pattern);
        String s = rgxGen.generate(TestingUtilities.newRandom(seed));
        System.out.println("Matching: '" + s + "'");
        assertTrue(matches(s, pattern, useFind), "Text: '" + s + "' does not match pattern " + pattern);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    public void generateNotMatchingTest(String name, boolean useFind, String pattern, int seed) {
        RgxGen rgxGen = new RgxGen(pattern);
        String s = rgxGen.generateNotMatching(TestingUtilities.newRandom(seed));
        System.out.println("Not Matching: '" + s + "'");
        assertFalse(matches(s, pattern, useFind), "Text: '" + s + "' does not match pattern " + pattern);
    }

    private boolean matches(String text, String pattern, boolean useFind) {
        Matcher matcher = Pattern.compile(pattern)
                                 .matcher(text);
        return useFind ? matcher.find() : matcher.matches();
    }
}
