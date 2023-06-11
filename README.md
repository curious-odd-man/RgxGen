# Regex: generate matching and non-matching strings

This is a java library that, given a regex pattern, allows to:

1. Generate matching strings
1. Iterate through unique matching strings
1. Generate not matching strings

# Table of contents

[Status](https://github.com/curious-odd-man/RgxGen#status) <br>
[Try it now](https://github.com/curious-odd-man/RgxGen#try-it-now) <br>
[Usage](https://github.com/curious-odd-man/RgxGen#usage) <br>
[Supported Syntax](https://github.com/curious-odd-man/RgxGen#supported-syntax) <br>
[Configuration](https://github.com/curious-odd-man/RgxGen#configuration) <br>
[Limitations](https://github.com/curious-odd-man/RgxGen#limitations) <br>
[Other similar libraries](https://github.com/curious-odd-man/RgxGen#other-tools-to-generate-values-by-regex-and-why-this-might-be-better) <br>
[Support](https://github.com/curious-odd-man/RgxGen#support)

## Status

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=plastic)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.curious-odd-man/rgxgen/badge.svg?style=plastic)](https://search.maven.org/search?q=a:rgxgen)
[![javadoc](https://javadoc.io/badge2/com.github.curious-odd-man/rgxgen/javadoc.svg?style=plastic)](https://javadoc.io/doc/com.github.curious-odd-man/rgxgen)

Build status:

|                                                             Latest Release                                                             |                                                           Latest snapshot                                                           |
|:--------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------:|
|    [![Build Status](https://travis-ci.com/curious-odd-man/RgxGen.svg?branch=master)](https://travis-ci.com/curious-odd-man/RgxGen)     |    [![Build Status](https://travis-ci.com/curious-odd-man/RgxGen.svg?branch=dev)](https://travis-ci.com/curious-odd-man/RgxGen)     |
| [![codecov](https://codecov.io/gh/curious-odd-man/RgxGen/branch/master/graph/badge.svg)](https://codecov.io/gh/curious-odd-man/RgxGen) | [![codecov](https://codecov.io/gh/curious-odd-man/RgxGen/branch/dev/graph/badge.svg)](https://codecov.io/gh/curious-odd-man/RgxGen) |

## Try it now!!!

Follow the link to Online IDE with created project: [JDoodle](https://www.jdoodle.com/a/2Q6T).
Enter your pattern and see the results.

## Usage

### Maven dependency

#### The Latest RELEASE:

```xml

<dependency>
    <groupId>com.github.curious-odd-man</groupId>
    <artifactId>rgxgen</artifactId>
    <version>1.4</version>
</dependency>
```

#### The Latest SNAPSHOT:

```xml

<project>
    <repositories>
        <repository>
            <id>snapshots-repository</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        </repository>
    </repositories>

    <!--  .... -->

    <dependency>
        <groupId>com.github.curious-odd-man</groupId>
        <artifactId>rgxgen</artifactId>
        <version>2.0-SNAPSHOT</version>
    </dependency>
</project>
```

Changes in snapshot:

- Support for Character Classes `\p{...}` and `\P{...}`
  patterns. [#76](https://github.com/curious-odd-man/RgxGen/issues/73)
- API changed:
  - No global configuration properties
  - Factory methods to create instance of RgxGen - see examples
---

### Code:

```java
public class Main {
    public static void main(String[] args) {
        RgxGen rgxGen = RgxGen.parse("[^0-9]*[12]?[0-9]{1,2}[^0-9]*");       // Create generator
        String s = rgxGen.generate();                                        // Generate new random value
        Optional<BigInteger> estimation = rgxGen.getUniqueEstimation();      // The estimation (not accurate, see Limitations) how much unique values can be generated with that pattern.
        StringIterator uniqueStrings = rgxGen.iterateUnique();               // Iterate over unique values (not accurate, see Limitations)
        String notMatching = rgxGen.generateNotMatching();                   // Generate not matching string
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        RgxGen rgxGen = RgxGen.parse("[^0-9]*[12]?[0-9]{1,2}[^0-9]*");       // Create generator
        Random rnd = new Random(1234);
        String s = rgxGen.generate(rnd);                                     // Generate first value
        String s1 = rgxGen.generate(rnd);                                    // Generate second value
        String s2 = rgxGen.generate(rnd);                                    // Generate third value
        String notMatching = rgxGen.generateNotMatching(rnd);                // Generate not matching string
        // On each launch s, s1 and s2 will be the same
    }
}
```

## Supported syntax

<details>
<summary><b>Supported syntax</b></summary>

|                        Pattern | Description                                                                                                                                                                                |
|-------------------------------:|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|                            `.` | Any symbol                                                                                                                                                                                 |
|                            `?` | One or zero occurrences                                                                                                                                                                    |
|                            `+` | One or more occurrences                                                                                                                                                                    |
|                            `*` | Zero or more occurrences                                                                                                                                                                   |
|                           `\r` | Carriage return `CR` character                                                                                                                                                             |
|                           `\t` | Tab `	` character                                                                                                                                                                          |
|                           `\n` | Line feed `LF` character.                                                                                                                                                                  |
|                           `\d` | A digit. Equivalent to `[0-9]`                                                                                                                                                             |
|                           `\D` | Not a digit. Equivalent to `[^0-9]`                                                                                                                                                        |
|                           `\s` | Carriage Return, Space, Tab, Newline, Vertical Tab, Form Feed                                                                                                                              |
|                           `\S` | Anything, but Carriage Return, Space, Tab, Newline, Vertical Tab, Form Feed                                                                                                                |
|                           `\w` | Any word character. Equivalent to `[a-zA-Z0-9_]`                                                                                                                                           |
|                           `\W` | Anything but a word character. Equivalent to `[^a-zA-Z0-9_]`                                                                                                                               |
|                           `\i` | Places same value as capture group with index `i`. `i` is any integer number.                                                                                                              |
|                  `\Q` and `\E` | Any characters between `\Q` and `\E`, including metacharacters, will be treated as literals.                                                                                               |
|                  `\b` and `\B` | These characters are ignored. No validation is performed!                                                                                                                                  |
|          `\xXX` and `\x{XXXX}` | Hexadecimal value of unicode characters 2 or 4 hexadecimal digits                                                                                                                          |
|                       `\uXXXX` | Hexadecimal value of unicode characters 4 hexadecimal digits                                                                                                                               |
|                      `\p{...}` | Any character in class. See all available keys in [`com.github.curiousoddman.rgxgen.model.UnicodeCategory`](src/main/java/com/github/curiousoddman/rgxgen/model/UnicodeCategory.java).     |
|                      `\P{...}` | Any character not in class. See all available keys in [`com.github.curiousoddman.rgxgen.model.UnicodeCategory`](src/main/java/com/github/curiousoddman/rgxgen/model/UnicodeCategory.java). |
|              `{a}` and `{a,b}` | Repeat a; or min a max b times. Use {n,} to repeat at least n times.                                                                                                                       |
|                        `[...]` | Single character from ones that are inside brackets. `[a-zA-Z]` (dash) also supported                                                                                                      |
|                       `[^...]` | Single character except the ones in brackets. `[^a]` - any symbol except 'a'                                                                                                               |
|                           `()` | To group multiple characters for the repetitions                                                                                                                                           |
| `foo(?=bar)` and `(?<=foo)bar` | Positive lookahead and lookbehind. These are equivalent to `foobar`                                                                                                                        |
| `foo(?!bar)` and `(?<!foo)bar` | Negative lookahead and lookbehind.                                                                                                                                                         |
|        <code>(a&#124;b)</code> | Alternatives                                                                                                                                                                               |
|                             \\ | Escape character (use \\\\ (double backslash) to generate single \ character)                                                                                                              |

RgxGen treats any other characters as literals - those are generated as is.

</details>

## Configuration

RgxGen can be configured per instance.

Please refer to the following enum for all available
properties: [`com.github.curiousoddman.rgxgen.config.RgxGenOption`](src/main/java/com/github/curiousoddman/rgxgen/config/RgxGenOption.java).

### Create and Use Configuration Properties

Use `new RgxGenProperties()` to create properties object.

<details>
<summary><b>Code example</b></summary>

```java
public class Main {
    public static void main(String[] args) {
        // Create properties object (RgxGenProperties extends java.util.Properties)
        RgxGenProperties properties = new RgxGenProperties();
        // Set value "20" for INFINITE_PATTERN_REPETITION option in properties
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(properties, 20);
        // ... now properties can be passed to RgxGen
        RgxGen rgxGen_3 = RgxGen.parse(properties, "my-cool-pattern");
    }
}
```

</details>

## Limitations

### Lookahead and Lookbehind

Currently, these two have very limited support. Please refer
to [#63](https://github.com/curious-odd-man/RgxGen/issues/63).
I'm currently working on the solution, but I cannot say when I come up with something.

### Estimation

`rgxGen.getUniqueEstimation()` - might not be accurate, because it does not count actual unique values, but only counts
different states of each building block of the expression.
For example: `"(a{0,2}|b{0,2})"`  will be estimated as 6, though actual number of unique values is 5.
That is because left and right alternative can produce same value.
At the same time `"(|(a{1,2}|b{1,2}))"` will be correctly estimated to 5, though it will generate same values.

### Uniqueness

For the similar reasons as with estimations - requested unique values iterator can contain duplicates.

### Infinite patterns

By design `a+`, `a*` and `a{n,}` patterns in regex imply infinite number of characters should be matched.
When generating data, that would mean values of infinite length might be generated.
It is highly doubtful anyone would require a string of infinite length, thus I've artificially limited repetitions in
such patterns to 100 symbols, when generating random values.
This value can be changed - please refer to [configuration](https://github.com/curious-odd-man/RgxGen#configuration)
section.

On the contrast, when generating **unique values** - the number of maximum repetitions is Integer.MAX_VALUE.

Use `a{n,m}` if you require some specific number of repetitions.
It is suggested to avoid using such infinite patterns to generate data based on regex.

### Not matching values generation

The general rule is - I am trying to generate not matching strings of same length as would be matching strings, though
it is not always possible.
For example pattern `.` - any symbol - would yield empty string as not matching string.
Another example `a{0,2}` - for this pattern not matching string would be an empty string, but I would only generate
the resulting strings of 1 or 2 symbols long.
I chose these approaches because they seem predictable and easier to implement.

#### Which values are used in non-matching generation

Whenever non-matching result is requested, with either `new RgxGen(".").generateNotMatching()` method or with pattern,
like `"[^a-z]"` - there is a choice in generator which are characters that do not match mentioned characters.
For example - for `"[^a-z]"` - any unicode character except the ones in a range `a-z` would be ok. Though that would
include non-printable, all kinds of blank characters and all the different wierd unicode characters. I expect that this
might not be expected behavior. Thus, I have defined 2 different universe ranges of characters that are used - one for
the ASCII only characters and another - for unicode characters.

These ranges are defined here:

- ASCII: `ASCII_SYMBOL_RANGE` constant
  in [`com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.java`](src/main/java/com/github/curiousoddman/rgxgen/parsing/dflt/ConstantsProvider.java)
- Unicode: `UNICODE_SYMBOL_RANGE` constant
  in [`com.github.curiousoddman.rgxgen.parsing.dflt.ConstantsProvider.java`](src/main/java/com/github/curiousoddman/rgxgen/parsing/dflt/ConstantsProvider.java)

`UNICODE_SYMBOL_RANGE` is currently used ONLY when Character Classes are used `\p{}` ir `\P{}` patterns.
By default `ASCII_SYMBOL_RANGE` is used.

To generate not matching characters I take one of the aforementioned constant ranges and subtract characters provided in
pattern - resulting range is the one that is used for non-matching generation.
For example for pattern `"[^a-z]"` `ASCII_SYMBOL_RANGE` will be used as a universe. 
The result then will be `ASCII_SYMBOL_RANGE` except `A-z` = `space - @` union `{ - ~`

### Unicode Categories

I've used this page as a basis for the unicode categories
information: https://www.regular-expressions.info/unicode.html#category.
Though only for a few language related patterns I was able to find exact list/range of code points that belongs to
specific categories.
To overcome this I used Java `Pattern.compile()` to split characters into categories.
Unfortunately there were several character categories that are not supported by Java `Pattern.compile()` as a result
these are missing.

If you need to know which category may produce which values - please refer
to [`com.github.curiousoddman.rgxgen.model.UnicodeCategory`](src/main/java/com/github/curiousoddman/rgxgen/model/UnicodeCategory.java)
Each there is list of ranges and list of characters - the RgxGen will generate any character within any of ranges (
including first and last) or any character from the list.

## Other tools to generate values by regex and why this might be better

There are 2 more libraries available to achieve same goal:

1. https://github.com/mifmif/Generex
1. http://code.google.com/p/xeger

Though I found they have the following issues:

1. All of them build graph which can easily produce OOM exception. For example pattern `a{60000}`,
   or [IPV6 regex pattern](https://stackoverflow.com/questions/53497/regular-expression-that-matches-valid-ipv6-addresses).
1. Alternatives - only 2 alternatives gives equal probability of each alternative to appear in generated values. For
   example: `(a|b)` the probability of a and b is equal. For `(a|b|c)` it would be expected to have a or b or c with
   probability 33.(3)% each. Though really the probabilities are a=50%, and b=25% and c=25% each. For longer
   alternatives you might never get the last alternative.
1. They are quite slow
1. Lightweight. This library does not have any dependencies.

## Support

I plan to support this library, so you're welcome to open issues or reach me by e-mail in case of any questions.
Any suggestions, feature requests or bug reports are welcome!

Please vote up my answer on [StackOverflow](https://stackoverflow.com/a/58813696/4174003) to help others find this
library.
