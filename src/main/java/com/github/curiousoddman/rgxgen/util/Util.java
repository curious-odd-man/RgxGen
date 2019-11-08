package com.github.curiousoddman.rgxgen.util;

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

    public static long pow(int a, int pow) {
        long res = 1;
        for (int i = 0; i < pow; i++) {
            res *= a;
        }

        return res;
    }
}
