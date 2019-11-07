# Text generator based on regular expression pattern

## Status

[![Build Status](https://travis-ci.com/curious-odd-man/RgxGen.svg?branch=master)](https://travis-ci.com/curious-odd-man/RgxGen)

## Usage

Maven dependency:
```
    TBD
```

Code: 
```
RgxGen rgxGen = new RgxGen(aRegex);                 // Create generator
String s = rgxGen.generate();                       // Generate new random value
BigInteger estimation = rgxGen.numUnique();         // The estimation (not accurate, see Limitations) how much unique values can be generated with that pattern.
Stream<String> uniqueStrings = rgxGen.uStream();    // Stream unique values
```

## Supported syntax

```
. - any symbol
? - one or zero occurrences
\+ - one or more occurrences
\* - zero or more occurrences
{2} and {1,2} - repeatitions. NOTE {1,} not supported yet
[] - single character from ones that are inside brackets. [a-zA-Z] (dash) also supported
() - to group multiple characters for the repetitions
(a|b) - alternatives 
escape character \ - to escape special characters (use \\ to generate single \ character)
```

Any other character are treated as simple characters and are generated as is.

## Limitations

### Estimation
rgxGen.numUnique() - might not be accurate, because it does not count actual unique values, but only counts different states of each building block of the expression.
For example: `"(a{0,2}|b{0,2})"`  will be estimated as 6, though actual number of unique values is 5. 
That is because left and right alternative can produce same value.
At the same time `"(|(a{1,2}|b{1,2}))"` will be correctly estimated to 5, though it will generate same values.

### Uniqueness

For the similar reasons as with estimations - requested unique values stream can contain duplicates. 
Despite `distinct()` method is called on a stream, before returining it, it does not guarantee uniquenes, as well.

### Infinite patterns

By design `a+`, `a*` and `a{n,}` patterns in regex imply infinite number of characters should be matched.
When generating data that would mean that values of infinite length might be generated.
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