package com.github.curiousoddman.rgxgen;

import com.github.curiousoddman.rgxgen.testutil.TestingUtilities;
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

public class CompleteTests {
    public static Stream<Arguments> getData() {
        return Arrays.stream(new Object[][]{
                             {"Card number", Boolean.FALSE, "[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}"},
                             {"IP v4", Boolean.FALSE, "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9])\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9])\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9])\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9])"},
                             {"IP v6", Boolean.FALSE, "(([0-9a-f]{1,4}:){1,1}(:[0-9a-f]{1,4}){1,6})|(([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,5})|" +
                                     "(([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,4})|(([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,3})|(([0-9a-f]{1,4}:){1,5}(:[0-9a-f]{1,4}){1,2})|" +
                                     "(([0-9a-f]{1,4}:){1,6}(:[0-9a-f]{1,4}){1,1})|((([0-9a-f]{1,4}:){1,7}|:):)|(:(:[0-9a-f]{1,4}){1,7})|(((([0-9a-f]{1,4}:){6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)" +
                                     "(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}))|((([0-9a-f]{1,4}:){5}[0-9a-f]{1,4}:(25[0-5]|2,[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}))|" +
                                     "(([0-9a-f]{1,4}:){5}:[0-9a-f]{1,4}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|(([0-9a-f]{1,4}:){1,1}(:[0-9a-f]{1,4}){1,4}:" +
                                     "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|(([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,3}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\." +
                                     "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|(([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,2}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|" +
                                     "(([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,1}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|((([0-9a-f]{1,4}:){1,5}|:):(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\." +
                                     "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|(:(:[0-9a-f]{1,4}){1,5}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})"},
                             {"Simple XML tag", Boolean.FALSE, "<([a-z])>asdf<\\/\\1>"},
                             {"XML tag", Boolean.FALSE, "<([a-z]+)[^<]{5,10}<\\/\\1>"},
                             {"JustRandom", Boolean.FALSE, "\\w\\W\\d\\D\\s\\S"},
                             {"JustRandom2", Boolean.FALSE, "[a-zA-Z0-9_]{5}"},
                             {"JustRandom3", Boolean.FALSE, "[^a-zA-Z0-9_]{5}"},
                             {"Email name-surname", Boolean.FALSE, "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"},
                             {"Email quoted name left", Boolean.FALSE, "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]"},
                             {"Email quoted name right", Boolean.FALSE, "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f]"},
                             {"Email quoted name", Boolean.FALSE, "\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\""},
                             {"Email before @", Boolean.FALSE, "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")"},
                             {"Email after @ - dns name", Boolean.FALSE, "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\."},
                             {"Email after @", Boolean.FALSE, "(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"},
                             {"Email pattern", Boolean.FALSE, "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"},
                             {"Name or Surname", Boolean.FALSE, "(-?([A-Z].\\s)?([A-Z][a-z]+)\\s?)+([A-Z]'([A-Z][a-z]+))?"},
                             {"File Extension", Boolean.FALSE, "(\\.)[a-z]{1,4}$"},
                             {"24 Hour Time", Boolean.FALSE, "([01]?[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?"},
                             {"ddXmmXyyyy Date", Boolean.FALSE, "^(0?[1-9]|[12][0-9]|3[01])([ \\/\\-])(0?[1-9]|1[012])\\2([0-9][0-9][0-9][0-9])(([ -])([0-1]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9])?$"},
                             {"mm/dd/yyyy Date", Boolean.FALSE, "^(0?[1-9]|1[0-2])[/](0?[1-9]|[12]\\d|3[01])[/](19|20)\\d{2}$"},
                             {"24 or 32 bit colors", Boolean.FALSE, "(?:#|0x)?(?:[0-9A-Fa-f]{2}){3,4}"},
                             {"rgb Value", Boolean.FALSE, "rgb\\((?:([0-9]{1,2}|1[0-9]{1,2}|2[0-4][0-9]|25[0-5]), ?)(?:([0-9]{1,2}|1[0-9]{1,2}|2[0-4][0-9]|25[0-5]), ?)(?:([0-9]{1,2}|1[0-9]{1,2}|2[0-4][0-9]|25[0-5]))\\)"},
                             {"src of image tag", Boolean.FALSE, "^<\\s*img[^>]+src\\s*=\\s*([\"'])(.*?)\\1[^>]*>$"},
                             {"Float value", Boolean.FALSE, "^[0-9]*.[0-9]*[1-9]+$"},
                             {"Windows path", Boolean.FALSE, "^(([a-zA-Z]:)|(\\\\{2}\\w+)\\$?)(\\\\(\\w[\\w ]*.*))+$"},
                             {"Dollar amounts", Boolean.FALSE, "\\$[0-9]+(.[0-9][0-9])?"},
                             {"Youtube URL", Boolean.FALSE, "(?:https?://)?(?:(?:(?:www\\.?)?youtube\\.com(?:/(?:(?:watch\\?.*?(v=[^&\\s]+).*)|(?:v(/.*))|(channel/.+)|(?:user/(.+))|(?:results\\?(search_query=.+))))?)|(?:youtu\\.be(/.*)?))"},
                             {"CSS comment", Boolean.FALSE, "(/\\*)(.|\r|\n)*?(\\*/)"},
                             {"URL with optional protocol", Boolean.FALSE, "^((https?|ftp|file):\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$"},
                             {"Password strength", Boolean.FALSE, "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$"},
                             {"UK Postal code", Boolean.FALSE, "^(([gG][iI][rR] {0,}0[aA]{2})|(([aA][sS][cC][nN]|[sS][tT][hH][lL]|[tT][dD][cC][uU]|[bB][bB][nN][dD]|[bB][iI][qQ][qQ]|[fF][iI][qQ][qQ]|[pP][cC][rR][nN]|[sS][iI][qQ][qQ]|[iT][kK][cC][aA]) {0,}1[zZ]{2})|((([a-pr-uwyzA-PR-UWYZ][a-hk-yxA-HK-XY]?[0-9][0-9]?)|(([a-pr-uwyzA-PR-UWYZ][0-9][a-hjkstuwA-HJKSTUW])|([a-pr-uwyzA-PR-UWYZ][a-hk-yA-HK-Y][0-9][abehmnprv-yABEHMNPRV-Y]))) {0,}[0-9][abd-hjlnp-uw-zABD-HJLNP-UW-Z]{2}))$"},
                             {"Semver", Boolean.FALSE, "^(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)(?:-((?:0|[1-9][0-9]*|[0-9]*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9][0-9]*|[0-9]*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"},
                             {"Periodic Table Elements", Boolean.FALSE, "\\b(?:A[cglmr-u]|B[aehikr]?|C[adefl-orsu]?|D[bsy]|E[rsu]|F[elmr]?|G[ade]|H[efgos]?|I[nr]?|Kr?|L[airuv]|M[dgont]|N[abdeiop]?|Os?|P[abdmortu]?|R[abe-hnu]|S[bcegimnr]?|T[abcehilm]|U(?:u[opst])?|V|W|Xe|Yb?|Z[nr])\\b"},
                             {"Russia Phone Number", Boolean.FALSE, "^((\\+7|7|8)+([0-9]){10})$|\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b"},
                             {"Brainfuck code", Boolean.FALSE, "^[+-<>.,\\[\\] \t\n\r]+$"},
                             {"USA/Canada Zip codes", Boolean.FALSE, "(^\\d{5}(-\\d{4})?$)|(^[A-Z]{1}\\d{1}[A-Z]{1} *\\d{1}[A-Z]{1}\\d{1}$)"},
                             {"JS comments", Boolean.TRUE, "//(?![\\S]{2,}\\.[\\w]).*|/\\*(.|\n)+\\*/"},
                             {"2-5 letter palindromes", Boolean.FALSE, "\\b(\\w?)(\\w)\\w?\\2\\1"},
                             })
                     .flatMap(arr -> IntStream.range(0, 100)
                                              .mapToObj(index -> Arguments.of(arr[0], arr[1], arr[2], index)));
    }


    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getData")
    public void generateTest(String aName, boolean aUseFind, String aRegex, int aSeed) {
        RgxGen rgxGen = RgxGen.parse(aRegex);
        String s = rgxGen.generate(TestingUtilities.newRandom(aSeed));
        assertTrue(matches(aRegex, s, aUseFind), "Text: '" + s + "'does not match pattern " + aRegex);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("getData")
    public void generateNotMatchingTest(String aName, boolean aUseFind, String aRegex, int aSeed) {
        RgxGen rgxGen = RgxGen.parse(aRegex);
        String s = rgxGen.generateNotMatching(TestingUtilities.newRandom(aSeed));
        assertFalse(matches(aRegex, s, aUseFind), "Text: '" + s + "'does not match pattern " + aRegex);
    }

    private boolean matches(String aRegex, String text, boolean aUseFind) {
        Matcher matcher = Pattern.compile(aRegex).matcher(text);
        return aUseFind ? matcher.find() : matcher.matches();
    }
}
