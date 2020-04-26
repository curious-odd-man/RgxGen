# Java library for text generation based on regular expression pattern

## Status

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Build status:

| Master Branch   | Dev branch  |
| :---------: | :---------: |
| [![Build Status](https://travis-ci.com/curious-odd-man/RgxGen.svg?branch=master)](https://travis-ci.com/curious-odd-man/RgxGen) | [![Build Status](https://travis-ci.com/curious-odd-man/RgxGen.svg?branch=dev)](https://travis-ci.com/curious-odd-man/RgxGen) |
| [![codecov](https://codecov.io/gh/curious-odd-man/RgxGen/branch/master/graph/badge.svg)](https://codecov.io/gh/curious-odd-man/RgxGen) | [![codecov](https://codecov.io/gh/curious-odd-man/RgxGen/branch/dev/graph/badge.svg)](https://codecov.io/gh/curious-odd-man/RgxGen) |

## Try it now!!!

Follow the link to Online IDE with already created simple project: [JDoodle](https://www.jdoodle.com/a/1NCw)

Note: 1.0 version is imported there - unfortunately currently I can't change it to latest (1.1).

Luckly there is no big difference between 1.0 and 1.1

Enter your pattern and see the results.


## Usage

### Maven dependency

#### latest RELEASE:
```xml
<dependency>
    <groupId>com.github.curious-odd-man</groupId>
    <artifactId>rgxgen</artifactId>
    <version>1.1</version>
</dependency>
```
#### latest SNAPSHOT:
```xml
<repositories>
    <repository>
        <id>snapshots-repository</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
</repositories>

// ....

<dependency>
    <groupId>com.github.curious-odd-man</groupId>
    <artifactId>rgxgen</artifactId>
    <version>1.1-SNAPSHOT</version>
</dependency>
```

### Code: 
```java
RgxGen rgxGen = new RgxGen("[^0-9]*[12]?[0-9]{1,2}[^0-9]*");         // Create generator
String s = rgxGen.generate();                                        // Generate new random value
BigInteger estimation = rgxGen.numUnique();                          // The estimation (not accurate, see Limitations) how much unique values can be generated with that pattern.
StringIterator uniqueStrings = rgxGen.iterateUnique();               // Iterate over unique values (not accurate, see Limitations)
```

```java
RgxGen rgxGen = new RgxGen("[^0-9]*[12]?[0-9]{1,2}[^0-9]*");         // Create generator
Random rnd = new Random(1234)
String s = rgxGen.generate(rnd);                                     // Generate first value
String s1 = rgxGen.generate(rnd);                                    // Generate second value
String s2 = rgxGen.generate(rnd);                                    // Generate third value
// On each launch s, s1 and s2 will be the same
```

## Supported syntax

<details>
<summary><b>Latest RELEASE</b></summary>

| Pattern   | Description  |
| ---------: |-------------|
| `.`  | Any symbol |
| `?`  | One or zero occurrences |
| `+`  | One or more occurrences |
| `*`  | Zero or more occurrences |
| `\d`  | A digit. Equivalent to `[0-9]` |
| `\D`  | Not a digit. Equivalent to `[^0-9]` |
| `\s`  | Carriage Return, Space, Tab, Newline, Vertical Tab, Form Feed |
| `\S`  | Anything, but Carriage Return, Space, Tab, Newline, Vertical Tab, Form Feed |
| `\w`  | Any word character. Equivalent to `[a-zA-Z0-9_]` |
| `\W`  | Anything but a word character. Equivalent to `[^a-zA-Z0-9_]` |
| `\i`  | Places same value as capture group with index `i`. `i` is any integer number.  |
| `\xXX` and `\x{XXXX}`  | Hexadecimal value of unicode characters 2 or 4 digits |
| `{a}` and `{a,b}`  | Repeat a; or min a max b times. Use {n,} to repeat at least n times. |
| `[...]`  | Single character from ones that are inside brackets. `[a-zA-Z]` (dash) also supported |
| `[^...]`  | Single character except the ones in brackets. `[^a]` - any symbol except 'a' |
| `()`  | To group multiple characters for the repetitions |
| `foo(?=bar)` and `(?<=foo)bar`  | Positive lookahead and lookbehind. These are equivalent to `foobar` |
| `foo(?!bar)` and `(?<!foo)bar`  | Negative lookahead and lookbehind. |
| <code>(a&#124;b)</code> |  Alternatives  |
| \\  | Escape character (use \\\\ (double backslash) to generate single \ character) |

Any other character are treated as simple characters and are generated as is, thought allowed to escape them.

</details>

## Limitations

### Estimation
`rgxGen.numUnique()` - might not be accurate, because it does not count actual unique values, but only counts different states of each building block of the expression.
For example: `"(a{0,2}|b{0,2})"`  will be estimated as 6, though actual number of unique values is 5. 
That is because left and right alternative can produce same value.
At the same time `"(|(a{1,2}|b{1,2}))"` will be correctly estimated to 5, though it will generate same values.

### Uniqueness

For the similar reasons as with estimations - requested unique values iterator can contain duplicates. 

### Infinite patterns

By design `a+`, `a*` and `a{n,}` patterns in regex imply infinite number of characters should be matched.
When generating data that would mean that values of infinite length might be generated.
It is highly doubtful that anyone would require a string of infinite length, thus I've artificially limited repetitions in such patterns to 100 symbols, when generating random values.

On the contrast, when generating **unique values** - the number of maximum repetitions is increased to Integer.MAX_VALUE.

Use `a{n,m}` if you require some specific number of repetitions.
It is suggested to avoid using such infinite patterns to generate data based on regex.

## Other tools to generate values by regex and why this might be better

There are 2 more libraries available to achieve same goal:
1. https://github.com/mifmif/Generex
1. http://code.google.com/p/xeger

Though I found they have following issues:
1. All of them build graph which can easily produce OOM exception. For example pattern `a{60000}`, or [IPV6 regex pattern](https://stackoverflow.com/questions/53497/regular-expression-that-matches-valid-ipv6-addresses)
1. For alternatives - only 2 alternatives gives equal probability of each alternative to appear in generated values. For example: `(a|b)` the probability of a and b is equal. For `(a|b|c)` it would be expected to have a or b or c with probability 33.(3)% each. Though really the probabilities are a=50%, and b=25% and c=25% each. For longer alternatives you might never get the last alternative.
1. They are quite slow

## Support

I plan to support this library, so you're welcome to open issues or reach me by e-mail in case of any questions.
Any suggestions, feature requests or bug reports are welcome!
