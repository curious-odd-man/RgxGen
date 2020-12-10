package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
@Ignore
public class TmpCompleteTests {
    @Parameterized.Parameters(name = "{3}: {0}")
    public static Collection<Object[]> data() {
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
                                              .mapToObj(index -> new Object[]{arr[0], arr[1], arr[2], index}))
                     .collect(Collectors.toList());
    }

    @Parameterized.Parameter
    public String aName;

    @Parameterized.Parameter(1)
    public boolean aUseFind;

    @Parameterized.Parameter(2)
    public String aRegex;

    @Parameterized.Parameter(3)
    public int aSeed;

    @Test
    public void generateTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        String s = rgxGen.generate(TestingUtilities.newRandom(aSeed));
        System.out.println("Matching: '" + s + "'");
        assertTrue("Text: '" + s + "' does not match pattern " + aRegex, matches(s));
    }

    @Test
    public void generateNotMatchingTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        String s = rgxGen.generateNotMatching(TestingUtilities.newRandom(aSeed));
        System.out.println("Not Matching: '" + s + "'");
        assertFalse("Text: '" + s + "' does not match pattern " + aRegex, matches(s));
    }

    private boolean matches(String text) {
        Matcher matcher = Pattern.compile(aRegex)
                                 .matcher(text);
        return aUseFind ? matcher.find() : matcher.matches();
    }
}
