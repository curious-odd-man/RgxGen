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

| Latest Release   | Latest snapshot  |
| :---------: | :---------: |
| [![Build Status](https://travis-ci.com/curious-odd-man/RgxGen.svg?branch=master)](https://travis-ci.com/curious-odd-man/RgxGen) | [![Build Status](https://travis-ci.com/curious-odd-man/RgxGen.svg?branch=dev)](https://travis-ci.com/curious-odd-man/RgxGen) |
| [![codecov](https://codecov.io/gh/curious-odd-man/RgxGen/branch/master/graph/badge.svg)](https://codecov.io/gh/curious-odd-man/RgxGen) | [![codecov](https://codecov.io/gh/curious-odd-man/RgxGen/branch/dev/graph/badge.svg)](https://codecov.io/gh/curious-odd-man/RgxGen) |

## Try it now!!!

Follow the link to Online IDE with already created simple project: [JDoodle](https://www.jdoodle.com/a/2fPm).
Enter your pattern and see the results.

## Usage

### Maven dependency

#### The Latest RELEASE:
```xml
<dependency>
    <groupId>com.github.curious-odd-man</groupId>
    <artifactId>rgxgen</artifactId>
    <version>1.2</version>
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
        <version>1.3-SNAPSHOT</version>
    </dependency>
</project>
```

Changes in sd snapshot:

- Fixed: Added support for `\Q` and `\E` sequences [#43](https://github.com/curious-odd-man/RgxGen/issues/43)
- Feature: Implemented controllable number of repetitions for infinite patterns [#45](https://github.com/curious-odd-man/RgxGen/issues/45)
- Feature: Case Insensitive generation support [#47](https://github.com/curious-odd-man/RgxGen/issues/47)

### Code: 
```java
public class Main {
    public static void main(String[] args){
        RgxGen rgxGen = new RgxGen("[^0-9]*[12]?[0-9]{1,2}[^0-9]*");         // Create generator
        String s = rgxGen.generate();                                        // Generate new random value
        BigInteger estimation = rgxGen.numUnique();                          // The estimation (not accurate, see Limitations) how much unique values can be generated with that pattern.
        StringIterator uniqueStrings = rgxGen.iterateUnique();               // Iterate over unique values (not accurate, see Limitations)
        String notMatching = rgxGen.generateNotMatching();                   // Generate not matching string
    }
}
```

```java
public class Main {
    public static void main(String[] args){
        RgxGen rgxGen = new RgxGen("[^0-9]*[12]?[0-9]{1,2}[^0-9]*");         // Create generator
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
| `\Q` and `\E`  | Any characters between `\Q` and `\E`, including metacharacters, will be treated as literals.  |
| `\xXX` and `\x{XXXX}`  | Hexadecimal value of unicode characters 2 or 4 digits |
| `{a}` and `{a,b}`  | Repeat a; or min a max b times. Use {n,} to repeat at least n times. |
| `[...]`  | Single character from ones that are inside brackets. `[a-zA-Z]` (dash) also supported |
| `[^...]`  | Single character except the ones in brackets. `[^a]` - any symbol except 'a' |
| `()`  | To group multiple characters for the repetitions |
| `foo(?=bar)` and `(?<=foo)bar`  | Positive lookahead and lookbehind. These are equivalent to `foobar` |
| `foo(?!bar)` and `(?<!foo)bar`  | Negative lookahead and lookbehind. |
| <code>(a&#124;b)</code> |  Alternatives  |
| \\  | Escape character (use \\\\ (double backslash) to generate single \ character) |

RgxGen treats any other characters as literals and are generated as is, thought allowed to escape them.

</details>

## Configuration

RgxGen can be configured on global or instance level.

Please refer to the following enum for all available properties: [`com.github.curiousoddman.rgxgen.config.RgxGenOption`](src/main/java/com/github/curiousoddman/rgxgen/config/RgxGenOption.java).

Each property value will be looked up in this order:

1. Local RgxGen instance config
2. Global RgxGen config
3. Default values


### Create Configuration

Use `new RgxGenProperties()` to create properties object.
`RgxGenProperties` extends `java.util.Properties` and can be used in all the same ways.

<details>
<summary><b>Code</b></summary>

```java
public class Main {
    public static void main(String[] args){
        // Create properties object (RgxGenProperties extends java.util.Properties)
        RgxGenProperties properties = new RgxGenProperties();
        // Set value "20" for INFINITE_PATTERN_REPETITION option in properties
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(properties, 20);
        // ... now properties can be passed to RgxGen
    }
}
```
</details>

### Set Global Configuration

Set a global configuration using `RgxGen.setDefaultProperties(properties);`

<details>
<summary><b>Code</b></summary>

```java
public class Main {
    public static void main(String[] args){
        RgxGenProperties properties = createAndConfigureProperitesObject();
    
        RgxGen rgxGen_1 = new RgxGen("xxx");        // Created for example purposes
        // Set default properties. 
        // NOTE! only instances created after setDefaultProperties are affected.
        // e.g. rgxGen_1 will have default value of INFINITE_PATTERN_REPETITION option
        // and rgxGen_2 will have value "20" for the property, unless local config specified.
        RgxGen.setDefaultProperties(properties);
        RgxGen rgxGen_2 = new RgxGen("xxx");
    }
}
```
</details>

### Set Local Configuration

Set a local configuration using `rgxGen.setProperties(localProperties);` on existing RgxGen instance.

<details>
<summary><b>Code</b></summary>

```java
public class Main {
    public static void main(String[] args){
        RgxGenProperties properties = createAndConfigureProperitesObject();
        RgxGen.setDefaultProperties(properties);
    
        // Create properties object (RgxGenProperties extends java.util.Properties)
        RgxGenProperties localProperties = createAndConfigureLocalProperitesObject();
        RgxGen rgxGen_3 = new RgxGen("xxx"); 
        // Set local configuration for rgxGen_3
        // Note, for options that are not defined in localProperties, will try find option inside properties, since these are set globally prior creation of rgxGen_3 instance creation 
        rgxGen_3.setProperties(localProperties);
    }
}
```
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
When generating data that would mean values of infinite length might be generated.
It is highly doubtful anyone would require a string of infinite length, thus I've artificially limited repetitions in such patterns to 100 symbols, when generating random values.
This value can be changed - please refer to [configuration](https://github.com/curious-odd-man/RgxGen#configuration) section.

On the contrast, when generating **unique values** - the number of maximum repetitions is Integer.MAX_VALUE.

Use `a{n,m}` if you require some specific number of repetitions.
It is suggested to avoid using such infinite patterns to generate data based on regex.

### Not matching values generation

The general rule is - I am trying to generate not matching strings of same length as would be matching strings, though it is not always possible.
For example pattern `.` - any symbol - would yield empty string as not matching string. 
Another example `a{0,2}` - this pattern could yield empty string, but for not matching string the resulting strings would be only 1 or 2 symbols long.
I chose these approaches because they seem predictable and easier to implement.

## Other tools to generate values by regex and why this might be better

There are 2 more libraries available to achieve same goal:
1. https://github.com/mifmif/Generex
1. http://code.google.com/p/xeger

Though I found they have following issues:
1. All of them build graph which can easily produce OOM exception. For example pattern `a{60000}`, or [IPV6 regex pattern](https://stackoverflow.com/questions/53497/regular-expression-that-matches-valid-ipv6-addresses).
1. Alternatives - only 2 alternatives gives equal probability of each alternative to appear in generated values. For example: `(a|b)` the probability of a and b is equal. For `(a|b|c)` it would be expected to have a or b or c with probability 33.(3)% each. Though really the probabilities are a=50%, and b=25% and c=25% each. For longer alternatives you might never get the last alternative.
1. They are quite slow

## Support

I plan to support this library, so you're welcome to open issues or reach me by e-mail in case of any questions.
Any suggestions, feature requests or bug reports are welcome!

Please vote up my answer on [StackOverflow](https://stackoverflow.com/a/58813696/4174003) to help others find this library.
