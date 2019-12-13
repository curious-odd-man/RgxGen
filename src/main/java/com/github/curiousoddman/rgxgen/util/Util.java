package com.github.curiousoddman.rgxgen.util;

import com.github.curiousoddman.rgxgen.generator.nodes.SymbolSet;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class Util {
    private static final String SYMBOLS = Arrays.stream(SymbolSet.getAllSymbols())
                                                .reduce("", String::concat);

    public static BigInteger factorial(long n) {
        BigInteger fact = BigInteger.ONE;
        for (long i = 2; i <= n; ++i) {
            fact = fact.multiply(BigInteger.valueOf(i));
        }
        return fact;
    }

    public static String[] stringToCharsSubstrings(String str) {
        return str.split("");
    }

    public static long pow(int a, int pow) {
        long res = 1;
        for (int i = 0; i < pow; i++) {
            res *= a;
        }

        return res;
    }

    public static <T> List<T> iteratorToList(Iterator<T> it) {
        List<T> lst = new LinkedList<>();

        while (it.hasNext()) {
            lst.add(it.next());
        }

        return lst;
    }

    public static String substringUntil(String expr, int startIndex, char c) {
        int endIndex = startIndex;
        while (true) {
            // Find ending character
            endIndex = expr.indexOf(c, endIndex);
            if (endIndex == -1) {
                throw new RuntimeException("Could not find character '" + c + "' in string: '" + expr.substring(startIndex));
            }
            int cnt = 1;        // One, because we subtract it later from endIndex. Just to avoid extra subtraction
            // Count how much backslashes there are -
            // Even number means that they all are escaped
            // Odd number means that the {@code c} is escaped
            while (expr.charAt(endIndex - cnt) == '\\') {
                ++cnt;
            }

            // because count was 1, not 0 initially we do not equal comparison
            if (cnt % 2 != 0) {
                break;
            }

            // Otherwise we will find the same {@code c} at same position
            ++endIndex;
        }

        return expr.substring(startIndex, endIndex);
    }

    public static String randomString(String value) {
        int count = Math.abs(value.hashCode() % 10);
        StringBuilder builder = new StringBuilder(count);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        while (count-- != 0) {
            builder.append(SYMBOLS.charAt(rnd.nextInt(0, SYMBOLS.length())));
        }
        return builder.toString();
    }
}
