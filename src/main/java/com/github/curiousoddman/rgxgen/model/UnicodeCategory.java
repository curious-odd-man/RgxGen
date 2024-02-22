package com.github.curiousoddman.rgxgen.model;

/* **************************************************************************
   Copyright 2019 Vladislavs Varslavans

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
/* **************************************************************************/

import com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider;
import com.github.curiousoddman.rgxgen.util.Util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.range;
import static com.github.curiousoddman.rgxgen.model.UnicodeCategoryConstants.*;
import static com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.ZERO_LENGTH_CHARACTER_ARRAY;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public enum UnicodeCategory {
    /**
     * The configuration is based on <a href="https://www.regular-expressions.info/unicode.html#category">...</a>
     */

    ANY_LETTER(keys("L", "Letter"), "Any kind of letter from any language", asList(LATIN_UPPERCASE, LATIN_LOWERCASE, RANGE_251, range('Ø', 'ö'), range('ø', 'ˁ'), RANGE_370, RANGE_200, range('Ͱ', 'ʹ'), range('Ͷ', 'ͷ'), range('ͺ', 'ͽ'), RANGE_136, range('Ύ', 'Ρ'), range('Σ', 'ϵ'), range('Ϸ', 'ҁ'), range('Ҋ', 'ԯ'), RANGE_481, RANGE_50, RANGE_230, RANGE_171, range('ؠ', 'ي'), RANGE_681, RANGE_48, RANGE_305, RANGE_501, RANGE_17, RANGE_549, RANGE_367, RANGE_108, RANGE_262, RANGE_522, RANGE_435, RANGE_375, RANGE_295, RANGE_29, RANGE_259, RANGE_580, range('ॱ', 'ঀ'), RANGE_77, RANGE_289, RANGE_374, RANGE_134, RANGE_393, RANGE_473, RANGE_541, RANGE_168, RANGE_618, RANGE_112, RANGE_201, RANGE_687, RANGE_125, RANGE_191, RANGE_258, RANGE_233, RANGE_31, RANGE_446, RANGE_648, RANGE_9, RANGE_507, RANGE_671, RANGE_3, RANGE_198, RANGE_264, RANGE_469, RANGE_566, RANGE_324, RANGE_488, RANGE_561, RANGE_654, RANGE_723, RANGE_74, RANGE_272, RANGE_358, RANGE_506, RANGE_606, RANGE_718, RANGE_100, RANGE_229, RANGE_620, RANGE_84, RANGE_180, RANGE_692, RANGE_208, RANGE_372, RANGE_445, RANGE_629, RANGE_727, RANGE_509, RANGE_2, RANGE_199, RANGE_562, RANGE_265, RANGE_451, RANGE_555, RANGE_485, RANGE_722, RANGE_575, RANGE_82, RANGE_536, RANGE_338, RANGE_605, RANGE_560, RANGE_126, range('เ', 'ๆ'), RANGE_357, RANGE_482, RANGE_30, RANGE_135, RANGE_312, RANGE_504, RANGE_572, RANGE_672, RANGE_243, RANGE_116, RANGE_65, RANGE_269, RANGE_138, RANGE_533, RANGE_38, RANGE_257, RANGE_484, RANGE_677, RANGE_110, RANGE_300, RANGE_596, range('ჼ', 'ቈ'), RANGE_635, RANGE_37, RANGE_253, RANGE_392, RANGE_551, RANGE_689, RANGE_670, RANGE_73, RANGE_283, RANGE_415, RANGE_47, RANGE_538, RANGE_691, RANGE_708, RANGE_684, RANGE_351, RANGE_24, RANGE_705, RANGE_365, RANGE_319, RANGE_563, RANGE_151, RANGE_453, RANGE_115, RANGE_68, RANGE_8, RANGE_318, RANGE_716, range('ᠠ', 'ᡷ'), RANGE_339, RANGE_498, RANGE_647, RANGE_158, RANGE_409, RANGE_359, RANGE_712, RANGE_277, RANGE_520, RANGE_486, RANGE_278, RANGE_166, RANGE_43, RANGE_225, RANGE_496, RANGE_529, RANGE_703, range('ᱚ', 'ᱽ'), RANGE_345, RANGE_388, RANGE_500, RANGE_639, range('ᴀ', 'ᶿ'), range('Ḁ', 'ἕ'), RANGE_664, range('ἠ', 'ὅ'), RANGE_232, RANGE_404, range('Ὗ', 'ώ'), range('ᾀ', 'ᾴ'), range('ᾶ', 'ᾼ'), RANGE_643, range('ῆ', 'ῌ'), RANGE_221, range('ῖ', 'Ί'), range('ῠ', 'Ῥ'), RANGE_217, range('ῶ', 'ῼ'), RANGE_679, range('ℊ', 'ℓ'), RANGE_690, RANGE_323, range('ℯ', 'ℹ'), range('ℼ', 'ℿ'), range('ⅅ', 'ⅉ'), range('Ↄ', 'ↄ'), RANGE_537, RANGE_107, range('Ⱡ', 'ⳤ'), range('Ⳬ', 'ⳮ'), range('Ⳳ', 'ⳳ'), RANGE_159, RANGE_468, RANGE_704, RANGE_644, RANGE_98, RANGE_266, RANGE_436, RANGE_603, RANGE_46, RANGE_218, RANGE_384, range('々', '〆'), RANGE_109, range('〻', '〼'), RANGE_471, range('ゝ', 'ゟ'), RANGE_343, range('ー', 'ヿ'), RANGE_274, RANGE_508, RANGE_662, RANGE_178, RANGE_666, RANGE_699, range('ꀀ', 'ꒌ'), range('ꓐ', 'ꓽ'), range('ꔀ', 'ꘌ'), RANGE_124, RANGE_675, range('Ꙁ', 'ꙮ'), range('ꙿ', 'ꚝ'), RANGE_326, RANGE_650, range('Ꜣ', 'ꞈ'), range('Ꞌ', 'Ɪ'), range('Ʞ', 'ꞷ'), range('ꟷ', 'ꠁ'), RANGE_591, RANGE_683, RANGE_36, RANGE_455, RANGE_403, RANGE_593, RANGE_379, RANGE_444, RANGE_6, RANGE_57, RANGE_576, range('ꧦ', 'ꧯ'), RANGE_371, RANGE_547, RANGE_437, RANGE_526, range('ꩠ', 'ꩶ'), RANGE_327, RANGE_14, RANGE_78, range('ꫛ', 'ꫝ'), RANGE_195, range('ꫲ', 'ꫴ'), RANGE_186, RANGE_336, RANGE_524, RANGE_120, RANGE_268, RANGE_475, range('ꭜ', 'ꭥ'), range('ꭰ', 'ꯢ'), RANGE_111, RANGE_270, RANGE_132, RANGE_299, RANGE_49, RANGE_153, RANGE_571, RANGE_83, RANGE_304, RANGE_599, RANGE_63, RANGE_133, RANGE_219, RANGE_394, RANGE_427, RANGE_385, RANGE_173, RANGE_729, RANGE_189, RANGE_127, RANGE_81, range('ｦ', 'ﾾ'), RANGE_658, RANGE_76, RANGE_275, RANGE_418, GEORGIAN_EXTENDED), new Character[]{'ª', 'µ', 'º', 'ˬ', 'ˮ', 'Ϳ', 'Ά', 'Ό', 'ՙ', 'ە', 'ۿ', 'ܐ', 'ޱ', 'ߺ', 'ࠚ', 'ࠤ', 'ࠨ', 'ऽ', 'ॐ', 'ল', 'ঽ', 'ৎ', 'ৼ', 'ਫ਼', 'ઽ', 'ૐ', 'ૹ', 'ଽ', 'ୱ', 'ஃ', 'ஜ', 'ௐ', 'ఽ', 'ಀ', 'ಽ', 'ೞ', 'ഽ', 'ൎ', 'ල', 'ຄ', 'ຊ', 'ຍ', 'ລ', 'ວ', 'ຽ', 'ໆ', 'ༀ', 'ဿ', 'ၡ', 'ႎ', 'Ⴧ', 'Ⴭ', 'ቘ', 'ዀ', 'ៗ', 'ៜ', 'ᢪ', 'ᪧ', 'Ὑ', 'Ὓ', 'Ὕ', 'ι', 'ⁱ', 'ⁿ', 'ℂ', 'ℇ', 'ℕ', 'ℤ', 'Ω', 'ℨ', 'ⅎ', 'ⴧ', 'ⴭ', 'ⵯ', 'ⸯ', 'ꣻ', 'ꣽ', 'ꧏ', 'ꩺ', 'ꪱ', 'ꫀ', 'ꫂ', 'יִ', 'מּ'}),
    LOWERCASE_LETTER(keys("Ll", "Lowercase_Letter"), "a lowercase letter that has an uppercase variant.", asList(LATIN_LOWERCASE, range('ß', 'ö'), range('ø', 'ÿ'), range('ķ', 'ĸ'), range('ň', 'ŉ'), range('ž', 'ƀ'), range('ƌ', 'ƍ'), range('ƙ', 'ƛ'), range('ƪ', 'ƫ'), range('ƹ', 'ƺ'), range('ƽ', 'ƿ'), range('ǜ', 'ǝ'), range('ǯ', 'ǰ'), range('ȳ', 'ȹ'), range('ȿ', 'ɀ'), range('ɏ', 'ʓ'), range('ʕ', 'ʯ'), range('ͻ', 'ͽ'), range('ά', 'ώ'), range('ϐ', 'ϑ'), range('ϕ', 'ϗ'), range('ϯ', 'ϳ'), range('ϻ', 'ϼ'), range('а', 'џ'), range('ӎ', 'ӏ'), RANGE_50, RANGE_351, RANGE_345, range('ᴀ', 'ᴫ'), range('ᵫ', 'ᵷ'), range('ᵹ', 'ᶚ'), range('ẕ', 'ẝ'), range('ỿ', 'ἇ'), range('ἐ', 'ἕ'), range('ἠ', 'ἧ'), range('ἰ', 'ἷ'), range('ὀ', 'ὅ'), RANGE_404, range('ὠ', 'ὧ'), range('ὰ', 'ώ'), range('ᾀ', 'ᾇ'), range('ᾐ', 'ᾗ'), range('ᾠ', 'ᾧ'), range('ᾰ', 'ᾴ'), range('ᾶ', 'ᾷ'), RANGE_643, range('ῆ', 'ῇ'), RANGE_221, range('ῖ', 'ῗ'), range('ῠ', 'ῧ'), RANGE_217, range('ῶ', 'ῷ'), range('ℎ', 'ℏ'), range('ℼ', 'ℽ'), range('ⅆ', 'ⅉ'), RANGE_107, range('ⱥ', 'ⱦ'), range('ⱳ', 'ⱴ'), range('ⱶ', 'ⱻ'), range('ⳣ', 'ⳤ'), RANGE_159, range('ꜯ', 'ꜱ'), range('ꝱ', 'ꝸ'), range('ꞓ', 'ꞕ'), RANGE_475, range('ꭠ', 'ꭥ'), range('ꭰ', 'ꮿ'), RANGE_153, RANGE_571, RANGE_81), new Character[]{'µ', 'ā', 'ă', 'ą', 'ć', 'ĉ', 'ċ', 'č', 'ď', 'đ', 'ē', 'ĕ', 'ė', 'ę', 'ě', 'ĝ', 'ğ', 'ġ', 'ģ', 'ĥ', 'ħ', 'ĩ', 'ī', 'ĭ', 'į', 'ı', 'ĳ', 'ĵ', 'ĺ', 'ļ', 'ľ', 'ŀ', 'ł', 'ń', 'ņ', 'ŋ', 'ō', 'ŏ', 'ő', 'œ', 'ŕ', 'ŗ', 'ř', 'ś', 'ŝ', 'ş', 'š', 'ţ', 'ť', 'ŧ', 'ũ', 'ū', 'ŭ', 'ů', 'ű', 'ų', 'ŵ', 'ŷ', 'ź', 'ż', 'ƃ', 'ƅ', 'ƈ', 'ƒ', 'ƕ', 'ƞ', 'ơ', 'ƣ', 'ƥ', 'ƨ', 'ƭ', 'ư', 'ƴ', 'ƶ', 'ǆ', 'ǉ', 'ǌ', 'ǎ', 'ǐ', 'ǒ', 'ǔ', 'ǖ', 'ǘ', 'ǚ', 'ǟ', 'ǡ', 'ǣ', 'ǥ', 'ǧ', 'ǩ', 'ǫ', 'ǭ', 'ǳ', 'ǵ', 'ǹ', 'ǻ', 'ǽ', 'ǿ', 'ȁ', 'ȃ', 'ȅ', 'ȇ', 'ȉ', 'ȋ', 'ȍ', 'ȏ', 'ȑ', 'ȓ', 'ȕ', 'ȗ', 'ș', 'ț', 'ȝ', 'ȟ', 'ȡ', 'ȣ', 'ȥ', 'ȧ', 'ȩ', 'ȫ', 'ȭ', 'ȯ', 'ȱ', 'ȼ', 'ɂ', 'ɇ', 'ɉ', 'ɋ', 'ɍ', 'ͱ', 'ͳ', 'ͷ', 'ΐ', 'ϙ', 'ϛ', 'ϝ', 'ϟ', 'ϡ', 'ϣ', 'ϥ', 'ϧ', 'ϩ', 'ϫ', 'ϭ', 'ϵ', 'ϸ', 'ѡ', 'ѣ', 'ѥ', 'ѧ', 'ѩ', 'ѫ', 'ѭ', 'ѯ', 'ѱ', 'ѳ', 'ѵ', 'ѷ', 'ѹ', 'ѻ', 'ѽ', 'ѿ', 'ҁ', 'ҋ', 'ҍ', 'ҏ', 'ґ', 'ғ', 'ҕ', 'җ', 'ҙ', 'қ', 'ҝ', 'ҟ', 'ҡ', 'ң', 'ҥ', 'ҧ', 'ҩ', 'ҫ', 'ҭ', 'ү', 'ұ', 'ҳ', 'ҵ', 'ҷ', 'ҹ', 'һ', 'ҽ', 'ҿ', 'ӂ', 'ӄ', 'ӆ', 'ӈ', 'ӊ', 'ӌ', 'ӑ', 'ӓ', 'ӕ', 'ӗ', 'ә', 'ӛ', 'ӝ', 'ӟ', 'ӡ', 'ӣ', 'ӥ', 'ӧ', 'ө', 'ӫ', 'ӭ', 'ӯ', 'ӱ', 'ӳ', 'ӵ', 'ӷ', 'ӹ', 'ӻ', 'ӽ', 'ӿ', 'ԁ', 'ԃ', 'ԅ', 'ԇ', 'ԉ', 'ԋ', 'ԍ', 'ԏ', 'ԑ', 'ԓ', 'ԕ', 'ԗ', 'ԙ', 'ԛ', 'ԝ', 'ԟ', 'ԡ', 'ԣ', 'ԥ', 'ԧ', 'ԩ', 'ԫ', 'ԭ', 'ԯ', 'ḁ', 'ḃ', 'ḅ', 'ḇ', 'ḉ', 'ḋ', 'ḍ', 'ḏ', 'ḑ', 'ḓ', 'ḕ', 'ḗ', 'ḙ', 'ḛ', 'ḝ', 'ḟ', 'ḡ', 'ḣ', 'ḥ', 'ḧ', 'ḩ', 'ḫ', 'ḭ', 'ḯ', 'ḱ', 'ḳ', 'ḵ', 'ḷ', 'ḹ', 'ḻ', 'ḽ', 'ḿ', 'ṁ', 'ṃ', 'ṅ', 'ṇ', 'ṉ', 'ṋ', 'ṍ', 'ṏ', 'ṑ', 'ṓ', 'ṕ', 'ṗ', 'ṙ', 'ṛ', 'ṝ', 'ṟ', 'ṡ', 'ṣ', 'ṥ', 'ṧ', 'ṩ', 'ṫ', 'ṭ', 'ṯ', 'ṱ', 'ṳ', 'ṵ', 'ṷ', 'ṹ', 'ṻ', 'ṽ', 'ṿ', 'ẁ', 'ẃ', 'ẅ', 'ẇ', 'ẉ', 'ẋ', 'ẍ', 'ẏ', 'ẑ', 'ẓ', 'ẟ', 'ạ', 'ả', 'ấ', 'ầ', 'ẩ', 'ẫ', 'ậ', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ', 'ẹ', 'ẻ', 'ẽ', 'ế', 'ề', 'ể', 'ễ', 'ệ', 'ỉ', 'ị', 'ọ', 'ỏ', 'ố', 'ồ', 'ổ', 'ỗ', 'ộ', 'ớ', 'ờ', 'ở', 'ỡ', 'ợ', 'ụ', 'ủ', 'ứ', 'ừ', 'ử', 'ữ', 'ự', 'ỳ', 'ỵ', 'ỷ', 'ỹ', 'ỻ', 'ỽ', 'ι', 'ℊ', 'ℓ', 'ℯ', 'ℴ', 'ℹ', 'ⅎ', 'ↄ', 'ⱡ', 'ⱨ', 'ⱪ', 'ⱬ', 'ⱱ', 'ⲁ', 'ⲃ', 'ⲅ', 'ⲇ', 'ⲉ', 'ⲋ', 'ⲍ', 'ⲏ', 'ⲑ', 'ⲓ', 'ⲕ', 'ⲗ', 'ⲙ', 'ⲛ', 'ⲝ', 'ⲟ', 'ⲡ', 'ⲣ', 'ⲥ', 'ⲧ', 'ⲩ', 'ⲫ', 'ⲭ', 'ⲯ', 'ⲱ', 'ⲳ', 'ⲵ', 'ⲷ', 'ⲹ', 'ⲻ', 'ⲽ', 'ⲿ', 'ⳁ', 'ⳃ', 'ⳅ', 'ⳇ', 'ⳉ', 'ⳋ', 'ⳍ', 'ⳏ', 'ⳑ', 'ⳓ', 'ⳕ', 'ⳗ', 'ⳙ', 'ⳛ', 'ⳝ', 'ⳟ', 'ⳡ', 'ⳬ', 'ⳮ', 'ⳳ', 'ⴧ', 'ⴭ', 'ꙁ', 'ꙃ', 'ꙅ', 'ꙇ', 'ꙉ', 'ꙋ', 'ꙍ', 'ꙏ', 'ꙑ', 'ꙓ', 'ꙕ', 'ꙗ', 'ꙙ', 'ꙛ', 'ꙝ', 'ꙟ', 'ꙡ', 'ꙣ', 'ꙥ', 'ꙧ', 'ꙩ', 'ꙫ', 'ꙭ', 'ꚁ', 'ꚃ', 'ꚅ', 'ꚇ', 'ꚉ', 'ꚋ', 'ꚍ', 'ꚏ', 'ꚑ', 'ꚓ', 'ꚕ', 'ꚗ', 'ꚙ', 'ꚛ', 'ꜣ', 'ꜥ', 'ꜧ', 'ꜩ', 'ꜫ', 'ꜭ', 'ꜳ', 'ꜵ', 'ꜷ', 'ꜹ', 'ꜻ', 'ꜽ', 'ꜿ', 'ꝁ', 'ꝃ', 'ꝅ', 'ꝇ', 'ꝉ', 'ꝋ', 'ꝍ', 'ꝏ', 'ꝑ', 'ꝓ', 'ꝕ', 'ꝗ', 'ꝙ', 'ꝛ', 'ꝝ', 'ꝟ', 'ꝡ', 'ꝣ', 'ꝥ', 'ꝧ', 'ꝩ', 'ꝫ', 'ꝭ', 'ꝯ', 'ꝺ', 'ꝼ', 'ꝿ', 'ꞁ', 'ꞃ', 'ꞅ', 'ꞇ', 'ꞌ', 'ꞎ', 'ꞑ', 'ꞗ', 'ꞙ', 'ꞛ', 'ꞝ', 'ꞟ', 'ꞡ', 'ꞣ', 'ꞥ', 'ꞧ', 'ꞩ', 'ꞵ', 'ꞷ', 'ꟺ'}),
    UPPERCASE_LETTER(keys("Lu", "Uppercase_Letter"), "an uppercase letter that has a lowercase variant.", asList(LATIN_UPPERCASE, RANGE_251, range('Ø', 'Þ'), range('Ÿ', 'Ź'), range('Ɓ', 'Ƃ'), range('Ɔ', 'Ƈ'), range('Ɖ', 'Ƌ'), range('Ǝ', 'Ƒ'), range('Ɠ', 'Ɣ'), range('Ɩ', 'Ƙ'), range('Ɯ', 'Ɲ'), range('Ɵ', 'Ơ'), range('Ʀ', 'Ƨ'), range('Ʈ', 'Ư'), range('Ʊ', 'Ƴ'), range('Ʒ', 'Ƹ'), range('Ƕ', 'Ǹ'), range('Ⱥ', 'Ȼ'), range('Ƚ', 'Ⱦ'), range('Ƀ', 'Ɇ'), RANGE_136, range('Ύ', 'Ώ'), range('Α', 'Ρ'), range('Σ', 'Ϋ'), range('ϒ', 'ϔ'), range('Ϲ', 'Ϻ'), range('Ͻ', 'Я'), range('Ӏ', 'Ӂ'), RANGE_481, RANGE_300, RANGE_684, range('Ἀ', 'Ἇ'), RANGE_664, range('Ἠ', 'Ἧ'), range('Ἰ', 'Ἷ'), RANGE_232, range('Ὠ', 'Ὧ'), range('Ᾰ', 'Ά'), range('Ὲ', 'Ή'), range('Ῐ', 'Ί'), range('Ῠ', 'Ῥ'), range('Ὸ', 'Ώ'), range('ℋ', 'ℍ'), range('ℐ', 'ℒ'), RANGE_690, RANGE_323, range('ℰ', 'ℳ'), range('ℾ', 'ℿ'), RANGE_537, range('Ɫ', 'Ɽ'), range('Ɑ', 'Ɒ'), range('Ȿ', 'Ⲁ'), range('Ᵹ', 'Ꝿ'), range('Ɦ', 'Ɪ'), range('Ʞ', 'Ꞵ'), RANGE_127), new Character[]{'Ā', 'Ă', 'Ą', 'Ć', 'Ĉ', 'Ċ', 'Č', 'Ď', 'Đ', 'Ē', 'Ĕ', 'Ė', 'Ę', 'Ě', 'Ĝ', 'Ğ', 'Ġ', 'Ģ', 'Ĥ', 'Ħ', 'Ĩ', 'Ī', 'Ĭ', 'Į', 'İ', 'Ĳ', 'Ĵ', 'Ķ', 'Ĺ', 'Ļ', 'Ľ', 'Ŀ', 'Ł', 'Ń', 'Ņ', 'Ň', 'Ŋ', 'Ō', 'Ŏ', 'Ő', 'Œ', 'Ŕ', 'Ŗ', 'Ř', 'Ś', 'Ŝ', 'Ş', 'Š', 'Ţ', 'Ť', 'Ŧ', 'Ũ', 'Ū', 'Ŭ', 'Ů', 'Ű', 'Ų', 'Ŵ', 'Ŷ', 'Ż', 'Ž', 'Ƅ', 'Ƣ', 'Ƥ', 'Ʃ', 'Ƭ', 'Ƶ', 'Ƽ', 'Ǆ', 'Ǉ', 'Ǌ', 'Ǎ', 'Ǐ', 'Ǒ', 'Ǔ', 'Ǖ', 'Ǘ', 'Ǚ', 'Ǜ', 'Ǟ', 'Ǡ', 'Ǣ', 'Ǥ', 'Ǧ', 'Ǩ', 'Ǫ', 'Ǭ', 'Ǯ', 'Ǳ', 'Ǵ', 'Ǻ', 'Ǽ', 'Ǿ', 'Ȁ', 'Ȃ', 'Ȅ', 'Ȇ', 'Ȉ', 'Ȋ', 'Ȍ', 'Ȏ', 'Ȑ', 'Ȓ', 'Ȕ', 'Ȗ', 'Ș', 'Ț', 'Ȝ', 'Ȟ', 'Ƞ', 'Ȣ', 'Ȥ', 'Ȧ', 'Ȩ', 'Ȫ', 'Ȭ', 'Ȯ', 'Ȱ', 'Ȳ', 'Ɂ', 'Ɉ', 'Ɋ', 'Ɍ', 'Ɏ', 'Ͱ', 'Ͳ', 'Ͷ', 'Ϳ', 'Ά', 'Ό', 'Ϗ', 'Ϙ', 'Ϛ', 'Ϝ', 'Ϟ', 'Ϡ', 'Ϣ', 'Ϥ', 'Ϧ', 'Ϩ', 'Ϫ', 'Ϭ', 'Ϯ', 'ϴ', 'Ϸ', 'Ѡ', 'Ѣ', 'Ѥ', 'Ѧ', 'Ѩ', 'Ѫ', 'Ѭ', 'Ѯ', 'Ѱ', 'Ѳ', 'Ѵ', 'Ѷ', 'Ѹ', 'Ѻ', 'Ѽ', 'Ѿ', 'Ҁ', 'Ҋ', 'Ҍ', 'Ҏ', 'Ґ', 'Ғ', 'Ҕ', 'Җ', 'Ҙ', 'Қ', 'Ҝ', 'Ҟ', 'Ҡ', 'Ң', 'Ҥ', 'Ҧ', 'Ҩ', 'Ҫ', 'Ҭ', 'Ү', 'Ұ', 'Ҳ', 'Ҵ', 'Ҷ', 'Ҹ', 'Һ', 'Ҽ', 'Ҿ', 'Ӄ', 'Ӆ', 'Ӈ', 'Ӊ', 'Ӌ', 'Ӎ', 'Ӑ', 'Ӓ', 'Ӕ', 'Ӗ', 'Ә', 'Ӛ', 'Ӝ', 'Ӟ', 'Ӡ', 'Ӣ', 'Ӥ', 'Ӧ', 'Ө', 'Ӫ', 'Ӭ', 'Ӯ', 'Ӱ', 'Ӳ', 'Ӵ', 'Ӷ', 'Ӹ', 'Ӻ', 'Ӽ', 'Ӿ', 'Ԁ', 'Ԃ', 'Ԅ', 'Ԇ', 'Ԉ', 'Ԋ', 'Ԍ', 'Ԏ', 'Ԑ', 'Ԓ', 'Ԕ', 'Ԗ', 'Ԙ', 'Ԛ', 'Ԝ', 'Ԟ', 'Ԡ', 'Ԣ', 'Ԥ', 'Ԧ', 'Ԩ', 'Ԫ', 'Ԭ', 'Ԯ', 'Ⴧ', 'Ⴭ', 'Ḁ', 'Ḃ', 'Ḅ', 'Ḇ', 'Ḉ', 'Ḋ', 'Ḍ', 'Ḏ', 'Ḑ', 'Ḓ', 'Ḕ', 'Ḗ', 'Ḙ', 'Ḛ', 'Ḝ', 'Ḟ', 'Ḡ', 'Ḣ', 'Ḥ', 'Ḧ', 'Ḩ', 'Ḫ', 'Ḭ', 'Ḯ', 'Ḱ', 'Ḳ', 'Ḵ', 'Ḷ', 'Ḹ', 'Ḻ', 'Ḽ', 'Ḿ', 'Ṁ', 'Ṃ', 'Ṅ', 'Ṇ', 'Ṉ', 'Ṋ', 'Ṍ', 'Ṏ', 'Ṑ', 'Ṓ', 'Ṕ', 'Ṗ', 'Ṙ', 'Ṛ', 'Ṝ', 'Ṟ', 'Ṡ', 'Ṣ', 'Ṥ', 'Ṧ', 'Ṩ', 'Ṫ', 'Ṭ', 'Ṯ', 'Ṱ', 'Ṳ', 'Ṵ', 'Ṷ', 'Ṹ', 'Ṻ', 'Ṽ', 'Ṿ', 'Ẁ', 'Ẃ', 'Ẅ', 'Ẇ', 'Ẉ', 'Ẋ', 'Ẍ', 'Ẏ', 'Ẑ', 'Ẓ', 'Ẕ', 'ẞ', 'Ạ', 'Ả', 'Ấ', 'Ầ', 'Ẩ', 'Ẫ', 'Ậ', 'Ắ', 'Ằ', 'Ẳ', 'Ẵ', 'Ặ', 'Ẹ', 'Ẻ', 'Ẽ', 'Ế', 'Ề', 'Ể', 'Ễ', 'Ệ', 'Ỉ', 'Ị', 'Ọ', 'Ỏ', 'Ố', 'Ồ', 'Ổ', 'Ỗ', 'Ộ', 'Ớ', 'Ờ', 'Ở', 'Ỡ', 'Ợ', 'Ụ', 'Ủ', 'Ứ', 'Ừ', 'Ử', 'Ữ', 'Ự', 'Ỳ', 'Ỵ', 'Ỷ', 'Ỹ', 'Ỻ', 'Ỽ', 'Ỿ', 'Ὑ', 'Ὓ', 'Ὕ', 'Ὗ', 'ℂ', 'ℇ', 'ℕ', 'ℤ', 'Ω', 'ℨ', 'ⅅ', 'Ↄ', 'Ⱡ', 'Ⱨ', 'Ⱪ', 'Ⱬ', 'Ⱳ', 'Ⱶ', 'Ⲃ', 'Ⲅ', 'Ⲇ', 'Ⲉ', 'Ⲋ', 'Ⲍ', 'Ⲏ', 'Ⲑ', 'Ⲓ', 'Ⲕ', 'Ⲗ', 'Ⲙ', 'Ⲛ', 'Ⲝ', 'Ⲟ', 'Ⲡ', 'Ⲣ', 'Ⲥ', 'Ⲧ', 'Ⲩ', 'Ⲫ', 'Ⲭ', 'Ⲯ', 'Ⲱ', 'Ⲳ', 'Ⲵ', 'Ⲷ', 'Ⲹ', 'Ⲻ', 'Ⲽ', 'Ⲿ', 'Ⳁ', 'Ⳃ', 'Ⳅ', 'Ⳇ', 'Ⳉ', 'Ⳋ', 'Ⳍ', 'Ⳏ', 'Ⳑ', 'Ⳓ', 'Ⳕ', 'Ⳗ', 'Ⳙ', 'Ⳛ', 'Ⳝ', 'Ⳟ', 'Ⳡ', 'Ⳣ', 'Ⳬ', 'Ⳮ', 'Ⳳ', 'Ꙁ', 'Ꙃ', 'Ꙅ', 'Ꙇ', 'Ꙉ', 'Ꙋ', 'Ꙍ', 'Ꙏ', 'Ꙑ', 'Ꙓ', 'Ꙕ', 'Ꙗ', 'Ꙙ', 'Ꙛ', 'Ꙝ', 'Ꙟ', 'Ꙡ', 'Ꙣ', 'Ꙥ', 'Ꙧ', 'Ꙩ', 'Ꙫ', 'Ꙭ', 'Ꚁ', 'Ꚃ', 'Ꚅ', 'Ꚇ', 'Ꚉ', 'Ꚋ', 'Ꚍ', 'Ꚏ', 'Ꚑ', 'Ꚓ', 'Ꚕ', 'Ꚗ', 'Ꚙ', 'Ꚛ', 'Ꜣ', 'Ꜥ', 'Ꜧ', 'Ꜩ', 'Ꜫ', 'Ꜭ', 'Ꜯ', 'Ꜳ', 'Ꜵ', 'Ꜷ', 'Ꜹ', 'Ꜻ', 'Ꜽ', 'Ꜿ', 'Ꝁ', 'Ꝃ', 'Ꝅ', 'Ꝇ', 'Ꝉ', 'Ꝋ', 'Ꝍ', 'Ꝏ', 'Ꝑ', 'Ꝓ', 'Ꝕ', 'Ꝗ', 'Ꝙ', 'Ꝛ', 'Ꝝ', 'Ꝟ', 'Ꝡ', 'Ꝣ', 'Ꝥ', 'Ꝧ', 'Ꝩ', 'Ꝫ', 'Ꝭ', 'Ꝯ', 'Ꝺ', 'Ꝼ', 'Ꞁ', 'Ꞃ', 'Ꞅ', 'Ꞇ', 'Ꞌ', 'Ɥ', 'Ꞑ', 'Ꞓ', 'Ꞗ', 'Ꞙ', 'Ꞛ', 'Ꞝ', 'Ꞟ', 'Ꞡ', 'Ꞣ', 'Ꞥ', 'Ꞧ', 'Ꞩ', 'Ꞷ'}),
    TITLECASE_LETTER(keys("Lt", "Titlecase_Letter"), "a letter that appears at the start of a word when only the first letter of the word is capitalized.", asList(range('ᾈ', 'ᾏ'), range('ᾘ', 'ᾟ'), range('ᾨ', 'ᾯ')), new Character[]{'ǅ', 'ǈ', 'ǋ', 'ǲ', 'ᾼ', 'ῌ', 'ῼ'}),
    MODIFIER_LETTER(keys("Lm", "Modifier_Letter"), "a special character that is used like a letter.", asList(range('ʰ', 'ˁ'), RANGE_370, RANGE_200, RANGE_305, RANGE_262, range('ᱸ', 'ᱽ'), range('ᴬ', 'ᵪ'), range('ᶛ', 'ᶿ'), RANGE_679, range('ⱼ', 'ⱽ'), RANGE_109, range('ゝ', 'ゞ'), range('ー', 'ヾ'), range('ꓸ', 'ꓽ'), range('ꚜ', 'ꚝ'), RANGE_650, range('ꟸ', 'ꟹ'), range('ꫳ', 'ꫴ'), range('ꭜ', 'ꭟ'), range('ﾞ', 'ﾟ')), new Character[]{'ˬ', 'ˮ', 'ʹ', 'ͺ', 'ՙ', 'ـ', 'ߺ', 'ࠚ', 'ࠤ', 'ࠨ', 'ॱ', 'ๆ', 'ໆ', 'ჼ', 'ៗ', 'ᡃ', 'ᪧ', 'ᵸ', 'ⁱ', 'ⁿ', 'ⵯ', 'ⸯ', '々', '〻', 'ꀕ', 'ꘌ', 'ꙿ', 'ꝰ', 'ꞈ', 'ꧏ', 'ꧦ', 'ꩰ', 'ꫝ', 'ｰ'}),
    OTHER_LETTER(keys("Lo", "Other_Letter"), "a letter or ideograph that does not have lowercase and uppercase variants.", asList(range('ǀ', 'ǃ'), RANGE_230, RANGE_171, range('ؠ', 'ؿ'), range('ف', 'ي'), RANGE_681, RANGE_48, RANGE_501, RANGE_17, RANGE_549, RANGE_367, RANGE_108, RANGE_522, RANGE_435, RANGE_375, RANGE_295, RANGE_29, RANGE_259, RANGE_580, range('ॲ', 'ঀ'), RANGE_77, RANGE_289, RANGE_374, RANGE_134, RANGE_393, RANGE_473, RANGE_541, RANGE_168, RANGE_618, RANGE_112, RANGE_201, RANGE_687, RANGE_125, RANGE_191, RANGE_258, RANGE_233, RANGE_31, RANGE_446, RANGE_648, RANGE_9, RANGE_507, RANGE_671, RANGE_3, RANGE_198, RANGE_264, RANGE_469, RANGE_566, RANGE_324, RANGE_488, RANGE_561, RANGE_654, RANGE_723, RANGE_74, RANGE_272, RANGE_358, RANGE_506, RANGE_606, RANGE_718, RANGE_100, RANGE_229, RANGE_620, RANGE_84, RANGE_180, RANGE_692, RANGE_208, RANGE_372, RANGE_445, RANGE_629, RANGE_727, RANGE_509, RANGE_2, RANGE_199, RANGE_562, RANGE_265, RANGE_451, RANGE_555, RANGE_485, RANGE_722, RANGE_575, RANGE_82, RANGE_536, RANGE_338, RANGE_605, RANGE_560, RANGE_126, range('เ', 'ๅ'), RANGE_357, RANGE_482, RANGE_30, RANGE_135, RANGE_312, RANGE_504, RANGE_572, RANGE_672, RANGE_243, RANGE_116, RANGE_65, RANGE_269, RANGE_138, RANGE_533, RANGE_38, RANGE_257, RANGE_484, RANGE_677, RANGE_110, RANGE_596, range('ჽ', 'ቈ'), RANGE_635, RANGE_37, RANGE_253, RANGE_392, RANGE_551, RANGE_689, RANGE_670, RANGE_73, RANGE_283, RANGE_415, RANGE_47, RANGE_538, RANGE_691, RANGE_708, RANGE_24, RANGE_705, RANGE_365, RANGE_319, RANGE_563, RANGE_151, RANGE_453, RANGE_115, RANGE_68, RANGE_8, RANGE_318, RANGE_716, range('ᠠ', 'ᡂ'), range('ᡄ', 'ᡷ'), RANGE_339, RANGE_498, RANGE_647, RANGE_158, RANGE_409, RANGE_359, RANGE_712, RANGE_277, RANGE_520, RANGE_486, RANGE_278, RANGE_166, RANGE_43, RANGE_225, RANGE_496, RANGE_529, RANGE_703, range('ᱚ', 'ᱷ'), RANGE_388, RANGE_500, RANGE_639, range('ℵ', 'ℸ'), RANGE_468, RANGE_704, RANGE_644, RANGE_98, RANGE_266, RANGE_436, RANGE_603, RANGE_46, RANGE_218, RANGE_384, RANGE_471, RANGE_343, RANGE_274, RANGE_508, RANGE_662, RANGE_178, RANGE_666, RANGE_699, range('ꀀ', 'ꀔ'), range('ꀖ', 'ꒌ'), range('ꓐ', 'ꓷ'), range('ꔀ', 'ꘋ'), RANGE_124, RANGE_675, RANGE_326, range('ꟻ', 'ꠁ'), RANGE_591, RANGE_683, RANGE_36, RANGE_455, RANGE_403, RANGE_593, RANGE_379, RANGE_444, RANGE_6, RANGE_57, RANGE_576, range('ꧧ', 'ꧯ'), RANGE_371, RANGE_547, RANGE_437, RANGE_526, range('ꩠ', 'ꩯ'), range('ꩱ', 'ꩶ'), RANGE_327, RANGE_14, RANGE_78, range('ꫛ', 'ꫜ'), RANGE_195, RANGE_186, RANGE_336, RANGE_524, RANGE_120, RANGE_268, range('ꯀ', 'ꯢ'), RANGE_111, RANGE_270, RANGE_132, RANGE_299, RANGE_49, RANGE_83, RANGE_304, RANGE_599, RANGE_63, RANGE_133, RANGE_219, RANGE_394, RANGE_427, RANGE_385, RANGE_173, RANGE_729, RANGE_189, range('ｦ', 'ｯ'), range('ｱ', 'ﾝ'), range('ﾠ', 'ﾾ'), RANGE_658, RANGE_76, RANGE_275, RANGE_418), new Character[]{'ª', 'º', 'ƻ', 'ʔ', 'ە', 'ۿ', 'ܐ', 'ޱ', 'ऽ', 'ॐ', 'ল', 'ঽ', 'ৎ', 'ৼ', 'ਫ਼', 'ઽ', 'ૐ', 'ૹ', 'ଽ', 'ୱ', 'ஃ', 'ஜ', 'ௐ', 'ఽ', 'ಀ', 'ಽ', 'ೞ', 'ഽ', 'ൎ', 'ල', 'ຄ', 'ຊ', 'ຍ', 'ລ', 'ວ', 'ຽ', 'ༀ', 'ဿ', 'ၡ', 'ႎ', 'ቘ', 'ዀ', 'ៜ', 'ᢪ', '〆', '〼', 'ゟ', 'ヿ', 'ꙮ', 'ꞏ', 'ꟷ', 'ꣻ', 'ꣽ', 'ꩺ', 'ꪱ', 'ꫀ', 'ꫂ', 'ꫲ', 'יִ', 'מּ'}),
    MARK(keys("M", "Mark"), "a character intended to be combined with another character (e.g. accents, umlauts, enclosing boxes, etc.).", asList(RANGE_193, range('҃', '҉'), RANGE_354, RANGE_623, RANGE_698, RANGE_129, RANGE_665, RANGE_715, RANGE_177, RANGE_353, RANGE_411, RANGE_457, RANGE_54, RANGE_72, RANGE_261, RANGE_366, RANGE_582, RANGE_661, RANGE_231, RANGE_674, range('ࣣ', 'ः'), range('ऺ', '़'), range('ा', 'ॏ'), RANGE_424, RANGE_56, range('ঁ', 'ঃ'), range('া', 'ৄ'), RANGE_21, range('ো', '্'), RANGE_602, range('ਁ', 'ਃ'), range('ਾ', 'ੂ'), RANGE_578, RANGE_657, RANGE_720, range('ઁ', 'ઃ'), range('ા', 'ૅ'), range('ે', 'ૉ'), range('ો', '્'), RANGE_237, RANGE_18, range('ଁ', 'ଃ'), range('ା', 'ୄ'), RANGE_210, range('ୋ', '୍'), range('ୖ', 'ୗ'), RANGE_55, range('ா', 'ூ'), RANGE_1, range('ொ', '்'), range('ఀ', 'ః'), range('ా', 'ౄ'), RANGE_558, RANGE_638, RANGE_139, RANGE_420, range('ಁ', 'ಃ'), range('ಾ', 'ೄ'), range('ೆ', 'ೈ'), range('ೊ', '್'), RANGE_694, RANGE_238, range('ഀ', 'ഃ'), RANGE_685, range('ാ', 'ൄ'), RANGE_190, range('ൊ', '്'), RANGE_59, RANGE_5, range('ා', 'ු'), RANGE_389, RANGE_216, RANGE_165, RANGE_583, RANGE_719, RANGE_131, RANGE_413, RANGE_663, RANGE_10, range('ཱ', '྄'), RANGE_101, RANGE_248, RANGE_517, range('ါ', 'ှ'), range('ၖ', 'ၙ'), RANGE_340, RANGE_419, RANGE_532, RANGE_7, range('ႂ', 'ႍ'), range('ႚ', 'ႝ'), RANGE_680, RANGE_539, RANGE_492, RANGE_450, RANGE_396, range('឴', '៓'), RANGE_13, RANGE_441, range('ᤠ', 'ᤫ'), range('ᤰ', '᤻'), range('ᨗ', 'ᨛ'), range('ᩕ', 'ᩞ'), range('᩠', '᩼'), range('᪰', '᪾'), range('ᬀ', 'ᬄ'), range('᬴', '᭄'), RANGE_255, range('ᮀ', 'ᮂ'), range('ᮡ', 'ᮭ'), range('᯦', '᯳'), range('ᰤ', '᰷'), RANGE_587, range('᳔', '᳨'), range('ᳲ', '᳴'), range('᳷', '᳹'), RANGE_621, RANGE_412, range('⃐', '⃰'), RANGE_516, RANGE_574, range('〪', '〯'), RANGE_137, range('꙯', '꙲'), RANGE_97, RANGE_235, RANGE_556, range('ꠣ', 'ꠧ'), RANGE_352, range('ꢴ', 'ꣅ'), RANGE_196, RANGE_250, range('ꥇ', '꥓'), range('ꦀ', 'ꦃ'), range('꦳', '꧀'), range('ꨩ', 'ꨶ'), range('ꩌ', 'ꩍ'), range('ꩻ', 'ꩽ'), RANGE_688, RANGE_34, RANGE_194, range('ꫫ', 'ꫯ'), range('ꫵ', '꫶'), range('ꯣ', 'ꯪ'), range('꯬', '꯭'), RANGE_514, RANGE_466), new Character[]{'ֿ', 'ׇ', 'ٰ', 'ܑ', '়', 'ৗ', '਼', 'ੑ', 'ੵ', '઼', '଼', 'ஂ', 'ௗ', '಼', 'ൗ', '්', 'ූ', 'ั', 'ັ', '༵', '༷', '༹', '࿆', 'ႏ', '៝', 'ᢩ', '᩿', '᳭', '⵿', 'ꠂ', '꠆', 'ꠋ', 'ꧥ', 'ꩃ', 'ꪰ', '꫁', 'ﬞ'}),
    NON_SPACING_MARK(keys("Mn", "Non_Spacing_Mark"), "a character intended to be combined with another character without taking up extra space (e.g. accents, umlauts, etc.).", asList(RANGE_193, range('҃', '҇'), RANGE_354, RANGE_623, RANGE_698, RANGE_129, RANGE_665, RANGE_715, RANGE_177, RANGE_353, RANGE_411, RANGE_457, RANGE_54, RANGE_72, RANGE_261, RANGE_366, RANGE_582, RANGE_661, RANGE_231, RANGE_674, range('ࣣ', 'ं'), range('ु', 'ै'), RANGE_424, RANGE_56, range('ু', 'ৄ'), RANGE_602, range('ਁ', 'ਂ'), range('ੁ', 'ੂ'), RANGE_578, RANGE_657, RANGE_720, range('ઁ', 'ં'), range('ુ', 'ૅ'), range('ે', 'ૈ'), RANGE_237, RANGE_18, range('ୁ', 'ୄ'), RANGE_55, range('ా', 'ీ'), RANGE_558, RANGE_638, RANGE_139, RANGE_420, range('ೌ', '್'), RANGE_238, range('ഀ', 'ഁ'), RANGE_685, range('ു', 'ൄ'), RANGE_59, range('ි', 'ු'), RANGE_165, RANGE_583, RANGE_719, RANGE_131, RANGE_413, RANGE_663, range('ཱ', 'ཾ'), range('ྀ', '྄'), RANGE_101, RANGE_248, RANGE_517, range('ိ', 'ူ'), range('ဲ', '့'), range('္', '်'), range('ွ', 'ှ'), range('ၘ', 'ၙ'), RANGE_340, RANGE_7, range('ႅ', 'ႆ'), RANGE_680, RANGE_539, RANGE_492, RANGE_450, RANGE_396, range('឴', '឵'), range('ិ', 'ួ'), range('៉', '៓'), RANGE_13, RANGE_441, range('ᤠ', 'ᤢ'), range('ᤧ', 'ᤨ'), range('᤹', '᤻'), range('ᨗ', 'ᨘ'), range('ᩘ', 'ᩞ'), range('ᩥ', 'ᩬ'), range('ᩳ', '᩼'), range('᪰', '᪽'), range('ᬀ', 'ᬃ'), range('ᬶ', 'ᬺ'), RANGE_255, range('ᮀ', 'ᮁ'), range('ᮢ', 'ᮥ'), range('ᮨ', 'ᮩ'), range('᮫', 'ᮭ'), range('ᯨ', 'ᯩ'), range('ᯯ', 'ᯱ'), range('ᰬ', 'ᰳ'), range('ᰶ', '᰷'), RANGE_587, range('᳔', '᳠'), range('᳢', '᳨'), range('᳸', '᳹'), RANGE_621, RANGE_412, range('⃐', '⃜'), range('⃥', '⃰'), RANGE_516, RANGE_574, range('〪', '〭'), RANGE_137, RANGE_97, RANGE_235, RANGE_556, range('ꠥ', 'ꠦ'), range('꣄', 'ꣅ'), RANGE_196, RANGE_250, range('ꥇ', 'ꥑ'), range('ꦀ', 'ꦂ'), range('ꦶ', 'ꦹ'), range('ꨩ', 'ꨮ'), range('ꨱ', 'ꨲ'), range('ꨵ', 'ꨶ'), RANGE_688, RANGE_34, RANGE_194, range('ꫬ', 'ꫭ'), RANGE_514, RANGE_466), new Character[]{'ֿ', 'ׇ', 'ٰ', 'ܑ', 'ऺ', '़', '्', 'ঁ', '়', '্', '਼', 'ੑ', 'ੵ', '઼', '્', 'ଁ', '଼', 'ି', '୍', 'ୖ', 'ஂ', 'ீ', '்', 'ఀ', 'ಁ', '಼', 'ಿ', 'ೆ', '്', '්', 'ූ', 'ั', 'ັ', '༵', '༷', '༹', '࿆', 'ႂ', 'ႍ', 'ႝ', 'ំ', '៝', 'ᢩ', 'ᤲ', 'ᨛ', 'ᩖ', '᩠', 'ᩢ', '᩿', '᬴', 'ᬼ', 'ᭂ', '᯦', 'ᯭ', '᳭', '᳴', '⃡', '⵿', '꙯', 'ꠂ', '꠆', 'ꠋ', '꦳', 'ꦼ', 'ꧥ', 'ꩃ', 'ꩌ', 'ꩼ', 'ꪰ', '꫁', '꫶', 'ꯥ', 'ꯨ', '꯭', 'ﬞ'}),
    SPACING_COMBINING_MARK(keys("Mc", "Spacing_Combining_Mark"), "a character intended to be combined with another character that takes up extra space (vowel signs in many Eastern languages).", asList(range('ा', 'ी'), range('ॉ', 'ौ'), range('ॎ', 'ॏ'), range('ং', 'ঃ'), range('া', 'ী'), RANGE_21, range('ো', 'ৌ'), range('ਾ', 'ੀ'), range('ા', 'ી'), range('ો', 'ૌ'), range('ଂ', 'ଃ'), RANGE_210, range('ୋ', 'ୌ'), range('ா', 'ி'), range('ு', 'ூ'), RANGE_1, range('ொ', 'ௌ'), range('ఁ', 'ః'), range('ు', 'ౄ'), range('ಂ', 'ಃ'), range('ೀ', 'ೄ'), range('ೇ', 'ೈ'), range('ೊ', 'ೋ'), RANGE_694, range('ം', 'ഃ'), range('ാ', 'ീ'), RANGE_190, range('ൊ', 'ൌ'), RANGE_5, range('ා', 'ෑ'), RANGE_389, RANGE_216, RANGE_10, range('ါ', 'ာ'), range('ျ', 'ြ'), range('ၖ', 'ၗ'), RANGE_419, RANGE_532, range('ႃ', 'ႄ'), range('ႇ', 'ႌ'), range('ႚ', 'ႜ'), range('ើ', 'ៅ'), range('ះ', 'ៈ'), range('ᤣ', 'ᤦ'), range('ᤩ', 'ᤫ'), range('ᤰ', 'ᤱ'), range('ᤳ', 'ᤸ'), range('ᨙ', 'ᨚ'), range('ᩣ', 'ᩤ'), range('ᩭ', 'ᩲ'), range('ᬽ', 'ᭁ'), range('ᭃ', '᭄'), range('ᮦ', 'ᮧ'), range('ᯪ', 'ᯬ'), range('᯲', '᯳'), range('ᰤ', 'ᰫ'), range('ᰴ', 'ᰵ'), range('ᳲ', 'ᳳ'), range('〮', '〯'), range('ꠣ', 'ꠤ'), RANGE_352, range('ꢴ', 'ꣃ'), range('ꥒ', '꥓'), range('ꦴ', 'ꦵ'), range('ꦺ', 'ꦻ'), range('ꦽ', '꧀'), range('ꨯ', 'ꨰ'), range('ꨳ', 'ꨴ'), range('ꫮ', 'ꫯ'), range('ꯣ', 'ꯤ'), range('ꯦ', 'ꯧ'), range('ꯩ', 'ꯪ')), new Character[]{'ः', 'ऻ', 'ৗ', 'ਃ', 'ઃ', 'ૉ', 'ା', 'ୀ', 'ୗ', 'ௗ', 'ಾ', 'ൗ', 'ཿ', 'ေ', 'း', 'ႏ', 'ា', 'ᩕ', 'ᩗ', 'ᩡ', 'ᬄ', 'ᬵ', 'ᬻ', 'ᮂ', 'ᮡ', '᮪', 'ᯧ', 'ᯮ', '᳡', '᳷', 'ꠧ', 'ꦃ', 'ꩍ', 'ꩻ', 'ꩽ', 'ꫫ', 'ꫵ', '꯬'}),
    ENCLOSING_MARK(keys("Me", "Enclosing_Mark"), "a character that encloses the character it is combined with (circle, square, keycap, etc.).", asList(range('҈', '҉'), range('⃝', '⃠'), range('⃢', '⃤'), range('꙰', '꙲')), new Character[]{'᪾'}),
    SEPARATOR(keys("Z", "Separator"), "any kind of whitespace or invisible separator.", asList(INVISIBLE_WHITECHARACTERS, range(' ', ' ')), new Character[]{' ', ' ', ' ', ' ', ' ', '　'}),
    SPACE_SEPARATOR(keys("Zs", "Space_Separator"), "a whitespace character that is invisible, but does take up space.", singletonList(INVISIBLE_WHITECHARACTERS), new Character[]{' ', ' ', ' ', ' ', ' ', '　'}),
    LINE_SEPARATOR(keys("Zl", "Line_Separator"), "line separator character U+2028.", ' '),
    PARAGRAPH_SEPARATOR(keys("Zp", "Paragraph_Separator"), "paragraph separator character U+2029.", ' '),
    SYMBOL(keys("S", "Symbol"), "math symbols, currency signs, dingbats, box-drawing characters, etc.", asList(LT_EQ_GT, range('¢', '¦'), range('¨', '©'), range('®', '±'), RANGE_286, RANGE_626, RANGE_306, RANGE_521, RANGE_53, range('֍', '֏'), RANGE_640, RANGE_86, RANGE_91, RANGE_215, range('৺', '৻'), range('௳', '௺'), RANGE_167, RANGE_598, RANGE_710, RANGE_570, RANGE_22, RANGE_175, RANGE_333, RANGE_244, RANGE_317, RANGE_534, RANGE_35, RANGE_449, RANGE_586, RANGE_150, RANGE_499, RANGE_113, RANGE_452, RANGE_207, RANGE_553, RANGE_301, RANGE_149, RANGE_220, RANGE_329, range('№', '℘'), RANGE_64, RANGE_660, RANGE_61, range('⅊', '⅍'), RANGE_188, range('←', '⌇'), range('⌌', '⌨'), range('⌫', '␦'), RANGE_434, RANGE_234, range('─', '❧'), range('➔', '⟄'), RANGE_32, range('⟰', '⦂'), RANGE_528, RANGE_483, range('⧾', '⭳'), RANGE_503, RANGE_487, RANGE_554, RANGE_95, RANGE_90, RANGE_302, RANGE_350, RANGE_214, RANGE_226, RANGE_185, RANGE_170, RANGE_212, RANGE_378, RANGE_179, RANGE_311, RANGE_440, RANGE_612, RANGE_530, RANGE_693, RANGE_383, RANGE_564, RANGE_356, RANGE_619, RANGE_707, RANGE_143, RANGE_121, RANGE_146, RANGE_624, range('꠶', '꠹'), RANGE_128, RANGE_313, range('﷼', '﷽'), RANGE_477, RANGE_4, range('￠', '￦'), range('￨', '￮'), RANGE_423), new Character[]{'$', '+', '^', '`', '|', '~', '¬', '´', '¸', '×', '÷', '˭', '͵', '϶', '҂', '؋', '۞', '۩', '߶', '૱', '୰', '౿', '൏', '൹', '฿', '༓', '༴', '༶', '༸', '៛', '᥀', '᾽', '⁄', '⁒', '℔', '℥', '℧', '℩', '℮', '⅏', '〄', '〠', '㉐', '꭛', '﬩', '﹢', '﹩', '＄', '＋', '＾', '｀', '｜', '～'}),
    MATH_SYMBOL(keys("Sm", "Math_Symbol"), "any mathematical symbol.", asList(LT_EQ_GT, RANGE_640, RANGE_207, RANGE_553, RANGE_61, range('←', '↔'), range('↚', '↛'), range('⇎', '⇏'), range('⇴', '⋿'), range('⌠', '⌡'), range('⎛', '⎳'), range('⏜', '⏡'), range('◸', '◿'), range('⟀', '⟄'), RANGE_32, range('⟰', '⟿'), range('⤀', '⦂'), RANGE_528, RANGE_483, range('⧾', '⫿'), range('⬰', '⭄'), range('⭇', '⭌'), RANGE_477, RANGE_4, range('￩', '￬')), new Character[]{'+', '|', '~', '¬', '±', '×', '÷', '϶', '⁄', '⁒', '℘', '⅋', '↠', '↣', '↦', '↮', '⇒', '⇔', '⍼', '▷', '◁', '♯', '﬩', '﹢', '＋', '｜', '～', '￢'}),
    CURRENCY_SYMBOL(keys("Sc", "Currency_Symbol"), "any currency sign.", asList(range('¢', '¥'), RANGE_215, RANGE_301, range('￠', '￡'), range('￥', '￦')), new Character[]{'$', '֏', '؋', '৻', '૱', '௹', '฿', '៛', '꠸', '﷼', '﹩', '＄'}),
    MODIFIER_SYMBOL(keys("Sk", "Modifier_Symbol"), "a combining character (mark) as a full character on its own.", asList(RANGE_286, RANGE_626, RANGE_306, RANGE_521, RANGE_53, RANGE_586, RANGE_150, RANGE_499, RANGE_113, RANGE_452, RANGE_179, RANGE_143, RANGE_121, RANGE_146, RANGE_313), new Character[]{'^', '`', '¨', '¯', '´', '¸', '˭', '͵', '᾽', '꭛', '＾', '｀', '￣'}),
    OTHER_SYMBOL(keys("So", "Other_Symbol"), "various symbols that are not math symbols, currency signs, or combining characters.", asList(range('֍', '֎'), RANGE_86, RANGE_91, range('௳', '௸'), RANGE_167, RANGE_598, RANGE_710, RANGE_570, RANGE_22, RANGE_175, RANGE_333, RANGE_244, RANGE_317, RANGE_534, RANGE_35, RANGE_449, RANGE_149, RANGE_220, RANGE_329, range('№', '℗'), RANGE_64, RANGE_660, range('⅌', '⅍'), RANGE_188, range('↕', '↙'), range('↜', '↟'), range('↡', '↢'), range('↤', '↥'), range('↧', '↭'), range('↯', '⇍'), range('⇐', '⇑'), range('⇕', '⇳'), range('⌀', '⌇'), range('⌌', '⌟'), range('⌢', '⌨'), range('⌫', '⍻'), range('⍽', '⎚'), range('⎴', '⏛'), range('⏢', '␦'), RANGE_434, RANGE_234, range('─', '▶'), range('▸', '◀'), range('◂', '◷'), range('☀', '♮'), range('♰', '❧'), range('➔', '➿'), range('⠀', '⣿'), range('⬀', '⬯'), range('⭅', '⭆'), range('⭍', '⭳'), RANGE_503, RANGE_487, RANGE_554, RANGE_95, RANGE_90, RANGE_302, RANGE_350, RANGE_214, RANGE_226, RANGE_185, RANGE_170, RANGE_212, RANGE_378, RANGE_311, RANGE_440, RANGE_612, RANGE_530, RANGE_693, RANGE_383, RANGE_564, RANGE_356, RANGE_619, RANGE_707, RANGE_624, range('꠶', '꠷'), RANGE_128, range('￭', '￮'), RANGE_423), new Character[]{'¦', '©', '®', '°', '҂', '۞', '۩', '߶', '৺', '୰', '௺', '౿', '൏', '൹', '༓', '༴', '༶', '༸', '᥀', '℔', '℥', '℧', '℩', '℮', '⅊', '⅏', '⇓', '〄', '〠', '㉐', '꠹', '﷽', '￤', '￨'}),
    NUMBER(keys("N", "Number"), "any kind of numeric character in any script.", asList(DIGITS, SQUARE_CUBE, FRACTION_DIGITS, RANGE_376, RANGE_545, RANGE_608, RANGE_142, RANGE_695, RANGE_263, RANGE_513, RANGE_331, RANGE_141, RANGE_395, range('௦', '௲'), RANGE_512, RANGE_160, RANGE_332, RANGE_577, range('൦', '൸'), RANGE_697, RANGE_39, RANGE_588, range('༠', '༳'), RANGE_425, RANGE_678, RANGE_213, RANGE_502, RANGE_569, RANGE_182, RANGE_130, RANGE_197, range('᧐', '᧚'), RANGE_341, RANGE_682, RANGE_402, RANGE_273, RANGE_430, RANGE_41, RANGE_75, RANGE_342, range('⅐', 'ↂ'), range('ↅ', '↉'), RANGE_398, RANGE_417, RANGE_494, RANGE_491, RANGE_252, RANGE_360, RANGE_474, RANGE_601, RANGE_67, RANGE_346, RANGE_653, RANGE_461, RANGE_348, RANGE_99, RANGE_581, RANGE_140, RANGE_211, RANGE_163, RANGE_26, RANGE_164, RANGE_495), new Character[]{'¹', '⁰', '⳽', '〇'}),
    DECIMAL_DIGIT_NUMBER(keys("Nd", "Decimal_Digit_Number"), "a digit zero through nine in any script except ideographic scripts.", asList(DIGITS, RANGE_376, RANGE_545, RANGE_608, RANGE_142, RANGE_695, RANGE_513, RANGE_331, RANGE_141, range('௦', '௯'), RANGE_512, RANGE_332, range('൦', '൯'), RANGE_697, RANGE_39, RANGE_588, range('༠', '༩'), RANGE_425, RANGE_678, RANGE_569, RANGE_130, RANGE_197, range('᧐', '᧙'), RANGE_341, RANGE_682, RANGE_402, RANGE_273, RANGE_430, RANGE_41, RANGE_461, RANGE_581, RANGE_140, RANGE_211, RANGE_163, RANGE_26, RANGE_164, RANGE_495), ZERO_LENGTH_CHARACTER_ARRAY),
    LETTER_NUMBER(keys("Nl", "Letter_Number"), "a number that looks like a letter, such as a Roman numeral.", asList(RANGE_502, range('Ⅰ', 'ↂ'), range('ↅ', 'ↈ'), RANGE_491, RANGE_252, RANGE_348), new Character[]{'〇'}),
    OTHER_NUMBER(keys("No", "Other_Number"), "a superscript or subscript digit, or a number that is not a digit 0-9 (excluding numbers from ideographic scripts).", asList(SQUARE_CUBE, FRACTION_DIGITS, RANGE_263, RANGE_395, range('௰', '௲'), RANGE_160, RANGE_577, range('൰', '൸'), range('༪', '༳'), RANGE_213, RANGE_182, RANGE_75, RANGE_342, range('⅐', '⅟'), RANGE_398, RANGE_417, RANGE_494, RANGE_360, RANGE_474, RANGE_601, RANGE_67, RANGE_346, RANGE_653, RANGE_99), new Character[]{'¹', '᧚', '⁰', '↉', '⳽'}),
    PUNCTUATION(keys("P", "Punctuation"), "any kind of punctuation character.", asList(RANGE_493, range('%', '*'), range(',', '/'), RANGE_298, RANGE_399, range('[', ']'), RANGE_23, RANGE_611, range('։', '֊'), RANGE_236, RANGE_711, RANGE_42, RANGE_426, RANGE_594, RANGE_154, RANGE_330, RANGE_89, RANGE_105, RANGE_254, RANGE_239, range('༺', '༽'), RANGE_222, RANGE_410, RANGE_637, RANGE_11, RANGE_651, range('᚛', '᚜'), RANGE_433, RANGE_559, RANGE_310, RANGE_387, range('᠀', '᠊'), RANGE_145, RANGE_429, RANGE_288, RANGE_458, RANGE_609, RANGE_432, RANGE_325, RANGE_290, RANGE_247, range('‐', '‧'), range('‰', '⁃'), range('⁅', '⁑'), range('⁓', '⁞'), range('⁽', '⁾'), range('₍', '₎'), range('⌈', '⌋'), range('〈', '〉'), range('❨', '❵'), range('⟅', '⟆'), range('⟦', '⟯'), range('⦃', '⦘'), range('⧘', '⧛'), range('⧼', '⧽'), RANGE_731, RANGE_114, range('⸀', '⸮'), range('⸰', '⹉'), RANGE_540, range('〈', '】'), range('〔', '〟'), RANGE_102, RANGE_52, RANGE_592, RANGE_92, RANGE_535, RANGE_702, RANGE_391, RANGE_616, RANGE_511, RANGE_282, RANGE_144, RANGE_552, range('﴾', '﴿'), range('︐', '︙'), range('︰', '﹒'), range('﹔', '﹡'), RANGE_584, RANGE_184, range('％', '＊'), range('，', '／'), RANGE_696, RANGE_88, range('［', '］'), range('｟', '･')), new Character[]{'_', '{', '}', '¡', '§', '«', '»', '¿', ';', '·', '־', '׀', '׃', '׆', '؛', '۔', '࡞', '॰', '৽', '૰', '෴', '๏', '༔', '྅', '჻', '᐀', '᳓', '⵰', '〰', '〽', '゠', '・', '꙳', '꙾', '꣼', '꥟', '꯫', '﹣', '﹨', '＿', '｛', '｝'}),
    DASH_PUNCTUATION(keys("Pd", "Dash_Punctuation"), "any kind of hyphen or dash.", asList(range('‐', '―'), range('⸺', '⸻'), range('︱', '︲')), new Character[]{'-', '֊', '־', '᐀', '᠆', '⸗', '⸚', '⹀', '〜', '〰', '゠', '﹘', '﹣', '－'}),
    OPEN_PUNCTUATION(keys("Ps", "Open_Punctuation"), "any kind of opening bracket.", '(', '[', '{', '༺', '༼', '᚛', '‚', '„', '⁅', '⁽', '₍', '⌈', '⌊', '〈', '❨', '❪', '❬', '❮', '❰', '❲', '❴', '⟅', '⟦', '⟨', '⟪', '⟬', '⟮', '⦃', '⦅', '⦇', '⦉', '⦋', '⦍', '⦏', '⦑', '⦓', '⦕', '⦗', '⧘', '⧚', '⧼', '⸢', '⸤', '⸦', '⸨', '⹂', '〈', '《', '「', '『', '【', '〔', '〖', '〘', '〚', '〝', '﴿', '︗', '︵', '︷', '︹', '︻', '︽', '︿', '﹁', '﹃', '﹇', '﹙', '﹛', '﹝', '（', '［', '｛', '｟', '｢'),
    CLOSE_PUNCTUATION(keys("Pe", "Close_Punctuation"), "any kind of closing bracket.", singletonList(range('〞', '〟')), new Character[]{')', ']', '}', '༻', '༽', '᚜', '⁆', '⁾', '₎', '⌉', '⌋', '〉', '❩', '❫', '❭', '❯', '❱', '❳', '❵', '⟆', '⟧', '⟩', '⟫', '⟭', '⟯', '⦄', '⦆', '⦈', '⦊', '⦌', '⦎', '⦐', '⦒', '⦔', '⦖', '⦘', '⧙', '⧛', '⧽', '⸣', '⸥', '⸧', '⸩', '〉', '》', '」', '』', '】', '〕', '〗', '〙', '〛', '﴾', '︘', '︶', '︸', '︺', '︼', '︾', '﹀', '﹂', '﹄', '﹈', '﹚', '﹜', '﹞', '）', '］', '｝', '｠', '｣'}),
    INITIAL_PUNCTUATION(keys("Pi", "Initial_Punctuation"), "any kind of opening quote.", singletonList(range('‛', '“')), new Character[]{'«', '‘', '‟', '‹', '⸂', '⸄', '⸉', '⸌', '⸜', '⸠'}),
    FINAL_PUNCTUATION(keys("Pf", "Final_Punctuation"), "any kind of closing quote.", '»', '’', '”', '›', '⸃', '⸅', '⸊', '⸍', '⸝', '⸡'),
    CONNECTOR_PUNCTUATION(keys("Pc", "Connector_Punctuation"), "a punctuation character such as an underscore that connects words.", asList(range('‿', '⁀'), range('︳', '︴'), range('﹍', '﹏')), new Character[]{'_', '⁔', '＿'}),
    OTHER_PUNCTUATION(keys("Po", "Other_Punctuation"), "any kind of punctuation character that is not a dash, bracket, quote or connector.", asList(RANGE_493, range('%', '\''), range('.', '/'), RANGE_298, RANGE_399, RANGE_23, RANGE_611, RANGE_236, RANGE_711, RANGE_42, RANGE_426, RANGE_594, RANGE_154, RANGE_330, RANGE_89, RANGE_105, RANGE_254, RANGE_239, RANGE_222, RANGE_410, RANGE_637, RANGE_11, RANGE_651, RANGE_433, RANGE_559, RANGE_310, RANGE_387, range('᠀', '᠅'), range('᠇', '᠊'), RANGE_145, RANGE_429, RANGE_288, RANGE_458, RANGE_609, RANGE_432, RANGE_325, RANGE_290, RANGE_247, range('‖', '‗'), range('†', '‧'), range('‰', '‸'), range('※', '‾'), range('⁁', '⁃'), range('⁇', '⁑'), range('⁕', '⁞'), RANGE_731, RANGE_114, range('⸀', '⸁'), range('⸆', '⸈'), range('⸎', '⸖'), range('⸘', '⸙'), range('⸞', '⸟'), range('⸪', '⸮'), range('⸰', '⸹'), range('⸼', '⸿'), range('⹃', '⹉'), RANGE_540, RANGE_102, RANGE_52, RANGE_592, RANGE_92, RANGE_535, RANGE_702, RANGE_391, RANGE_616, RANGE_511, RANGE_282, RANGE_144, RANGE_552, range('︐', '︖'), range('﹅', '﹆'), range('﹉', '﹌'), range('﹐', '﹒'), range('﹔', '﹗'), range('﹟', '﹡'), RANGE_584, RANGE_184, range('％', '＇'), range('．', '／'), RANGE_696, RANGE_88, range('､', '･')), new Character[]{'*', ',', '\\', '¡', '§', '¿', ';', '·', '։', '׀', '׃', '׆', '؛', '۔', '࡞', '॰', '৽', '૰', '෴', '๏', '༔', '྅', '჻', '᳓', '⁓', '⵰', '⸋', '⸛', '⹁', '〽', '・', '꙳', '꙾', '꣼', '꥟', '꯫', '︙', '︰', '﹨', '＊', '，', '＼', '｡'}),
    CONTROL(keys("Cc", "Control"), "an ASCII or Latin-1 control character", C1_CONTROLS),
    FORMAT(keys("Cf", "Format"), "invisible formatting indicator.", asList(range('؀', '؅'), RANGE_19, RANGE_686, range('⁠', '⁤'), range('⁦', '⁯'), range('￹', '￻')), new Character[]{'­', '؜', '۝', '܏', '࣢', '᠎', '﻿'}),
    PRIVATE_USE(keys("Co", "Private_Use"), "any code point reserved for private use.", range('', '')),
    IN_BASIC_LATIN(keys("InBasic_Latin", "Latin"), "32-U+007F", range(ConstantsProvider.SPACE_ASCII_CODE, 0x007F)),
    IN_IPA_EXTENSIONS(keys("InIPA_Extensions"), "U+0250-U+02AF", range(0x0250, 0x02AF)),
    IN_SPACING_MODIFIER_LETTERS(keys("InSpacing_Modifier_Letters"), "U+02B0-U+02FF", range(0x02B0, 0x02FF)),
    IN_COMBINING_DIACRITICAL_MARKS(keys("InCombining_Diacritical_Marks"), "U+0300-U+036F", range(0x0300, 0x036F)),

    IN_CYRILLIC(keys("InCyrillic", "Cyrillic"), "U+0400-U+04FF", range(0x0400, 0x04FF)),
    IN_CYRILLIC_SUPPLEMENTARY(keys("InCyrillic_Supplementary"), "U+0500-U+052F", range(0x0500, 0x052F)),
    IN_ARMENIAN(keys("InArmenian", "Armenian"), "U+0530-U+058F", range(0x0530, 0x058F)),
    IN_HEBREW(keys("InHebrew", "Hebrew"), "U+0590-U+05FF", range(0x0590, 0x05FF)),
    IN_ARABIC(keys("InArabic", "Arabic"), "U+0600-U+06FF", range(0x0600, 0x06FF)),
    IN_SYRIAC(keys("InSyriac", "Syriac"), "U+0700-U+074F", range(0x0700, 0x074F)),
    IN_THAANA(keys("InThaana", "Thaana"), "U+0780-U+07BF", range(0x0780, 0x07BF)),
    IN_DEVANAGARI(keys("InDevanagari", "Devanagari"), "U+0900-U+097F", range(0x0900, 0x097F)),
    IN_BENGALI(keys("InBengali", "Bengali"), "U+0980-U+09FF", range(0x0980, 0x09FF)),
    IN_GURMUKHI(keys("InGurmukhi", "Gurmukhi"), "U+0A00-U+0A7F", range(0x0A00, 0x0A7F)),
    IN_GUJARATI(keys("InGujarati", "Gujarati"), "U+0A80-U+0AFF", range(0x0A80, 0x0AFF)),
    IN_ORIYA(keys("InOriya", "Oriya"), "U+0B00-U+0B7F", range(0x0B00, 0x0B7F)),
    IN_TAMIL(keys("InTamil", "Tamil"), "U+0B80-U+0BFF", range(0x0B80, 0x0BFF)),
    IN_TELUGU(keys("InTelugu", "Telugu"), "U+0C00-U+0C7F", range(0x0C00, 0x0C7F)),
    IN_KANNADA(keys("InKannada", "Kannada"), "U+0C80-U+0CFF", range(0x0C80, 0x0CFF)),
    IN_MALAYALAM(keys("InMalayalam", "Malayalam"), "U+0D00-U+0D7F", range(0x0D00, 0x0D7F)),
    IN_SINHALA(keys("InSinhala", "Sinhala"), "U+0D80-U+0DFF", range(0x0D80, 0x0DFF)),
    IN_THAI(keys("InThai", "Thai"), "U+0E00-U+0E7F", range(0x0E00, 0x0E7F)),
    IN_LAO(keys("InLao", "Lao"), "U+0E80-U+0EFF", range(0x0E80, 0x0EFF)),
    IN_TIBETAN(keys("InTibetan", "Tibetan"), "U+0F00-U+0FFF", range(0x0F00, 0x0FFF)),
    IN_MYANMAR(keys("InMyanmar", "Myanmar"), "U+1000-U+109F", range(0x1000, 0x109F)),
    IN_GEORGIAN(keys("InGeorgian", "Georgian"), "U+10A0-U+10FF", range(0x10A0, 0x10FF)),
    IN_HANGUL_JAMO(keys("InHangul_Jamo", "Hangul"), "U+1100-U+11FF", range(0x1100, 0x11FF)),
    IN_ETHIOPIC(keys("InEthiopic", "Ethiopic"), "U+1200-U+137F", range(0x1200, 0x137F)),
    IN_CHEROKEE(keys("InCherokee", "Cherokee"), "U+13A0-U+13FF", range(0x13A0, 0x13FF)),
    IN_UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS(keys("InUnified_Canadian_Aboriginal_Syllabics", "Canadian_Aboriginal"), "U+1400-U+167F", range(0x1400, 0x167F)),
    IN_OGHAM(keys("InOgham", "Ogham"), "U+1680-U+169F", range(0x1680, 0x169F)),
    IN_RUNIC(keys("InRunic", "Runic"), "U+16A0-U+16FF", range(0x16A0, 0x16FF)),
    IN_TAGALOG(keys("InTagalog", "Tagalog"), "U+1700-U+171F", range(0x1700, 0x171F)),
    IN_HANUNOO(keys("InHanunoo", "Hanunoo"), "U+1720-U+173F", range(0x1720, 0x173F)),
    IN_BUHID(keys("InBuhid", "Buhid"), "U+1740-U+175F", range(0x1740, 0x175F)),
    IN_TAGBANWA(keys("InTagbanwa", "Tagbanwa"), "U+1760-U+177F", range(0x1760, 0x177F)),
    IN_KHMER(keys("InKhmer", "Khmer"), "U+1780-U+17FF", range(0x1780, 0x17FF)),
    IN_MONGOLIAN(keys("InMongolian", "Mongolian"), "U+1800-U+18AF", range(0x1800, 0x18AF)),
    IN_LIMBU(keys("InLimbu", "Limbu"), "U+1900-U+194F", range(0x1900, 0x194F)),
    IN_TAI_LE(keys("InTai_Le"), "U+1950-U+197F", range(0x1950, 0x197F)),
    IN_KHMER_SYMBOLS(keys("InKhmer_Symbols"), "U+19E0-U+19FF", range(0x19E0, 0x19FF)),
    IN_PHONETIC_EXTENSIONS(keys("InPhonetic_Extensions"), "U+1D00-U+1D7F", range(0x1D00, 0x1D7F)),
    IN_LATIN_EXTENDED_ADDITIONAL(keys("InLatin_Extended_Additional"), "U+1E00-U+1EFF", range(0x1E00, 0x1EFF)),
    IN_GREEK_EXTENDED(keys("InGreek_Extended", "Greek"), "U+1F00-U+1FFF", range(0x1F00, 0x1FFF)),
    IN_GENERAL_PUNCTUATION(keys("InGeneral_Punctuation"), "U+2000-U+206F", range(0x2000, 0x206F)),
    IN_SUPERSCRIPTS_AND_SUBSCRIPTS(keys("InSuperscripts_and_Subscripts"), "U+2070-U+209F", range(0x2070, 0x209F)),
    IN_CURRENCY_SYMBOLS(keys("InCurrency_Symbols"), "U+20A0-U+20CF", range(0x20A0, 0x20CF)),
    IN_LETTERLIKE_SYMBOLS(keys("InLetterlike_Symbols"), "U+2100-U+214F", range(0x2100, 0x214F)),
    IN_NUMBER_FORMS(keys("InNumber_Forms"), "U+2150-U+218F", range(0x2150, 0x218F)),
    IN_ARROWS(keys("InArrows"), "U+2190-U+21FF", range(0x2190, 0x21FF)),
    IN_MATHEMATICAL_OPERATORS(keys("InMathematical_Operators"), "U+2200-U+22FF", range(0x2200, 0x22FF)),
    IN_MISCELLANEOUS_TECHNICAL(keys("InMiscellaneous_Technical"), "U+2300-U+23FF", range(0x2300, 0x23FF)),
    IN_CONTROL_PICTURES(keys("InControl_Pictures"), "U+2400-U+243F", range(0x2400, 0x243F)),
    IN_OPTICAL_CHARACTER_RECOGNITION(keys("InOptical_Character_Recognition"), "U+2440-U+245F", range(0x2440, 0x245F)),
    IN_ENCLOSED_ALPHANUMERICS(keys("InEnclosed_Alphanumerics"), "U+2460-U+24FF", range(0x2460, 0x24FF)),
    IN_BOX_DRAWING(keys("InBox_Drawing"), "U+2500-U+257F", range(0x2500, 0x257F)),
    IN_BLOCK_ELEMENTS(keys("InBlock_Elements"), "U+2580-U+259F", range(0x2580, 0x259F)),
    IN_GEOMETRIC_SHAPES(keys("InGeometric_Shapes"), "U+25A0-U+25FF", range(0x25A0, 0x25FF)),
    IN_MISCELLANEOUS_SYMBOLS(keys("InMiscellaneous_Symbols"), "U+2600-U+26FF", range(0x2600, 0x26FF)),
    IN_DINGBATS(keys("InDingbats"), "U+2700-U+27BF", range(0x2700, 0x27BF)),
    IN_BRAILLE_PATTERNS(keys("InBraille_Patterns", "Braille"), "U+2800-U+28FF", range(0x2800, 0x28FF)),
    IN_SUPPLEMENTAL_MATHEMATICAL_OPERATORS(keys("InSupplemental_Mathematical_Operators"), "U+2A00-U+2AFF", range(0x2A00, 0x2AFF)),
    IN_MISCELLANEOUS_SYMBOLS_AND_ARROWS(keys("InMiscellaneous_Symbols_and_Arrows"), "U+2B00-U+2BFF", range(0x2B00, 0x2BFF)),
    IN_CJK_RADICALS_SUPPLEMENT(keys("InCJK_Radicals_Supplement"), "U+2E80-U+2EFF", range(0x2E80, 0x2EFF)),
    IN_KANGXI_RADICALS(keys("InKangxi_Radicals"), "U+2F00-U+2FDF", range(0x2F00, 0x2FDF)),
    IN_IDEOGRAPHIC_DESCRIPTION_CHARACTERS(keys("InIdeographic_Description_Characters"), "U+2FF0-U+2FFF", range(0x2FF0, 0x2FFF)),
    IN_CJK_SYMBOLS_AND_PUNCTUATION(keys("InCJK_Symbols_and_Punctuation"), "U+3000-U+303F", range(0x3000, 0x303F)),
    IN_HIRAGANA(keys("InHiragana", "Hiragana"), "U+3040-U+309F", range(0x3040, 0x309F)),
    IN_KATAKANA(keys("InKatakana", "Katakana"), "U+30A0-U+30FF", range(0x30A0, 0x30FF)),
    IN_BOPOMOFO(keys("InBopomofo", "Bopomofo"), "U+3100-U+312F", range(0x3100, 0x312F)),
    IN_HANGUL_COMPATIBILITY_JAMO(keys("InHangul_Compatibility_Jamo"), "U+3130-U+318F", range(0x3130, 0x318F)),
    IN_KANBUN(keys("InKanbun"), "U+3190-U+319F", range(0x3190, 0x319F)),
    IN_BOPOMOFO_EXTENDED(keys("InBopomofo_Extended"), "U+31A0-U+31BF", range(0x31A0, 0x31BF)),
    IN_KATAKANA_PHONETIC_EXTENSIONS(keys("InKatakana_Phonetic_Extensions"), "U+31F0-U+31FF", range(0x31F0, 0x31FF)),
    IN_ENCLOSED_CJK_LETTERS_AND_MONTHS(keys("InEnclosed_CJK_Letters_and_Months"), "U+3200-U+32FF", range(0x3200, 0x32FF)),
    IN_CJK_COMPATIBILITY(keys("InCJK_Compatibility"), "U+3300-U+33FF", range(0x3300, 0x33FF)),
    IN_CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A(keys("InCJK_Unified_Ideographs_Extension_A"), "U+3400-U+4DBF", range(0x3400, 0x4DBF)),
    IN_YIJING_HEXAGRAM_SYMBOLS(keys("InYijing_Hexagram_Symbols", "Yi"), "U+4DC0-U+4DFF", range(0x4DC0, 0x4DFF)),
    IN_CJK_UNIFIED_IDEOGRAPHS(keys("InCJK_Unified_Ideographs"), "U+4E00-U+9FFF", range(0x4E00, 0x9FFF)),
    IN_YI_SYLLABLES(keys("InYi_Syllables"), "U+A000-U+A48F", range(0xA000, 0xA48F)),
    IN_YI_RADICALS(keys("InYi_Radicals"), "U+A490-U+A4CF", range(0xA490, 0xA4CF)),
    IN_HANGUL_SYLLABLES(keys("InHangul_Syllables"), "U+AC00-U+D7AF", range(0xAC00, 0xD7AF)),
    IN_PRIVATE_USE_AREA(keys("InPrivate_Use_Area"), "U+E000-U+F8FF", range(0xE000, 0xF8FF)),
    IN_CJK_COMPATIBILITY_IDEOGRAPHS(keys("InCJK_Compatibility_Ideographs"), "U+F900-U+FAFF", range(0xF900, 0xFAFF)),
    IN_ALPHABETIC_PRESENTATION_FORMS(keys("InAlphabetic_Presentation_Forms"), "U+FB00-U+FB4F", range(0xFB00, 0xFB4F)),
    IN_VARIATION_SELECTORS(keys("InVariation_Selectors"), "U+FE00-U+FE0F", range(0xFE00, 0xFE0F)),
    IN_COMBINING_HALF_MARKS(keys("InCombining_Half_Marks"), "U+FE20-U+FE2F", range(0xFE20, 0xFE2F)),
    IN_CJK_COMPATIBILITY_FORMS(keys("InCJK_Compatibility_Forms"), "U+FE30-U+FE4F", range(0xFE30, 0xFE4F)),
    IN_SMALL_FORM_VARIANTS(keys("InSmall_Form_Variants"), "U+FE50-U+FE6F", range(0xFE50, 0xFE6F)),
    IN_HALFWIDTH_AND_FULLWIDTH_FORMS(keys("InHalfwidth_and_Fullwidth_Forms"), "U+FF00-U+FFEF", range(0xFF00, 0xFFEF)),
    IN_SPECIALS(keys("InSpecials"), "U+FFF0-U+FFFF", range(0xFFF0, 0xFFFF)),

    // =============================================================================================
    // Below this line categories are disabled because I'm having troubles generating these values.
    // Please open a feature ticket if these are needed - then I'll invest time in those.

    //IN_LATIN_1_SUPPLEMENT(keys("InLatin-1_Supplement"), "U+0080-U+00FF", range(0x0080, 0x00FF)),
    //IN_LATIN_EXTENDED_A(keys("InLatin_Extended-A"), "U+0100-U+017F", range(0x0100, 0x017F)),
    //IN_LATIN_EXTENDED_B(keys("InLatin_Extended-B"), "U+0180-U+024F", range(0x0180, 0x024F)),
    //IN_GREEK_AND_COPTIC(keys("InGreek_and_Coptic"), "U+0370-U+03FF", range(0x0370, 0x03FF)),

    //CASED_LETTER(keys("L&", "Cased_Letter"), "a letter that exists in lowercase and uppercase variants (combination of Ll, Lu and Lt).", asList(LATIN_UPPERCASE, LATIN_LOWERCASE, range('À', 'Ö'), range('Ø', 'ö'), range('ø', 'İ'), range('Ĳ', 'ķ'), range('Ĺ', 'ň'), range('Ŋ', 'ž'), range('ƀ', 'ƌ'), range('Ǝ', 'ƚ'), range('Ɯ', 'Ʃ'), range('Ƭ', 'ƹ'), range('Ƽ', 'ƽ'), range('ǆ', 'Ǉ'), range('ǉ', 'Ǌ'), range('ǌ', 'ǯ'), range('ǳ', 'Ƞ'), range('Ȣ', 'ȳ'), range('Ⱥ', 'ɔ'), range('ɖ', 'ɗ'), range('ɛ', 'ɜ'), range('ɠ', 'ɡ'), range('ɥ', 'ɦ'), range('ɨ', 'ɬ'), range('ɱ', 'ɲ'), range('ʇ', 'ʌ'), range('ʝ', 'ʞ'), range('Ͱ', 'ͳ'), range('Ͷ', 'ͷ'), range('ͻ', 'ͽ'), range('Έ', 'Ί'), range('Ύ', 'Ώ'), range('Α', 'Ρ'), range('Σ', 'ί'), range('α', 'ρ'), range('σ', 'Ϗ'), range('ϗ', 'ϯ'), range('ϲ', 'ϴ'), range('Ϸ', 'ϻ'), range('Ͻ', 'ҁ'), range('Ҋ', 'ԯ'), range('Ա', 'Ֆ'), range('ա', 'ֆ'), range('Ⴀ', 'Ⴥ'), range('Ꭰ', 'Ᏽ'), range('ᏸ', 'ᏽ'), range('Ḁ', 'ẕ'), range('Ạ', 'ἕ'), range('Ἐ', 'Ἕ'), range('ἠ', 'ὅ'), range('Ὀ', 'Ὅ'), range('Ὗ', 'ώ'), range('ᾰ', 'ᾱ'), range('Ᾰ', 'Ά'), range('Ὲ', 'Ή'), range('ῐ', 'ῑ'), range('Ῐ', 'Ί'), range('ῠ', 'ῡ'), range('Ῠ', 'Ῥ'), range('Ὸ', 'Ώ'), range('K', 'Å'), range('Ↄ', 'ↄ'), range('Ⰰ', 'Ⱞ'), range('ⰰ', 'ⱞ'), range('Ⱡ', 'Ɒ'), range('Ⱳ', 'ⱳ'), range('Ⱶ', 'ⱶ'), range('Ȿ', 'ⳣ'), range('Ⳬ', 'ⳮ'), range('Ⳳ', 'ⳳ'), range('ⴀ', 'ⴥ'), range('Ꙁ', 'ꙭ'), range('Ꚁ', 'ꚛ'), range('Ꜣ', 'ꜯ'), range('Ꜳ', 'ꝯ'), range('Ꝺ', 'ꞇ'), range('Ꞌ', 'Ɥ'), range('Ꞑ', 'ꞓ'), range('Ꞗ', 'Ɪ'), range('Ʞ', 'ꞷ'), range('ꭰ', 'ꮿ'), range('Ａ', 'Ｚ'), range('ａ', 'ｚ')), new Character[]{'ƿ', 'Ǆ', 'Ǳ', 'ə', 'ɣ', 'ɯ', 'ɵ', 'ɽ', 'ʀ', 'ʃ', 'ʒ', 'Ϳ', 'Ά', 'Ό', 'Ⴧ', 'Ⴭ', 'ᵹ', 'ᵽ', 'ẞ', 'ὑ', 'ὓ', 'ὕ', 'ὗ', 'Ὑ', 'Ὓ', 'Ὕ', 'ῥ', 'Ω', 'Ⅎ', 'ⅎ', 'ⴧ', 'ⴭ', 'ꭓ'}),

    //IN_COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS(keys("InCombining_Diacritical_Marks_for_Symbols"), "U+20D0-U+20FF", range(0x20D0, 0x20FF)),

    //IN_SUPPLEMENTAL_ARROWS_B(keys("InSupplemental_Arrows-B"), "U+2900-U+297F", range(0x2900, 0x297F)),
    //IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B(keys("InMiscellaneous_Mathematical_Symbols-B"), "U+2980-U+29FF", range(0x2980, 0x29FF)),

    //IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A(keys("InMiscellaneous_Mathematical_Symbols-A"), "U+27C0-U+27EF", range(0x27C0, 0x27EF)),
    //IN_SUPPLEMENTAL_ARROWS_A(keys("InSupplemental_Arrows-A"), "U+27F0-U+27FF", range(0x27F0, 0x27FF)),

    //IN_ARABIC_PRESENTATION_FORMS_A(keys("InArabic_Presentation_Forms-A"), "U+FB50-U+FDFF", range(0xFB50, 0xFDFF)),
    //IN_ARABIC_PRESENTATION_FORMS_B(keys("InArabic_Presentation_Forms-B"), "U+FE70-U+FEFF", range(0xFE70, 0xFEFF)),
//    IN_HIGH_SURROGATES(keys("InHigh_Surrogates"), "U+D800-U+DB7F", range(0xD800, 0xDB7F)),
//    IN_HIGH_PRIVATE_USE_SURROGATES(keys("InHigh_Private_Use_Surrogates"), "U+DB80-U+DBFF", range(0xDB80, 0xDBFF))
//    IN_LOW_SURROGATES(keys("InLow_Surrogates"), "U+DC00-U+DFFF", range(0xDC00, 0xDFFF)),

    // OTHER(keys("C", "Other"), "invisible control characters and unused code points.", asList(C1_CONTROLS, UNUSED_CODEPOINT_1, UNUSED_CODEPOINTS_2, UNUSED_CODEPOINTS_3, UNUSED_CODEPOINTS_4, UNUSED_CODEPOINTS_5, UNUSED_CODEPOINTS_6, range('׵', '؅'), range('؜', '؝'), range('܎', '܏'), UNUSED_CODEPOINTS_7, UNUSED_CODEPOINTS_8, UNUSED_CODEPOINTS_9, UNUSED_CODEPOINTS_10, UNUSED_CODEPOINTS_11, UNUSED_CODEPOINTS_12, UNUSED_CODEPOINTS_13, UNUSED_CODEPOINTS_14, UNUSED_CODEPOINTS_15, UNUSED_CODEPOINTS_16, UNUSED_CODEPOINTS_17, UNUSED_CODEPOINTS_18, UNUSED_CODEPOINTS_19, UNUSED_CODEPOINTS_20, UNUSED_CODEPOINTS_21, UNUSED_CODEPOINTS_22, RANGE_470, RANGE_16, RANGE_147, RANGE_296, RANGE_490, RANGE_610, RANGE_724, RANGE_80, RANGE_361, RANGE_122, RANGE_117, RANGE_542, RANGE_607, RANGE_280, RANGE_585, RANGE_428, RANGE_515, RANGE_659, RANGE_161, RANGE_256, RANGE_364, RANGE_579, RANGE_104, RANGE_531, RANGE_204, RANGE_439, RANGE_645, RANGE_25, RANGE_156, RANGE_479, RANGE_668, RANGE_174, RANGE_241, RANGE_390, RANGE_408, RANGE_297, RANGE_726, RANGE_276, RANGE_462, RANGE_725, RANGE_118, RANGE_546, RANGE_0, RANGE_281, RANGE_604, RANGE_400, RANGE_106, RANGE_700, RANGE_460, RANGE_567, RANGE_20, RANGE_119, RANGE_565, RANGE_169, RANGE_285, RANGE_322, RANGE_309, RANGE_442, RANGE_573, RANGE_632, RANGE_459, RANGE_202, RANGE_544, RANGE_70, RANGE_209, RANGE_292, RANGE_465, RANGE_414, RANGE_543, RANGE_728, RANGE_344, RANGE_633, RANGE_28, RANGE_369, RANGE_615, RANGE_634, RANGE_630, RANGE_523, RANGE_303, RANGE_472, RANGE_224, RANGE_732, RANGE_600, RANGE_595, RANGE_489, RANGE_448, RANGE_519, RANGE_45, RANGE_382, range('᠎', '᠏'), RANGE_349, RANGE_162, RANGE_525, RANGE_669, RANGE_363, RANGE_706, RANGE_79, RANGE_316, RANGE_463, RANGE_183, RANGE_94, RANGE_454, RANGE_380, RANGE_271, RANGE_550, RANGE_157, RANGE_590, RANGE_242, RANGE_320, RANGE_628, RANGE_267, RANGE_260, RANGE_636, RANGE_548, RANGE_416, RANGE_15, RANGE_614, RANGE_66, RANGE_192, RANGE_362, RANGE_652, RANGE_308, RANGE_172, RANGE_19, RANGE_686, range('⁠', '⁯'), RANGE_33, RANGE_223, RANGE_249, RANGE_568, RANGE_228, RANGE_625, RANGE_667, RANGE_438, RANGE_443, RANGE_480, RANGE_293, RANGE_176, RANGE_617, RANGE_279, RANGE_405, RANGE_187, RANGE_377, RANGE_464, RANGE_655, RANGE_627, RANGE_355, RANGE_431, RANGE_96, RANGE_148, RANGE_422, RANGE_510, RANGE_649, RANGE_397, RANGE_87, RANGE_597, RANGE_401, RANGE_721, RANGE_730, RANGE_447, RANGE_717, RANGE_284, RANGE_181, RANGE_381, RANGE_58, RANGE_103, RANGE_505, RANGE_613, RANGE_421, RANGE_245, RANGE_714, RANGE_240, RANGE_307, RANGE_709, RANGE_287, RANGE_478, RANGE_656, RANGE_152, RANGE_123, RANGE_373, RANGE_12, RANGE_44, range('퟼', ''), RANGE_676, RANGE_93, RANGE_321, RANGE_641, RANGE_646, RANGE_60, RANGE_315, RANGE_62, RANGE_467, RANGE_334, RANGE_622, range('﻽', '＀'), RANGE_589, RANGE_27, RANGE_227, RANGE_368, RANGE_497, range('￯', '￻')), new Character[]{'­', '΋', '΍', '΢', '԰', 'ՠ', 'ֈ', '֐', '۝', '࠿', '࡟', 'ࢵ', '࣢', '঄', '঩', '঱', '৞', '਄', '਩', '਱', '਴', '਷', '਽', '੝', '઄', '઎', '઒', '઩', '઱', '઴', '૆', '૊', '଀', '଄', '଩', '଱', '଴', '୞', '஄', '஑', '஛', '஝', '௉', 'ఄ', '఍', '఑', '఩', '౅', '౉', '౗', '಄', '಍', '಑', '಩', '಴', '೅', '೉', '೟', '೰', 'ഄ', '഍', '഑', '൅', '൉', '඄', '඲', '඼', '෕', '෗', '຃', 'ຉ', 'ຘ', 'ຠ', '຤', '຦', 'ຬ', '຺', '໅', '໇', '཈', '྘', '྽', '࿍', '჆', '቉', '቗', '቙', '኉', '኱', '኿', '዁', '዗', '጑', 'ᜍ', '᝭', '᝱', '᤟', '᩟', '᷺', '὘', '὚', '὜', '὞', '᾵', '῅', '῜', '῵', '῿', '₏', '⯉', 'Ⱟ', 'ⱟ', '⴦', '⶧', '⶯', '⶷', '⶿', '⷇', '⷏', '⷗', '⷟', '⺚', '぀', '㆏', '㈟', 'ꞯ', '꧎', '꧿', '꬧', '꬯', '﬷', '﬽', '﬿', '﭂', '﭅', '﹓', '﹧', '﹵', '￧', '￾'}),
    // UNASSIGNED(keys("Cn", "Unassigned"), "any code point to which no character has been assigned.", asList(UNUSED_CODEPOINT_1, UNUSED_CODEPOINTS_2, UNUSED_CODEPOINTS_3, UNUSED_CODEPOINTS_4, UNUSED_CODEPOINTS_5, UNUSED_CODEPOINTS_6, range('׵', '׿'), UNUSED_CODEPOINTS_7, UNUSED_CODEPOINTS_8, UNUSED_CODEPOINTS_9, UNUSED_CODEPOINTS_10, UNUSED_CODEPOINTS_11, UNUSED_CODEPOINTS_12, UNUSED_CODEPOINTS_13, UNUSED_CODEPOINTS_14, UNUSED_CODEPOINTS_15, UNUSED_CODEPOINTS_16, UNUSED_CODEPOINTS_17, UNUSED_CODEPOINTS_18, UNUSED_CODEPOINTS_19, UNUSED_CODEPOINTS_20, UNUSED_CODEPOINTS_21, UNUSED_CODEPOINTS_22, RANGE_470, RANGE_16, RANGE_147, RANGE_296, RANGE_490, RANGE_610, RANGE_724, RANGE_80, RANGE_361, RANGE_122, RANGE_117, RANGE_542, RANGE_607, RANGE_280, RANGE_585, RANGE_428, RANGE_515, RANGE_659, RANGE_161, RANGE_256, RANGE_364, RANGE_579, RANGE_104, RANGE_531, RANGE_204, RANGE_439, RANGE_645, RANGE_25, RANGE_156, RANGE_479, RANGE_668, RANGE_174, RANGE_241, RANGE_390, RANGE_408, RANGE_297, RANGE_726, RANGE_276, RANGE_462, RANGE_725, RANGE_118, RANGE_546, RANGE_0, RANGE_281, RANGE_604, RANGE_400, RANGE_106, RANGE_700, RANGE_460, RANGE_567, RANGE_20, RANGE_119, RANGE_565, RANGE_169, RANGE_285, RANGE_322, RANGE_309, RANGE_442, RANGE_573, RANGE_632, RANGE_459, RANGE_202, RANGE_544, RANGE_70, RANGE_209, RANGE_292, RANGE_465, RANGE_414, RANGE_543, RANGE_728, RANGE_344, RANGE_633, RANGE_28, RANGE_369, RANGE_615, RANGE_634, RANGE_630, RANGE_523, RANGE_303, RANGE_472, RANGE_224, RANGE_732, RANGE_600, RANGE_595, RANGE_489, RANGE_448, RANGE_519, RANGE_45, RANGE_382, RANGE_349, RANGE_162, RANGE_525, RANGE_669, RANGE_363, RANGE_706, RANGE_79, RANGE_316, RANGE_463, RANGE_183, RANGE_94, RANGE_454, RANGE_380, RANGE_271, RANGE_550, RANGE_157, RANGE_590, RANGE_242, RANGE_320, RANGE_628, RANGE_267, RANGE_260, RANGE_636, RANGE_548, RANGE_416, RANGE_15, RANGE_614, RANGE_66, RANGE_192, RANGE_362, RANGE_652, RANGE_308, RANGE_172, RANGE_33, RANGE_223, RANGE_249, RANGE_568, RANGE_228, RANGE_625, RANGE_667, RANGE_438, RANGE_443, RANGE_480, RANGE_293, RANGE_176, RANGE_617, RANGE_279, RANGE_405, RANGE_187, RANGE_377, RANGE_464, RANGE_655, RANGE_627, RANGE_355, RANGE_431, RANGE_96, RANGE_148, RANGE_422, RANGE_510, RANGE_649, RANGE_397, RANGE_87, RANGE_597, RANGE_401, RANGE_721, RANGE_730, RANGE_447, RANGE_717, RANGE_284, RANGE_181, RANGE_381, RANGE_58, RANGE_103, RANGE_505, RANGE_613, RANGE_421, RANGE_245, RANGE_714, RANGE_240, RANGE_307, RANGE_709, RANGE_287, RANGE_478, RANGE_656, RANGE_152, RANGE_123, RANGE_373, RANGE_12, RANGE_44, range('퟼', '퟿'), RANGE_676, RANGE_93, RANGE_321, RANGE_641, RANGE_646, RANGE_60, RANGE_315, RANGE_62, RANGE_467, RANGE_334, RANGE_622, range('﻽', '﻾'), RANGE_589, RANGE_27, RANGE_227, RANGE_368, RANGE_497, range('￯', '￸')), new Character[]{'΋', '΍', '΢', '԰', 'ՠ', 'ֈ', '֐', '؝', '܎', '࠿', '࡟', 'ࢵ', '঄', '঩', '঱', '৞', '਄', '਩', '਱', '਴', '਷', '਽', '੝', '઄', '઎', '઒', '઩', '઱', '઴', '૆', '૊', '଀', '଄', '଩', '଱', '଴', '୞', '஄', '஑', '஛', '஝', '௉', 'ఄ', '఍', '఑', '఩', '౅', '౉', '౗', '಄', '಍', '಑', '಩', '಴', '೅', '೉', '೟', '೰', 'ഄ', '഍', '഑', '൅', '൉', '඄', '඲', '඼', '෕', '෗', '຃', 'ຉ', 'ຘ', 'ຠ', '຤', '຦', 'ຬ', '຺', '໅', '໇', '཈', '྘', '྽', '࿍', '჆', '቉', '቗', '቙', '኉', '኱', '኿', '዁', '዗', '጑', 'ᜍ', '᝭', '᝱', '᠏', '᤟', '᩟', '᷺', '὘', '὚', '὜', '὞', '᾵', '῅', '῜', '῵', '῿', '⁥', '₏', '⯉', 'Ⱟ', 'ⱟ', '⴦', '⶧', '⶯', '⶷', '⶿', '⷇', '⷏', '⷗', '⷟', '⺚', '぀', '㆏', '㈟', 'ꞯ', '꧎', '꧿', '꬧', '꬯', '﬷', '﬽', '﬿', '﭂', '﭅', '﹓', '﹧', '﹵', '＀', '￧', '￾'}),

    // ===========================================================================================
    // Help needed - unable to find exact list of codepoints in COMMON category
    //COMMON("Common", "", null, null),
    ;

    public static final Map<String, UnicodeCategory> ALL_CATEGORIES = Collections.unmodifiableMap(
            stream(values())
                    .flatMap(UnicodeCategory::allowUseOfHyphenOrSpacesOrUnderscores)
                    .collect(Collectors.toMap(
                            KeyValue::getKey,
                            KeyValue::getValue
                    )));

    private static List<String> keys(String... keys) {
        return asList(keys);
    }

    private static Stream<KeyValue> allowUseOfHyphenOrSpacesOrUnderscores(UnicodeCategory unicodeCategory) {
        Set<String> keys = Util.makeVariations(unicodeCategory.keys, '_', ' ', '-');
        return keys.stream()
                   .map(key -> new KeyValue(key, unicodeCategory));
    }

    private final List<String>      keys;
    private final String            description;
    private final List<SymbolRange> symbolRanges;
    private final Character[]       symbols;

    UnicodeCategory(List<String> keys, String description, List<SymbolRange> symbolRanges, Character[] symbols) {
        this.keys = keys;
        this.description = description;
        this.symbolRanges = symbolRanges;
        this.symbols = symbols;
    }

    UnicodeCategory(List<String> keys, String description, Character... symbols) {
        this(keys, description, emptyList(), symbols);
    }

    UnicodeCategory(List<String> keys, String description, SymbolRange symbolRange) {
        this(keys, description, singletonList(symbolRange), ZERO_LENGTH_CHARACTER_ARRAY);
    }

    public boolean isConfigured() {
        return (!symbolRanges.isEmpty()) || (symbols.length != 0);
    }

    public List<SymbolRange> getSymbolRanges() {
        return symbolRanges;
    }

    public Character[] getSymbols() {
        return symbols;
    }

    private static class KeyValue {
        private final String          key;
        private final UnicodeCategory value;

        KeyValue(String key, UnicodeCategory value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public UnicodeCategory getValue() {
            return value;
        }
    }

    public List<String> getKeys() {
        return keys;
    }

    public String getDescription() {
        return description;
    }

    public boolean contains(Character c) {
        for (SymbolRange symbolRange : symbolRanges) {
            if (symbolRange.contains(c)) {
                return true;
            }
        }

        for (Character symbol : symbols) {
            if (symbol == c) {
                return true;
            }
        }

        return false;
    }
}
