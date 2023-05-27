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

import com.github.curiousoddman.rgxgen.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.curiousoddman.rgxgen.model.SymbolRange.symbols;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

@Getter
@RequiredArgsConstructor
public enum UnicodeCategory {
    /**
     * The configuration is based on <a href="https://www.regular-expressions.info/unicode.html#category">...</a>
     */

    ANY_LETTER(asList("L", "Letter"), "Any kind of letter from any language", asList(symbols(65, 90), symbols(97, 122), symbols(192, 214), symbols(216, 246), symbols(248, 705), symbols(710, 721), symbols(736, 740), symbols(880, 884), symbols(886, 887), symbols(890, 893), symbols(904, 906), symbols(910, 929), symbols(931, 1013), symbols(1015, 1153), symbols(1162, 1327), symbols(1329, 1366), symbols(1377, 1415), symbols(1488, 1514), symbols(1520, 1522), symbols(1568, 1610), symbols(1646, 1647), symbols(1649, 1747), symbols(1765, 1766), symbols(1774, 1775), symbols(1786, 1788), symbols(1810, 1839), symbols(1869, 1957), symbols(1994, 2026), symbols(2036, 2037), symbols(2048, 2069), symbols(2112, 2136), symbols(2144, 2154), symbols(2208, 2228), symbols(2230, 2237), symbols(2308, 2361), symbols(2392, 2401), symbols(2417, 2432), symbols(2437, 2444), symbols(2447, 2448), symbols(2451, 2472), symbols(2474, 2480), symbols(2486, 2489), symbols(2524, 2525), symbols(2527, 2529), symbols(2544, 2545), symbols(2565, 2570), symbols(2575, 2576), symbols(2579, 2600), symbols(2602, 2608), symbols(2610, 2611), symbols(2613, 2614), symbols(2616, 2617), symbols(2649, 2652), symbols(2674, 2676), symbols(2693, 2701), symbols(2703, 2705), symbols(2707, 2728), symbols(2730, 2736), symbols(2738, 2739), symbols(2741, 2745), symbols(2784, 2785), symbols(2821, 2828), symbols(2831, 2832), symbols(2835, 2856), symbols(2858, 2864), symbols(2866, 2867), symbols(2869, 2873), symbols(2908, 2909), symbols(2911, 2913), symbols(2949, 2954), symbols(2958, 2960), symbols(2962, 2965), symbols(2969, 2970), symbols(2974, 2975), symbols(2979, 2980), symbols(2984, 2986), symbols(2990, 3001), symbols(3077, 3084), symbols(3086, 3088), symbols(3090, 3112), symbols(3114, 3129), symbols(3160, 3162), symbols(3168, 3169), symbols(3205, 3212), symbols(3214, 3216), symbols(3218, 3240), symbols(3242, 3251), symbols(3253, 3257), symbols(3296, 3297), symbols(3313, 3314), symbols(3333, 3340), symbols(3342, 3344), symbols(3346, 3386), symbols(3412, 3414), symbols(3423, 3425), symbols(3450, 3455), symbols(3461, 3478), symbols(3482, 3505), symbols(3507, 3515), symbols(3520, 3526), symbols(3585, 3632), symbols(3634, 3635), symbols(3648, 3654), symbols(3713, 3714), symbols(3719, 3720), symbols(3732, 3735), symbols(3737, 3743), symbols(3745, 3747), symbols(3754, 3755), symbols(3757, 3760), symbols(3762, 3763), symbols(3776, 3780), symbols(3804, 3807), symbols(3904, 3911), symbols(3913, 3948), symbols(3976, 3980), symbols(4096, 4138), symbols(4176, 4181), symbols(4186, 4189), symbols(4197, 4198), symbols(4206, 4208), symbols(4213, 4225), symbols(4256, 4293), symbols(4304, 4346), symbols(4348, 4680), symbols(4682, 4685), symbols(4688, 4694), symbols(4698, 4701), symbols(4704, 4744), symbols(4746, 4749), symbols(4752, 4784), symbols(4786, 4789), symbols(4792, 4798), symbols(4802, 4805), symbols(4808, 4822), symbols(4824, 4880), symbols(4882, 4885), symbols(4888, 4954), symbols(4992, 5007), symbols(5024, 5109), symbols(5112, 5117), symbols(5121, 5740), symbols(5743, 5759), symbols(5761, 5786), symbols(5792, 5866), symbols(5873, 5880), symbols(5888, 5900), symbols(5902, 5905), symbols(5920, 5937), symbols(5952, 5969), symbols(5984, 5996), symbols(5998, 6000), symbols(6016, 6067), symbols(6176, 6263), symbols(6272, 6276), symbols(6279, 6312), symbols(6320, 6389), symbols(6400, 6430), symbols(6480, 6509), symbols(6512, 6516), symbols(6528, 6571), symbols(6576, 6601), symbols(6656, 6678), symbols(6688, 6740), symbols(6917, 6963), symbols(6981, 6987), symbols(7043, 7072), symbols(7086, 7087), symbols(7098, 7141), symbols(7168, 7203), symbols(7245, 7247), symbols(7258, 7293), symbols(7296, 7304), symbols(7401, 7404), symbols(7406, 7409), symbols(7413, 7414), symbols(7424, 7615), symbols(7680, 7957), symbols(7960, 7965), symbols(7968, 8005), symbols(8008, 8013), symbols(8016, 8023), symbols(8031, 8061), symbols(8064, 8116), symbols(8118, 8124), symbols(8130, 8132), symbols(8134, 8140), symbols(8144, 8147), symbols(8150, 8155), symbols(8160, 8172), symbols(8178, 8180), symbols(8182, 8188), symbols(8336, 8348), symbols(8458, 8467), symbols(8473, 8477), symbols(8490, 8493), symbols(8495, 8505), symbols(8508, 8511), symbols(8517, 8521), symbols(8579, 8580), symbols(11264, 11310), symbols(11312, 11358), symbols(11360, 11492), symbols(11499, 11502), symbols(11506, 11507), symbols(11520, 11557), symbols(11568, 11623), symbols(11648, 11670), symbols(11680, 11686), symbols(11688, 11694), symbols(11696, 11702), symbols(11704, 11710), symbols(11712, 11718), symbols(11720, 11726), symbols(11728, 11734), symbols(11736, 11742), symbols(12293, 12294), symbols(12337, 12341), symbols(12347, 12348), symbols(12353, 12438), symbols(12445, 12447), symbols(12449, 12538), symbols(12540, 12543), symbols(12549, 12590), symbols(12593, 12686), symbols(12704, 12730), symbols(12784, 12799), symbols(13312, 19893), symbols(19968, 40938), symbols(40960, 42124), symbols(42192, 42237), symbols(42240, 42508), symbols(42512, 42527), symbols(42538, 42539), symbols(42560, 42606), symbols(42623, 42653), symbols(42656, 42725), symbols(42775, 42783), symbols(42786, 42888), symbols(42891, 42926), symbols(42928, 42935), symbols(42999, 43009), symbols(43011, 43013), symbols(43015, 43018), symbols(43020, 43042), symbols(43072, 43123), symbols(43138, 43187), symbols(43250, 43255), symbols(43274, 43301), symbols(43312, 43334), symbols(43360, 43388), symbols(43396, 43442), symbols(43488, 43492), symbols(43494, 43503), symbols(43514, 43518), symbols(43520, 43560), symbols(43584, 43586), symbols(43588, 43595), symbols(43616, 43638), symbols(43646, 43695), symbols(43701, 43702), symbols(43705, 43709), symbols(43739, 43741), symbols(43744, 43754), symbols(43762, 43764), symbols(43777, 43782), symbols(43785, 43790), symbols(43793, 43798), symbols(43808, 43814), symbols(43816, 43822), symbols(43824, 43866), symbols(43868, 43877), symbols(43888, 44002), symbols(44032, 55203), symbols(55216, 55238), symbols(55243, 55291), symbols(63744, 64109), symbols(64112, 64217), symbols(64256, 64262), symbols(64275, 64279), symbols(64287, 64296), symbols(64298, 64310), symbols(64312, 64316), symbols(64320, 64321), symbols(64323, 64324), symbols(64326, 64433), symbols(64467, 64829), symbols(64848, 64911), symbols(64914, 64967), symbols(65008, 65019), symbols(65136, 65140), symbols(65142, 65276), symbols(65313, 65338), symbols(65345, 65370), symbols(65382, 65470), symbols(65474, 65479), symbols(65482, 65487), symbols(65490, 65495), symbols(65498, 65500)), new Character[]{'ª', 'µ', 'º', 'ˬ', 'ˮ', 'Ϳ', 'Ά', 'Ό', 'ՙ', 'ە', 'ۿ', 'ܐ', 'ޱ', 'ߺ', 'ࠚ', 'ࠤ', 'ࠨ', 'ऽ', 'ॐ', 'ল', 'ঽ', 'ৎ', 'ৼ', 'ਫ਼', 'ઽ', 'ૐ', 'ૹ', 'ଽ', 'ୱ', 'ஃ', 'ஜ', 'ௐ', 'ఽ', 'ಀ', 'ಽ', 'ೞ', 'ഽ', 'ൎ', 'ල', 'ຄ', 'ຊ', 'ຍ', 'ລ', 'ວ', 'ຽ', 'ໆ', 'ༀ', 'ဿ', 'ၡ', 'ႎ', 'Ⴧ', 'Ⴭ', 'ቘ', 'ዀ', 'ៗ', 'ៜ', 'ᢪ', 'ᪧ', 'Ὑ', 'Ὓ', 'Ὕ', 'ι', 'ⁱ', 'ⁿ', 'ℂ', 'ℇ', 'ℕ', 'ℤ', 'Ω', 'ℨ', 'ⅎ', 'ⴧ', 'ⴭ', 'ⵯ', 'ⸯ', 'ꣻ', 'ꣽ', 'ꧏ', 'ꩺ', 'ꪱ', 'ꫀ', 'ꫂ', 'יִ', 'מּ'}),
    LOWERCASE_LETTER(asList("Ll", "Lowercase_Letter"), "a lowercase letter that has an uppercase variant.", null, null),
    UPPERCASE_LETTER(asList("Lu", "Uppercase_Letter"), "an uppercase letter that has a lowercase variant.", null, null),
    TITLECASE_LETTER(asList("Lt", "Titlecase_Letter"), "a letter that appears at the start of a word when only the first letter of the word is capitalized.", null, null),
    CASED_LETTER(asList("L&", "Cased_Letter"), "a letter that exists in lowercase and uppercase variants (combination of Ll, Lu and Lt).", null, null),
    MODIFIER_LETTER(asList("Lm", "Modifier_Letter"), "a special character that is used like a letter.", null, null),
    OTHER_LETTER(asList("Lo", "Other_Letter"), "a letter or ideograph that does not have lowercase and uppercase variants.", null, null),
    MARK(asList("M", "Mark"), "a character intended to be combined with another character (e.g. accents, umlauts, enclosing boxes, etc.).", null, null),
    NON_SPACING_MARK(asList("Mn", "Non_Spacing_Mark"), "a character intended to be combined with another character without taking up extra space (e.g. accents, umlauts, etc.).", null, null),
    SPACING_COMBINING_MARK(asList("Mc", "Spacing_Combining_Mark"), "a character intended to be combined with another character that takes up extra space (vowel signs in many Eastern languages).", null, null),
    ENCLOSING_MARK(asList("Me", "Enclosing_Mark"), "a character that encloses the character it is combined with (circle, square, keycap, etc.).", null, null),
    SEPARATOR(asList("Z", "Separator"), "any kind of whitespace or invisible separator.", null, null),
    SPACE_SEPARATOR(asList("Zs", "Space_Separator"), "a whitespace character that is invisible, but does take up space.", null, null),
    LINE_SEPARATOR(asList("Zl", "Line_Separator"), "line separator character U+2028.", null, null),
    PARAGRAPH_SEPARATOR(asList("Zp", "Paragraph_Separator"), "paragraph separator character U+2029.", null, null),
    SYMBOL(asList("S", "Symbol"), "math symbols, currency signs, dingbats, box-drawing characters, etc.", null, null),
    MATH_SYMBOL(asList("Sm", "Math_Symbol"), "any mathematical symbol.", null, null),
    CURRENCY_SYMBOL(asList("Sc", "Currency_Symbol"), "any currency sign.", null, null),
    MODIFIER_SYMBOL(asList("Sk", "Modifier_Symbol"), "a combining character (mark) as a full character on its own.", null, null),
    OTHER_SYMBOL(asList("So", "Other_Symbol"), "various symbols that are not math symbols, currency signs, or combining characters.", null, null),
    NUMBER(asList("N", "Number"), "any kind of numeric character in any script.", null, null),
    DECIMAL_DIGIT_NUMBER(asList("Nd", "Decimal_Digit_Number"), "a digit zero through nine in any script except ideographic scripts.", null, null),
    LETTER_NUMBER(asList("Nl", "Letter_Number"), "a number that looks like a letter, such as a Roman numeral.", null, null),
    OTHER_NUMBER(asList("No", "Other_Number"), "a superscript or subscript digit, or a number that is not a digit 0-9 (excluding numbers from ideographic scripts).", null, null),
    PUNCTUATION(asList("P", "Punctuation"), "any kind of punctuation character.", null, null),
    DASH_PUNCTUATION(asList("Pd", "Dash_Punctuation"), "any kind of hyphen or dash.", null, null),
    OPEN_PUNCTUATION(asList("Ps", "Open_Punctuation"), "any kind of opening bracket.", null, null),
    CLOSE_PUNCTUATION(asList("Pe", "Close_Punctuation"), "any kind of closing bracket.", null, null),
    INITIAL_PUNCTUATION(asList("Pi", "Initial_Punctuation"), "any kind of opening quote.", null, null),
    FINAL_PUNCTUATION(asList("Pf", "Final_Punctuation"), "any kind of closing quote.", null, null),
    CONNECTOR_PUNCTUATION(asList("Pc", "Connector_Punctuation"), "a punctuation character such as an underscore that connects words.", null, null),
    OTHER_PUNCTUATION(asList("Po", "Other_Punctuation"), "any kind of punctuation character that is not a dash, bracket, quote or connector.", null, null),
    OTHER(asList("C", "Other"), "invisible control characters and unused code points.", null, null),
    CONTROL(asList("Cc", "Control"), "an ASCII or Latin-1 control character", null, null),
    FORMAT(asList("Cf", "Format"), "invisible formatting indicator.", null, null),
    PRIVATE_USE(asList("Co", "Private_Use"), "any code point reserved for private use.", null, null),
    SURROGATE(asList("Cs", "Surrogate"), "one half of a surrogate pair in UTF-16 encoding.", null, null),
    UNASSIGNED(asList("Cn", "Unassigned"), "any code point to which no character has been assigned.", null, null),
    COMMON("Common", "", null, null),
    ARABIC("Arabic", "", null, null),
    ARMENIAN("Armenian", "", null, null),
    BENGALI("Bengali", "", null, null),
    BOPOMOFO("Bopomofo", "", null, null),
    BRAILLE("Braille", "", null, null),
    BUHID("Buhid", "", null, null),
    CANADIAN_ABORIGINAL("Canadian_Aboriginal", "", null, null),
    CHEROKEE("Cherokee", "", null, null),
    CYRILLIC("Cyrillic", "", null, null),
    DEVANAGARI("Devanagari", "", null, null),
    ETHIOPIC("Ethiopic", "", null, null),
    GEORGIAN("Georgian", "", null, null),
    GREEK("Greek", "", null, null),
    GUJARATI("Gujarati", "", null, null),
    GURMUKHI("Gurmukhi", "", null, null),
    HAN("Han", "", null, null),
    HANGUL("Hangul", "", null, null),
    HANUNOO("Hanunoo", "", null, null),
    HEBREW("Hebrew", "", null, null),
    HIRAGANA("Hiragana", "", null, null),
    INHERITED("Inherited", "", null, null),
    KANNADA("Kannada", "", null, null),
    KATAKANA("Katakana", "", null, null),
    KHMER("Khmer", "", null, null),
    LAO("Lao", "", null, null),
    LATIN("Latin", "", null, null),
    LIMBU("Limbu", "", null, null),
    MALAYALAM("Malayalam", "", null, null),
    MONGOLIAN("Mongolian", "", null, null),
    MYANMAR("Myanmar", "", null, null),
    OGHAM("Ogham", "", null, null),
    ORIYA("Oriya", "", null, null),
    RUNIC("Runic", "", null, null),
    SINHALA("Sinhala", "", null, null),
    SYRIAC("Syriac", "", null, null),
    TAGALOG("Tagalog", "", null, null),
    TAGBANWA("Tagbanwa", "", null, null),
    TAILE("TaiLe", "", null, null),
    TAMIL("Tamil", "", null, null),
    TELUGU("Telugu", "", null, null),
    THAANA("Thaana", "", null, null),
    THAI("Thai", "", null, null),
    TIBETAN("Tibetan", "", null, null),
    YI("Yi", "", null, null),
    IN_BASIC_LATIN("InBasic_Latin", "U+0000-U+007F", symbols(0x0000, 0x007F)),
    IN_LATIN_1_SUPPLEMENT("InLatin-1_Supplement", "U+0080-U+00FF", symbols(0x0080, 0x00FF)),
    IN_LATIN_EXTENDED_A("InLatin_Extended-A", "U+0100-U+017F", symbols(0x0100, 0x017F)),
    IN_LATIN_EXTENDED_B("InLatin_Extended-B", "U+0180-U+024F", symbols(0x0180, 0x024F)),
    IN_IPA_EXTENSIONS("InIPA_Extensions", "U+0250-U+02AF", symbols(0x0250, 0x02AF)),
    IN_SPACING_MODIFIER_LETTERS("InSpacing_Modifier_Letters", "U+02B0-U+02FF", symbols(0x02B0, 0x02FF)),
    IN_COMBINING_DIACRITICAL_MARKS("InCombining_Diacritical_Marks", "U+0300-U+036F", symbols(0x0300, 0x036F)),
    IN_GREEK_AND_COPTIC("InGreek_and_Coptic", "U+0370-U+03FF", symbols(0x0370, 0x03FF)),
    IN_CYRILLIC("InCyrillic", "U+0400-U+04FF", symbols(0x0400, 0x04FF)),
    IN_CYRILLIC_SUPPLEMENTARY("InCyrillic_Supplementary", "U+0500-U+052F", symbols(0x0500, 0x052F)),
    IN_ARMENIAN("InArmenian", "U+0530-U+058F", symbols(0x0530, 0x058F)),
    IN_HEBREW("InHebrew", "U+0590-U+05FF", symbols(0x0590, 0x05FF)),
    IN_ARABIC("InArabic", "U+0600-U+06FF", symbols(0x0600, 0x06FF)),
    IN_SYRIAC("InSyriac", "U+0700-U+074F", symbols(0x0700, 0x074F)),
    IN_THAANA("InThaana", "U+0780-U+07BF", symbols(0x0780, 0x07BF)),
    IN_DEVANAGARI("InDevanagari", "U+0900-U+097F", symbols(0x0900, 0x097F)),
    IN_BENGALI("InBengali", "U+0980-U+09FF", symbols(0x0980, 0x09FF)),
    IN_GURMUKHI("InGurmukhi", "U+0A00-U+0A7F", symbols(0x0A00, 0x0A7F)),
    IN_GUJARATI("InGujarati", "U+0A80-U+0AFF", symbols(0x0A80, 0x0AFF)),
    IN_ORIYA("InOriya", "U+0B00-U+0B7F", symbols(0x0B00, 0x0B7F)),
    IN_TAMIL("InTamil", "U+0B80-U+0BFF", symbols(0x0B80, 0x0BFF)),
    IN_TELUGU("InTelugu", "U+0C00-U+0C7F", symbols(0x0C00, 0x0C7F)),
    IN_KANNADA("InKannada", "U+0C80-U+0CFF", symbols(0x0C80, 0x0CFF)),
    IN_MALAYALAM("InMalayalam", "U+0D00-U+0D7F", symbols(0x0D00, 0x0D7F)),
    IN_SINHALA("InSinhala", "U+0D80-U+0DFF", symbols(0x0D80, 0x0DFF)),
    IN_THAI("InThai", "U+0E00-U+0E7F", symbols(0x0E00, 0x0E7F)),
    IN_LAO("InLao", "U+0E80-U+0EFF", symbols(0x0E80, 0x0EFF)),
    IN_TIBETAN("InTibetan", "U+0F00-U+0FFF", symbols(0x0F00, 0x0FFF)),
    IN_MYANMAR("InMyanmar", "U+1000-U+109F", symbols(0x1000, 0x109F)),
    IN_GEORGIAN("InGeorgian", "U+10A0-U+10FF", symbols(0x10A0, 0x10FF)),
    IN_HANGUL_JAMO("InHangul_Jamo", "U+1100-U+11FF", symbols(0x1100, 0x11FF)),
    IN_ETHIOPIC("InEthiopic", "U+1200-U+137F", symbols(0x1200, 0x137F)),
    IN_CHEROKEE("InCherokee", "U+13A0-U+13FF", symbols(0x13A0, 0x13FF)),
    IN_UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS("InUnified_Canadian_Aboriginal_Syllabics", "U+1400-U+167F", symbols(0x1400, 0x167F)),
    IN_OGHAM("InOgham", "U+1680-U+169F", symbols(0x1680, 0x169F)),
    IN_RUNIC("InRunic", "U+16A0-U+16FF", symbols(0x16A0, 0x16FF)),
    IN_TAGALOG("InTagalog", "U+1700-U+171F", symbols(0x1700, 0x171F)),
    IN_HANUNOO("InHanunoo", "U+1720-U+173F", symbols(0x1720, 0x173F)),
    IN_BUHID("InBuhid", "U+1740-U+175F", symbols(0x1740, 0x175F)),
    IN_TAGBANWA("InTagbanwa", "U+1760-U+177F", symbols(0x1760, 0x177F)),
    IN_KHMER("InKhmer", "U+1780-U+17FF", symbols(0x1780, 0x17FF)),
    IN_MONGOLIAN("InMongolian", "U+1800-U+18AF", symbols(0x1800, 0x18AF)),
    IN_LIMBU("InLimbu", "U+1900-U+194F", symbols(0x1900, 0x194F)),
    IN_TAI_LE("InTai_Le", "U+1950-U+197F", symbols(0x1950, 0x197F)),
    IN_KHMER_SYMBOLS("InKhmer_Symbols", "U+19E0-U+19FF", symbols(0x19E0, 0x19FF)),
    IN_PHONETIC_EXTENSIONS("InPhonetic_Extensions", "U+1D00-U+1D7F", symbols(0x1D00, 0x1D7F)),
    IN_LATIN_EXTENDED_ADDITIONAL("InLatin_Extended_Additional", "U+1E00-U+1EFF", symbols(0x1E00, 0x1EFF)),
    IN_GREEK_EXTENDED("InGreek_Extended", "U+1F00-U+1FFF", symbols(0x1F00, 0x1FFF)),
    IN_GENERAL_PUNCTUATION("InGeneral_Punctuation", "U+2000-U+206F", symbols(0x2000, 0x206F)),
    IN_SUPERSCRIPTS_AND_SUBSCRIPTS("InSuperscripts_and_Subscripts", "U+2070-U+209F", symbols(0x2070, 0x209F)),
    IN_CURRENCY_SYMBOLS("InCurrency_Symbols", "U+20A0-U+20CF", symbols(0x20A0, 0x20CF)),
    IN_COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS("InCombining_Diacritical_Marks_for_Symbols", "U+20D0-U+20FF", symbols(0x20D0, 0x20FF)),
    IN_LETTERLIKE_SYMBOLS("InLetterlike_Symbols", "U+2100-U+214F", symbols(0x2100, 0x214F)),
    IN_NUMBER_FORMS("InNumber_Forms", "U+2150-U+218F", symbols(0x2150, 0x218F)),
    IN_ARROWS("InArrows", "U+2190-U+21FF", symbols(0x2190, 0x21FF)),
    IN_MATHEMATICAL_OPERATORS("InMathematical_Operators", "U+2200-U+22FF", symbols(0x2200, 0x22FF)),
    IN_MISCELLANEOUS_TECHNICAL("InMiscellaneous_Technical", "U+2300-U+23FF", symbols(0x2300, 0x23FF)),
    IN_CONTROL_PICTURES("InControl_Pictures", "U+2400-U+243F", symbols(0x2400, 0x243F)),
    IN_OPTICAL_CHARACTER_RECOGNITION("InOptical_Character_Recognition", "U+2440-U+245F", symbols(0x2440, 0x245F)),
    IN_ENCLOSED_ALPHANUMERICS("InEnclosed_Alphanumerics", "U+2460-U+24FF", symbols(0x2460, 0x24FF)),
    IN_BOX_DRAWING("InBox_Drawing", "U+2500-U+257F", symbols(0x2500, 0x257F)),
    IN_BLOCK_ELEMENTS("InBlock_Elements", "U+2580-U+259F", symbols(0x2580, 0x259F)),
    IN_GEOMETRIC_SHAPES("InGeometric_Shapes", "U+25A0-U+25FF", symbols(0x25A0, 0x25FF)),
    IN_MISCELLANEOUS_SYMBOLS("InMiscellaneous_Symbols", "U+2600-U+26FF", symbols(0x2600, 0x26FF)),
    IN_DINGBATS("InDingbats", "U+2700-U+27BF", symbols(0x2700, 0x27BF)),
    IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A("InMiscellaneous_Mathematical_Symbols-A", "U+27C0-U+27EF", symbols(0x27C0, 0x27EF)),
    IN_SUPPLEMENTAL_ARROWS_A("InSupplemental_Arrows-A", "U+27F0-U+27FF", symbols(0x27F0, 0x27FF)),
    IN_BRAILLE_PATTERNS("InBraille_Patterns", "U+2800-U+28FF", symbols(0x2800, 0x28FF)),
    IN_SUPPLEMENTAL_ARROWS_B("InSupplemental_Arrows-B", "U+2900-U+297F", symbols(0x2900, 0x297F)),
    IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B("InMiscellaneous_Mathematical_Symbols-B", "U+2980-U+29FF", symbols(0x2980, 0x29FF)),
    IN_SUPPLEMENTAL_MATHEMATICAL_OPERATORS("InSupplemental_Mathematical_Operators", "U+2A00-U+2AFF", symbols(0x2A00, 0x2AFF)),
    IN_MISCELLANEOUS_SYMBOLS_AND_ARROWS("InMiscellaneous_Symbols_and_Arrows", "U+2B00-U+2BFF", symbols(0x2B00, 0x2BFF)),
    IN_CJK_RADICALS_SUPPLEMENT("InCJK_Radicals_Supplement", "U+2E80-U+2EFF", symbols(0x2E80, 0x2EFF)),
    IN_KANGXI_RADICALS("InKangxi_Radicals", "U+2F00-U+2FDF", symbols(0x2F00, 0x2FDF)),
    IN_IDEOGRAPHIC_DESCRIPTION_CHARACTERS("InIdeographic_Description_Characters", "U+2FF0-U+2FFF", symbols(0x2FF0, 0x2FFF)),
    IN_CJK_SYMBOLS_AND_PUNCTUATION("InCJK_Symbols_and_Punctuation", "U+3000-U+303F", symbols(0x3000, 0x303F)),
    IN_HIRAGANA("InHiragana", "U+3040-U+309F", symbols(0x3040, 0x309F)),
    IN_KATAKANA("InKatakana", "U+30A0-U+30FF", symbols(0x30A0, 0x30FF)),
    IN_BOPOMOFO("InBopomofo", "U+3100-U+312F", symbols(0x3100, 0x312F)),
    IN_HANGUL_COMPATIBILITY_JAMO("InHangul_Compatibility_Jamo", "U+3130-U+318F", symbols(0x3130, 0x318F)),
    IN_KANBUN("InKanbun", "U+3190-U+319F", symbols(0x3190, 0x319F)),
    IN_BOPOMOFO_EXTENDED("InBopomofo_Extended", "U+31A0-U+31BF", symbols(0x31A0, 0x31BF)),
    IN_KATAKANA_PHONETIC_EXTENSIONS("InKatakana_Phonetic_Extensions", "U+31F0-U+31FF", symbols(0x31F0, 0x31FF)),
    IN_ENCLOSED_CJK_LETTERS_AND_MONTHS("InEnclosed_CJK_Letters_and_Months", "U+3200-U+32FF", symbols(0x3200, 0x32FF)),
    IN_CJK_COMPATIBILITY("InCJK_Compatibility", "U+3300-U+33FF", symbols(0x3300, 0x33FF)),
    IN_CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A("InCJK_Unified_Ideographs_Extension_A", "U+3400-U+4DBF", symbols(0x3400, 0x4DBF)),
    IN_YIJING_HEXAGRAM_SYMBOLS("InYijing_Hexagram_Symbols", "U+4DC0-U+4DFF", symbols(0x4DC0, 0x4DFF)),
    IN_CJK_UNIFIED_IDEOGRAPHS("InCJK_Unified_Ideographs", "U+4E00-U+9FFF", symbols(0x4E00, 0x9FFF)),
    IN_YI_SYLLABLES("InYi_Syllables", "U+A000-U+A48F", symbols(0xA000, 0xA48F)),
    IN_YI_RADICALS("InYi_Radicals", "U+A490-U+A4CF", symbols(0xA490, 0xA4CF)),
    IN_HANGUL_SYLLABLES("InHangul_Syllables", "U+AC00-U+D7AF", symbols(0xAC00, 0xD7AF)),
    IN_HIGH_SURROGATES("InHigh_Surrogates", "U+D800-U+DB7F", symbols(0xD800, 0xDB7F)),
    IN_HIGH_PRIVATE_USE_SURROGATES("InHigh_Private_Use_Surrogates", "U+DB80-U+DBFF", symbols(0xDB80, 0xDBFF)),
    IN_LOW_SURROGATES("InLow_Surrogates", "U+DC00-U+DFFF", symbols(0xDC00, 0xDFFF)),
    IN_PRIVATE_USE_AREA("InPrivate_Use_Area", "U+E000-U+F8FF", symbols(0xE000, 0xF8FF)),
    IN_CJK_COMPATIBILITY_IDEOGRAPHS("InCJK_Compatibility_Ideographs", "U+F900-U+FAFF", symbols(0xF900, 0xFAFF)),
    IN_ALPHABETIC_PRESENTATION_FORMS("InAlphabetic_Presentation_Forms", "U+FB00-U+FB4F", symbols(0xFB00, 0xFB4F)),
    IN_ARABIC_PRESENTATION_FORMS_A("InArabic_Presentation_Forms-A", "U+FB50-U+FDFF", symbols(0xFB50, 0xFDFF)),
    IN_VARIATION_SELECTORS("InVariation_Selectors", "U+FE00-U+FE0F", symbols(0xFE00, 0xFE0F)),
    IN_COMBINING_HALF_MARKS("InCombining_Half_Marks", "U+FE20-U+FE2F", symbols(0xFE20, 0xFE2F)),
    IN_CJK_COMPATIBILITY_FORMS("InCJK_Compatibility_Forms", "U+FE30-U+FE4F", symbols(0xFE30, 0xFE4F)),
    IN_SMALL_FORM_VARIANTS("InSmall_Form_Variants", "U+FE50-U+FE6F", symbols(0xFE50, 0xFE6F)),
    IN_ARABIC_PRESENTATION_FORMS_B("InArabic_Presentation_Forms-B", "U+FE70-U+FEFF", symbols(0xFE70, 0xFEFF)),
    IN_HALFWIDTH_AND_FULLWIDTH_FORMS("InHalfwidth_and_Fullwidth_Forms", "U+FF00-U+FFEF", symbols(0xFF00, 0xFFEF)),
    IN_SPECIALS("InSpecials", "U+FFF0-U+FFFF", symbols(0xFFF0, 0xFFFF));

    public static final Map<String, UnicodeCategory> ALL_CATEGORIES = Collections.unmodifiableMap(
            stream(values())
                    .flatMap(UnicodeCategory::allowUseOfHyphenOrSpacesOrUnderscores)
                    .collect(Collectors.toMap(
                            KeyValue::getKey,
                            KeyValue::getValue
                    )));

    private static Stream<KeyValue> allowUseOfHyphenOrSpacesOrUnderscores(UnicodeCategory unicodeCategory) {
        Set<String> keys = Util.makeVariations(unicodeCategory.keys, '_', ' ', '-');
        return keys.stream()
                   .map(key -> new KeyValue(key, unicodeCategory));
    }

    // TODO: allow you to use the shorthand \pL. The shorthand only works with single-letter Unicode properties.
    private final List<String>            keys;
    private final String                  description;
    private final Collection<SymbolRange> symbolRanges;
    private final Character[]             symbols;

    UnicodeCategory(String key, String description, Collection<SymbolRange> symbolRanges, Character[] symbols) {
        this(Collections.singletonList(key), description, symbolRanges, symbols);
    }

    UnicodeCategory(String key, String description, SymbolRange symbolRange) {
        this(Collections.singletonList(key), description, Collections.singletonList(symbolRange), null);
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
}
