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

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

@Getter
@RequiredArgsConstructor
public enum UnicodeCategory {

    ANY_LETTER(asList("L", "Letter"), "Any kind of letter from any language", null, null),
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
    IN_BASIC_LATIN_("InBasic_Latin", "U+0000-U+007F", null, null),
    IN_LATIN_1_SUPPLEMENT("InLatin-1_Supplement", "U+0080-U+00FF", null, null),
    IN_LATIN_EXTENDED_A("InLatin_Extended-A", "U+0100-U+017F", null, null),
    IN_LATIN_EXTENDED_B("InLatin_Extended-B", "U+0180-U+024F", null, null),
    IN_IPA_EXTENSIONS("InIPA_Extensions", "U+0250-U+02AF", null, null),
    IN_SPACING_MODIFIER_LETTERS("InSpacing_Modifier_Letters", "U+02B0-U+02FF", null, null),
    INCOMBINING_DIACRITICAL_MARKS("InCombining_Diacritical_Marks", "U+0300-U+036F", null, null),
    IN_GREEK_AND_COPTIC("InGreek_and_Coptic", "U+0370-U+03FF", null, null),
    IN_CYRILLIC("InCyrillic", "U+0400-U+04FF", null, null),
    IN_CYRILLIC_SUPPLEMENTARY("InCyrillic_Supplementary", "U+0500-U+052F", null, null),
    IN_ARMENIAN("InArmenian", "U+0530-U+058F", null, null),
    IN_HEBREW("InHebrew", "U+0590-U+05FF", null, null),
    IN_ARABIC("InArabic", "U+0600-U+06FF", null, null),
    IN_SYRIAC("InSyriac", "U+0700-U+074F", null, null),
    IN_THAANA("InThaana", "U+0780-U+07BF", null, null),
    IN_DEVANAGARI("InDevanagari", "U+0900-U+097F", null, null),
    IN_BENGALI("InBengali", "U+0980-U+09FF", null, null),
    IN_GURMUKHI("InGurmukhi", "U+0A00-U+0A7F", null, null),
    IN_GUJARATI("InGujarati", "U+0A80-U+0AFF", null, null),
    IN_ORIYA("InOriya", "U+0B00-U+0B7F", null, null),
    IN_TAMIL("InTamil", "U+0B80-U+0BFF", null, null),
    IN_TELUGU("InTelugu", "U+0C00-U+0C7F", null, null),
    IN_KANNADA("InKannada", "U+0C80-U+0CFF", null, null),
    IN_MALAYALAM("InMalayalam", "U+0D00-U+0D7F", null, null),
    IN_SINHALA("InSinhala", "U+0D80-U+0DFF", null, null),
    IN_THAI("InThai", "U+0E00-U+0E7F", null, null),
    IN_LAO("InLao", "U+0E80-U+0EFF", null, null),
    IN_TIBETAN("InTibetan", "U+0F00-U+0FFF", null, null),
    IN_MYANMAR("InMyanmar", "U+1000-U+109F", null, null),
    IN_GEORGIAN("InGeorgian", "U+10A0-U+10FF", null, null),
    IN_HANGUL_JAMO("InHangul_Jamo", "U+1100-U+11FF", null, null),
    IN_ETHIOPIC("InEthiopic", "U+1200-U+137F", null, null),
    IN_CHEROKEE("InCherokee", "U+13A0-U+13FF", null, null),
    IN_UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS("InUnified_Canadian_Aboriginal_Syllabics", "U+1400-U+167F", null, null),
    IN_OGHAM("InOgham", "U+1680-U+169F", null, null),
    IN_RUNIC("InRunic", "U+16A0-U+16FF", null, null),
    IN_TAGALOG("InTagalog", "U+1700-U+171F", null, null),
    IN_HANUNOO("InHanunoo", "U+1720-U+173F", null, null),
    IN_BUHID("InBuhid", "U+1740-U+175F", null, null),
    IN_TAGBANWA("InTagbanwa", "U+1760-U+177F", null, null),
    IN_KHMER("InKhmer", "U+1780-U+17FF", null, null),
    IN_MONGOLIAN("InMongolian", "U+1800-U+18AF", null, null),
    IN_LIMBU("InLimbu", "U+1900-U+194F", null, null),
    IN_TAI_LE("InTai_Le", "U+1950-U+197F", null, null),
    IN_KHMER_SYMBOLS("InKhmer_Symbols", "U+19E0-U+19FF", null, null),
    IN_PHONETIC_EXTENSIONS("InPhonetic_Extensions", "U+1D00-U+1D7F", null, null),
    IN_LATIN_EXTENDED_ADDITIONAL("InLatin_Extended_Additional", "U+1E00-U+1EFF", null, null),
    IN_GREEK_EXTENDED("InGreek_Extended", "U+1F00-U+1FFF", null, null),
    IN_GENERAL_PUNCTUATION("InGeneral_Punctuation", "U+2000-U+206F", null, null),
    IN_SUPERSCRIPTS_AND_SUBSCRIPTS("InSuperscripts_and_Subscripts", "U+2070-U+209F", null, null),
    IN_CURRENCY_SYMBOLS("InCurrency_Symbols", "U+20A0-U+20CF", null, null),
    IN_COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS("InCombining_Diacritical_Marks_for_Symbols", "U+20D0-U+20FF", null, null),
    IN_LETTERLIKE_SYMBOLS("InLetterlike_Symbols", "U+2100-U+214F", null, null),
    IN_NUMBER_FORMS("InNumber_Forms", "U+2150-U+218F", null, null),
    IN_ARROWS("InArrows", "U+2190-U+21FF", null, null),
    IN_MATHEMATICAL_OPERATORS("InMathematical_Operators", "U+2200-U+22FF", null, null),
    IN_MISCELLANEOUS_TECHNICAL("InMiscellaneous_Technical", "U+2300-U+23FF", null, null),
    IN_CONTROL_PICTURES("InControl_Pictures", "U+2400-U+243F", null, null),
    IN_OPTICAL_CHARACTER_RECOGNITION("InOptical_Character_Recognition", "U+2440-U+245F", null, null),
    IN_ENCLOSED_ALPHANUMERICS("InEnclosed_Alphanumerics", "U+2460-U+24FF", null, null),
    IN_BOX_DRAWING("InBox_Drawing", "U+2500-U+257F", null, null),
    IN_BLOCK_ELEMENTS("InBlock_Elements", "U+2580-U+259F", null, null),
    IN_GEOMETRIC_SHAPES("InGeometric_Shapes", "U+25A0-U+25FF", null, null),
    IN_MISCELLANEOUS_SYMBOLS("InMiscellaneous_Symbols", "U+2600-U+26FF", null, null),
    IN_DINGBATS("InDingbats", "U+2700-U+27BF", null, null),
    IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A("InMiscellaneous_Mathematical_Symbols-A", "U+27C0-U+27EF", null, null),
    IN_SUPPLEMENTAL_ARROWS_A("InSupplemental_Arrows-A", "U+27F0-U+27FF", null, null),
    IN_BRAILLE_PATTERNS("InBraille_Patterns", "U+2800-U+28FF", null, null),
    IN_SUPPLEMENTAL_ARROWS_B("InSupplemental_Arrows-B", "U+2900-U+297F", null, null),
    IN_MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B("InMiscellaneous_Mathematical_Symbols-B", "U+2980-U+29FF", null, null),
    IN_SUPPLEMENTAL_MATHEMATICAL_OPERATORS("InSupplemental_Mathematical_Operators", "U+2A00-U+2AFF", null, null),
    IN_MISCELLANEOUS_SYMBOLS_AND_ARROWS("InMiscellaneous_Symbols_and_Arrows", "U+2B00-U+2BFF", null, null),
    IN_CJK_RADICALS_SUPPLEMENT("InCJK_Radicals_Supplement", "U+2E80-U+2EFF", null, null),
    IN_KANGXI_RADICALS("InKangxi_Radicals", "U+2F00-U+2FDF", null, null),
    IN_IDEOGRAPHIC_DESCRIPTION_CHARACTERS("InIdeographic_Description_Characters", "U+2FF0-U+2FFF", null, null),
    IN_CJK_SYMBOLS_AND_PUNCTUATION("InCJK_Symbols_and_Punctuation", "U+3000-U+303F", null, null),
    IN_HIRAGANA("InHiragana", "U+3040-U+309F", null, null),
    IN_KATAKANA("InKatakana", "U+30A0-U+30FF", null, null),
    IN_BOPOMOFO("InBopomofo", "U+3100-U+312F", null, null),
    IN_HANGUL_COMPATIBILITY_JAMO("InHangul_Compatibility_Jamo", "U+3130-U+318F", null, null),
    IN_KANBUN("InKanbun", "U+3190-U+319F", null, null),
    IN_BOPOMOFO_EXTENDED("InBopomofo_Extended", "U+31A0-U+31BF", null, null),
    IN_KATAKANA_PHONETIC_EXTENSIONS("InKatakana_Phonetic_Extensions", "U+31F0-U+31FF", null, null),
    IN_ENCLOSED_CJK_LETTERS_AND_MONTHS("InEnclosed_CJK_Letters_and_Months", "U+3200-U+32FF", null, null),
    IN_CJK_COMPATIBILITY("InCJK_Compatibility", "U+3300-U+33FF", null, null),
    IN_CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A("InCJK_Unified_Ideographs_Extension_A", "U+3400-U+4DBF", null, null),
    IN_YIJING_HEXAGRAM_SYMBOLS("InYijing_Hexagram_Symbols", "U+4DC0-U+4DFF", null, null),
    IN_CJK_UNIFIED_IDEOGRAPHS("InCJK_Unified_Ideographs", "U+4E00-U+9FFF", null, null),
    IN_YI_SYLLABLES("InYi_Syllables", "U+A000-U+A48F", null, null),
    IN_YI_RADICALS("InYi_Radicals", "U+A490-U+A4CF", null, null),
    IN_HANGUL_SYLLABLES("InHangul_Syllables", "U+AC00-U+D7AF", null, null),
    IN_HIGH_SURROGATES("InHigh_Surrogates", "U+D800-U+DB7F", null, null),
    IN_HIGH_PRIVATE_USE_SURROGATES("InHigh_Private_Use_Surrogates", "U+DB80-U+DBFF", null, null),
    IN_LOW_SURROGATES("InLow_Surrogates", "U+DC00-U+DFFF", null, null),
    IN_PRIVATE_USE_AREA("InPrivate_Use_Area", "U+E000-U+F8FF", null, null),
    IN_CJK_COMPATIBILITY_IDEOGRAPHS("InCJK_Compatibility_Ideographs", "U+F900-U+FAFF", null, null),
    IN_ALPHABETIC_PRESENTATION_FORMS("InAlphabetic_Presentation_Forms", "U+FB00-U+FB4F", null, null),
    IN_ARABIC_PRESENTATION_FORMS_A("InArabic_Presentation_Forms-A", "U+FB50-U+FDFF", null, null),
    IN_VARIATION_SELECTORS("InVariation_Selectors", "U+FE00-U+FE0F", null, null),
    IN_COMBINING_HALF_MARKS("InCombining_Half_Marks", "U+FE20-U+FE2F", null, null),
    IN_CJK_COMPATIBILITY_FORMS("InCJK_Compatibility_Forms", "U+FE30-U+FE4F", null, null),
    IN_SMALL_FORM_VARIANTS("InSmall_Form_Variants", "U+FE50-U+FE6F", null, null),
    IN_ARABIC_PRESENTATION_FORMS_B("InArabic_Presentation_Forms-B", "U+FE70-U+FEFF", null, null),
    IN_HALFWIDTH_AND_FULLWIDTH_FORMS("InHalfwidth_and_Fullwidth_Forms", "U+FF00-U+FFEF", null, null),
    IN_SPECIALS("InSpecials", "U+FFF0-U+FFFF", null, null),

