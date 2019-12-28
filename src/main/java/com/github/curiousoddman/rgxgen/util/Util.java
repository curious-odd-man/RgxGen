package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.generator.nodes.SymbolSet;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Utility methods collection
 */
public final class Util {
    private static final String SYMBOLS = Arrays.stream(SymbolSet.getAllSymbols())
                                                .reduce("", String::concat);

    private static final Pattern EMPTY = Pattern.compile("");

    /**
     * Splits string into array of single-character strings
     *
     * @param str string to split
     * @return array of single-character strings
     */
    public static String[] stringToCharsSubstrings(String str) {
        return EMPTY.split(str);
    }

    /**
     * Creates random string up to 10 symbols long
     *
     * @param value seed used to select length
     * @return random string up to 10 symbols long
     */
    public static String randomString(String value) {
        int count = Math.abs(value.hashCode() % 10);
        StringBuilder builder = new StringBuilder(count);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        while (count >= 0) {
            builder.append(SYMBOLS.charAt(rnd.nextInt(SYMBOLS.length())));
            --count;
        }
        return builder.toString();
    }

    /**
     * Utility class can't be instantiated
     */
    private Util() {
    }
}
