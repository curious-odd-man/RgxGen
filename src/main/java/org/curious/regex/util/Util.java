package org.curious.regex.util;

import java.math.BigInteger;

public final class Util {
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
}
