package com.github.curiousoddman.rgxgen;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class CompleteTests {
    @Parameterized.Parameters(name = "{2}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.stream(new String[][]{
                {"Card number", "[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}"},
                {"IP v4", "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9])\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9])\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9])\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9])"},
                {"IP v6", "(([0-9a-f]{1,4}:){1,1}(:[0-9a-f]{1,4}){1,6})|(([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,5})|" +
                        "(([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,4})|(([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,3})|(([0-9a-f]{1,4}:){1,5}(:[0-9a-f]{1,4}){1,2})|" +
                        "(([0-9a-f]{1,4}:){1,6}(:[0-9a-f]{1,4}){1,1})|((([0-9a-f]{1,4}:){1,7}|:):)|(:(:[0-9a-f]{1,4}){1,7})|(((([0-9a-f]{1,4}:){6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)" +
                        "(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}))|((([0-9a-f]{1,4}:){5}[0-9a-f]{1,4}:(25[0-5]|2,[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}))|" +
                        "(([0-9a-f]{1,4}:){5}:[0-9a-f]{1,4}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|(([0-9a-f]{1,4}:){1,1}(:[0-9a-f]{1,4}){1,4}:" +
                        "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|(([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,3}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\." +
                        "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|(([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,2}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|" +
                        "(([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,1}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|((([0-9a-f]{1,4}:){1,5}|:):(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\." +
                        "(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|(:(:[0-9a-f]{1,4}){1,5}:(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})"},
                {"Simple XML tag", "<([a-z])>asdf<\\/\\1>"},
                {"XML tag", "<([a-z]+)[^<]{5,10}<\\/\\1>"},
                {"JustRandom", "\\w\\W\\d\\D\\s\\S"},
                {"JustRandom2", "[a-zA-Z0-9_]{5}"},
                {"JustRandom3", "[^a-zA-Z0-9_]{5}"},
                {"Email name-surname", "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"},
                {"Email quoted name left", "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]"},
                {"Email quoted name right", "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f]"},
                {"Email quoted name", "\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\""},
                {"Email before @", "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")"},
                {"Email after @ - dns name", "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\."},
                {"Email after @", "(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"},
                {"Email pattern", "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"},
                {"Name or Surname", "(-?([A-Z].\\s)?([A-Z][a-z]+)\\s?)+([A-Z]'([A-Z][a-z]+))?"},
                {"File Extension", "(\\.)[a-z]{1,4}$"},
                {"24 Hour Time", "([01]?[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?"},
                {"ddXmmXyyyy Date", "^(0?[1-9]|[12][0-9]|3[01])([ \\/\\-])(0?[1-9]|1[012])\\2([0-9][0-9][0-9][0-9])(([ -])([0-1]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9])?$"},
                {"mm/dd/yyyy Date", "^(0?[1-9]|1[0-2])[/](0?[1-9]|[12]\\d|3[01])[/](19|20)\\d{2}$"},
                {"24 or 32 bit colors", "(?:#|0x)?(?:[0-9A-Fa-f]{2}){3,4}"},
                {"rgb Value", "rgb\\((?:([0-9]{1,2}|1[0-9]{1,2}|2[0-4][0-9]|25[0-5]), ?)(?:([0-9]{1,2}|1[0-9]{1,2}|2[0-4][0-9]|25[0-5]), ?)(?:([0-9]{1,2}|1[0-9]{1,2}|2[0-4][0-9]|25[0-5]))\\)"},
                {"src of image tag", "^<\\s*img[^>]+src\\s*=\\s*([\"'])(.*?)\\1[^>]*>$"},
                {"Float value", "^[0-9]*.[0-9]*[1-9]+$"},
                {"Windows path", "^(([a-zA-Z]:)|(\\\\{2}\\w+)\\$?)(\\\\(\\w[\\w ]*.*))+$"},
                {"Dollar amounts", "\\$[0-9]+(.[0-9][0-9])?"},
                {"Youtube URL", "(?:https?://)?(?:(?:(?:www\\.?)?youtube\\.com(?:/(?:(?:watch\\?.*?(v=[^&\\s]+).*)|(?:v(/.*))|(channel/.+)|(?:user/(.+))|(?:results\\?(search_query=.+))))?)|(?:youtu\\.be(/.*)?))"},
                {"CSS comment", "(/\\*)(.|\r|\n)*?(\\*/)"},
                {"URL with optional protocol", "^((https?|ftp|file):\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$"},
                {"Password strength", "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$"},
                {"UK Postal code", "^(([gG][iI][rR] {0,}0[aA]{2})|(([aA][sS][cC][nN]|[sS][tT][hH][lL]|[tT][dD][cC][uU]|[bB][bB][nN][dD]|[bB][iI][qQ][qQ]|[fF][iI][qQ][qQ]|[pP][cC][rR][nN]|[sS][iI][qQ][qQ]|[iT][kK][cC][aA]) {0,}1[zZ]{2})|((([a-pr-uwyzA-PR-UWYZ][a-hk-yxA-HK-XY]?[0-9][0-9]?)|(([a-pr-uwyzA-PR-UWYZ][0-9][a-hjkstuwA-HJKSTUW])|([a-pr-uwyzA-PR-UWYZ][a-hk-yA-HK-Y][0-9][abehmnprv-yABEHMNPRV-Y]))) {0,}[0-9][abd-hjlnp-uw-zABD-HJLNP-UW-Z]{2}))$"},
                {"Semver", "^(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)\\.(0|[1-9][0-9]*)(?:-((?:0|[1-9][0-9]*|[0-9]*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9][0-9]*|[0-9]*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"},
                {"Periodic Table Elements", "\\b(?:A[cglmr-u]|B[aehikr]?|C[adefl-orsu]?|D[bsy]|E[rsu]|F[elmr]?|G[ade]|H[efgos]?|I[nr]?|Kr?|L[airuv]|M[dgont]|N[abdeiop]?|Os?|P[abdmortu]?|R[abe-hnu]|S[bcegimnr]?|T[abcehilm]|U(?:u[opst])?|V|W|Xe|Yb?|Z[nr])\\b"},
                {"2-5letter palindromes", "\\b(\\w)?(\\w)\\w?\\2\\1"},
                {"Russia Phone Number", "^((\\+7|7|8)+([0-9]){10})$|\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b"},
                {"Brainfuck code", "^[+-<>.,\\[\\] \t\n\r]+$"},

                // FIXME:
                // {"Unix Path", "/|((?=/)|\\.|\\.\\.|~|~(?=/))(/(?=[^/])[^/]+)*/?"},  // For this need to use find() instead of matches()
                // {"Hashtags", "\\B#([a-z0-9]{2,})(?![~!@#$%^&*()=+_`\\-\\|\\/'\\[\\]\\{\\}]|[?.,]*\\w)"}, // This partially fails
                // {"HTML Tags", "(<script(\\s|\\S)*?</script>)|(<style(\\s|\\S)*?</style>)|(<!--(\\s|\\S)*?-->)|(</?(\\s|\\S)*?>)"}, // This hangs
//                {"ISO-8601 Date", "^(?![+-]?\\d{4,5}-?(?:\\d{2}|W\\d{2})T)(?:|(\\d{4}|[+-]\\d{5})-?(?:|(0\\d|1[0-2])(?:|-?([0-2]\\d|3[0-1]))|([0-2]\\d{2}|3[0-5]\\d|36[0-6])|W([0-4]\\d|5[0-3])(?:|-?([1-7])))(?:(?!\\d)|T(?=\\d)))(?:|([01]\\d|2[0-4])(?:|:?([0-5]\\d)(?:|:?([0-5]\\d)(?:|\\.(\\d{3})))(?:|[zZ]|([+-](?:[01]\\d|2[0-4]))(?:|:?([0-5]\\d)))))$"},
//                {"Domain name", "(?!w{1,}\\.)(\\w+\\.?)([a-zA-Z]+)(\\.\\w+)"},
//                {"JWT", "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"},
//                {"Morse code", "^[.-]{1,5}(?:[ \\t]+[.-]{1,5})*(?:[ \\t]+[.-]{1,5}(?:[ \\t]+[.-]{1,5})*)*$"},
//                {"USA/Canada Zip codes", "(^\\d{5}(-\\d{4})?$)|(^[A-Z]{1}\\d{1}[A-Z]{1} *\\d{1}[A-Z]{1}\\d{1}$)"},
//                {"JS comments", "//(?![\\S]{2,}\\.[\\w]).*|/\\*(.|\n)+?\\*/"},
        })
                     .flatMap(arr -> IntStream.range(0, 100)
                                              .mapToObj(index -> new Object[]{arr[0], arr[1], index}))
                     .collect(Collectors.toList());
    }

    @Parameterized.Parameter
    public String aName;

    @Parameterized.Parameter(1)
    public String aRegex;

    @Parameterized.Parameter(2)
    public int aSeed;

    @Test
    public void generateTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        String s = rgxGen.generate(new Random(aSeed));
        assertTrue("Text: '" + s + "'does not match pattern " + aRegex, Pattern.compile(aRegex)
                                                                               .matcher(s)
                                                                               .matches());
    }

    @Test
    public void generateNotMatchingTest() {
        RgxGen rgxGen = new RgxGen(aRegex);
        String s = rgxGen.generateNotMatching(new Random(aSeed));
        assertFalse("Text: '" + s + "'does not match pattern " + aRegex, Pattern.compile(aRegex)
                                                                                .matcher(s)
                                                                                .matches());
    }
}