    /*
\p{Ll} or \p{Lowercase_Letter}: a lowercase letter that has an uppercase variant.
\p{Lu} or \p{Uppercase_Letter}: an uppercase letter that has a lowercase variant.
\p{Lt} or \p{Titlecase_Letter}: a letter that appears at the start of a word when only the first letter of the word is capitalized.
\p{L&} or \p{Cased_Letter}: a letter that exists in lowercase and uppercase variants (combination of Ll, Lu and Lt).
\p{Lm} or \p{Modifier_Letter}: a special character that is used like a letter.
\p{Lo} or \p{Other_Letter}: a letter or ideograph that does not have lowercase and uppercase variants.
\p{M} or \p{Mark}: a character intended to be combined with another character (e.g. accents, umlauts, enclosing boxes, etc.).
\p{Mn} or \p{Non_Spacing_Mark}: a character intended to be combined with another character without taking up extra space (e.g. accents, umlauts, etc.).
\p{Mc} or \p{Spacing_Combining_Mark}: a character intended to be combined with another character that takes up extra space (vowel signs in many Eastern languages).
\p{Me} or \p{Enclosing_Mark}: a character that encloses the character it is combined with (circle, square, keycap, etc.).
\p{Z} or \p{Separator}: any kind of whitespace or invisible separator.
\p{Zs} or \p{Space_Separator}: a whitespace character that is invisible, but does take up space.
\p{Zl} or \p{Line_Separator}: line separator character U+2028.
\p{Zp} or \p{Paragraph_Separator}: paragraph separator character U+2029.
\p{S} or \p{Symbol}: math symbols, currency signs, dingbats, box-drawing characters, etc.
\p{Sm} or \p{Math_Symbol}: any mathematical symbol.
\p{Sc} or \p{Currency_Symbol}: any currency sign.
\p{Sk} or \p{Modifier_Symbol}: a combining character (mark) as a full character on its own.
\p{So} or \p{Other_Symbol}: various symbols that are not math symbols, currency signs, or combining characters.
\p{N} or \p{Number}: any kind of numeric character in any script.
\p{Nd} or \p{Decimal_Digit_Number}: a digit zero through nine in any script except ideographic scripts.
\p{Nl} or \p{Letter_Number}: a number that looks like a letter, such as a Roman numeral.
\p{No} or \p{Other_Number}: a superscript or subscript digit, or a number that is not a digit 0–9 (excluding numbers from ideographic scripts).
\p{P} or \p{Punctuation}: any kind of punctuation character.
\p{Pd} or \p{Dash_Punctuation}: any kind of hyphen or dash.
\p{Ps} or \p{Open_Punctuation}: any kind of opening bracket.
\p{Pe} or \p{Close_Punctuation}: any kind of closing bracket.
\p{Pi} or \p{Initial_Punctuation}: any kind of opening quote.
\p{Pf} or \p{Final_Punctuation}: any kind of closing quote.
\p{Pc} or \p{Connector_Punctuation}: a punctuation character such as an underscore that connects words.
\p{Po} or \p{Other_Punctuation}: any kind of punctuation character that is not a dash, bracket, quote or connector.
\p{C} or \p{Other}: invisible control characters and unused code points.
\p{Cc} or \p{Control}: an ASCII or Latin-1 control character: 0x00–0x1F and 0x7F–0x9F.
\p{Cf} or \p{Format}: invisible formatting indicator.
\p{Co} or \p{Private_Use}: any code point reserved for private use.
\p{Cs} or \p{Surrogate}: one half of a surrogate pair in UTF-16 encoding.
\p{Cn} or \p{Unassigned}: any code point to which no character has been assigned.

\p{Common}
\p{Arabic}
\p{Armenian}
\p{Bengali}
\p{Bopomofo}
\p{Braille}
\p{Buhid}
\p{Canadian_Aboriginal}
\p{Cherokee}
\p{Cyrillic}
\p{Devanagari}
\p{Ethiopic}
\p{Georgian}
\p{Greek}
\p{Gujarati}
\p{Gurmukhi}
\p{Han}
\p{Hangul}
\p{Hanunoo}
\p{Hebrew}
\p{Hiragana}
\p{Inherited}
\p{Kannada}
\p{Katakana}
\p{Khmer}
\p{Lao}
\p{Latin}
\p{Limbu}
\p{Malayalam}
\p{Mongolian}
\p{Myanmar}
\p{Ogham}
\p{Oriya}
\p{Runic}
\p{Sinhala}
\p{Syriac}
\p{Tagalog}
\p{Tagbanwa}
\p{TaiLe}
\p{Tamil}
\p{Telugu}
\p{Thaana}
\p{Thai}
\p{Tibetan}
\p{Yi}

\p{InBasic_Latin}: U+0000–U+007F
\p{InLatin-1_Supplement}: U+0080–U+00FF
\p{InLatin_Extended-A}: U+0100–U+017F
\p{InLatin_Extended-B}: U+0180–U+024F
\p{InIPA_Extensions}: U+0250–U+02AF
\p{InSpacing_Modifier_Letters}: U+02B0–U+02FF
\p{InCombining_Diacritical_Marks}: U+0300–U+036F
\p{InGreek_and_Coptic}: U+0370–U+03FF
\p{InCyrillic}: U+0400–U+04FF
\p{InCyrillic_Supplementary}: U+0500–U+052F
\p{InArmenian}: U+0530–U+058F
\p{InHebrew}: U+0590–U+05FF
\p{InArabic}: U+0600–U+06FF
\p{InSyriac}: U+0700–U+074F
\p{InThaana}: U+0780–U+07BF
\p{InDevanagari}: U+0900–U+097F
\p{InBengali}: U+0980–U+09FF
\p{InGurmukhi}: U+0A00–U+0A7F
\p{InGujarati}: U+0A80–U+0AFF
\p{InOriya}: U+0B00–U+0B7F
\p{InTamil}: U+0B80–U+0BFF
\p{InTelugu}: U+0C00–U+0C7F
\p{InKannada}: U+0C80–U+0CFF
\p{InMalayalam}: U+0D00–U+0D7F
\p{InSinhala}: U+0D80–U+0DFF
\p{InThai}: U+0E00–U+0E7F
\p{InLao}: U+0E80–U+0EFF
\p{InTibetan}: U+0F00–U+0FFF
\p{InMyanmar}: U+1000–U+109F
\p{InGeorgian}: U+10A0–U+10FF
\p{InHangul_Jamo}: U+1100–U+11FF
\p{InEthiopic}: U+1200–U+137F
\p{InCherokee}: U+13A0–U+13FF
\p{InUnified_Canadian_Aboriginal_Syllabics}: U+1400–U+167F
\p{InOgham}: U+1680–U+169F
\p{InRunic}: U+16A0–U+16FF
\p{InTagalog}: U+1700–U+171F
\p{InHanunoo}: U+1720–U+173F
\p{InBuhid}: U+1740–U+175F
\p{InTagbanwa}: U+1760–U+177F
\p{InKhmer}: U+1780–U+17FF
\p{InMongolian}: U+1800–U+18AF
\p{InLimbu}: U+1900–U+194F
\p{InTai_Le}: U+1950–U+197F
\p{InKhmer_Symbols}: U+19E0–U+19FF
\p{InPhonetic_Extensions}: U+1D00–U+1D7F
\p{InLatin_Extended_Additional}: U+1E00–U+1EFF
\p{InGreek_Extended}: U+1F00–U+1FFF
\p{InGeneral_Punctuation}: U+2000–U+206F
\p{InSuperscripts_and_Subscripts}: U+2070–U+209F
\p{InCurrency_Symbols}: U+20A0–U+20CF
\p{InCombining_Diacritical_Marks_for_Symbols}: U+20D0–U+20FF
\p{InLetterlike_Symbols}: U+2100–U+214F
\p{InNumber_Forms}: U+2150–U+218F
\p{InArrows}: U+2190–U+21FF
\p{InMathematical_Operators}: U+2200–U+22FF
\p{InMiscellaneous_Technical}: U+2300–U+23FF
\p{InControl_Pictures}: U+2400–U+243F
\p{InOptical_Character_Recognition}: U+2440–U+245F
\p{InEnclosed_Alphanumerics}: U+2460–U+24FF
\p{InBox_Drawing}: U+2500–U+257F
\p{InBlock_Elements}: U+2580–U+259F
\p{InGeometric_Shapes}: U+25A0–U+25FF
\p{InMiscellaneous_Symbols}: U+2600–U+26FF
\p{InDingbats}: U+2700–U+27BF
\p{InMiscellaneous_Mathematical_Symbols-A}: U+27C0–U+27EF
\p{InSupplemental_Arrows-A}: U+27F0–U+27FF
\p{InBraille_Patterns}: U+2800–U+28FF
\p{InSupplemental_Arrows-B}: U+2900–U+297F
\p{InMiscellaneous_Mathematical_Symbols-B}: U+2980–U+29FF
\p{InSupplemental_Mathematical_Operators}: U+2A00–U+2AFF
\p{InMiscellaneous_Symbols_and_Arrows}: U+2B00–U+2BFF
\p{InCJK_Radicals_Supplement}: U+2E80–U+2EFF
\p{InKangxi_Radicals}: U+2F00–U+2FDF
\p{InIdeographic_Description_Characters}: U+2FF0–U+2FFF
\p{InCJK_Symbols_and_Punctuation}: U+3000–U+303F
\p{InHiragana}: U+3040–U+309F
\p{InKatakana}: U+30A0–U+30FF
\p{InBopomofo}: U+3100–U+312F
\p{InHangul_Compatibility_Jamo}: U+3130–U+318F
\p{InKanbun}: U+3190–U+319F
\p{InBopomofo_Extended}: U+31A0–U+31BF
\p{InKatakana_Phonetic_Extensions}: U+31F0–U+31FF
\p{InEnclosed_CJK_Letters_and_Months}: U+3200–U+32FF
\p{InCJK_Compatibility}: U+3300–U+33FF
\p{InCJK_Unified_Ideographs_Extension_A}: U+3400–U+4DBF
\p{InYijing_Hexagram_Symbols}: U+4DC0–U+4DFF
\p{InCJK_Unified_Ideographs}: U+4E00–U+9FFF
\p{InYi_Syllables}: U+A000–U+A48F
\p{InYi_Radicals}: U+A490–U+A4CF
\p{InHangul_Syllables}: U+AC00–U+D7AF
\p{InHigh_Surrogates}: U+D800–U+DB7F
\p{InHigh_Private_Use_Surrogates}: U+DB80–U+DBFF
\p{InLow_Surrogates}: U+DC00–U+DFFF
\p{InPrivate_Use_Area}: U+E000–U+F8FF
\p{InCJK_Compatibility_Ideographs}: U+F900–U+FAFF
\p{InAlphabetic_Presentation_Forms}: U+FB00–U+FB4F
\p{InArabic_Presentation_Forms-A}: U+FB50–U+FDFF
\p{InVariation_Selectors}: U+FE00–U+FE0F
\p{InCombining_Half_Marks}: U+FE20–U+FE2F
\p{InCJK_Compatibility_Forms}: U+FE30–U+FE4F
\p{InSmall_Form_Variants}: U+FE50–U+FE6F
\p{InArabic_Presentation_Forms-B}: U+FE70–U+FEFF
\p{InHalfwidth_and_Fullwidth_Forms}: U+FF00–U+FFEF
\p{InSpecials}: U+FFF0–U+FFFF

     */;

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
