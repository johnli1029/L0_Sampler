package edu.comp90051.a2;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class FastHash extends T_Wise_Hash {
    public static long MERSENNE_PRIME17 = 0x1FFFF;
    public static long MERSENNE_PRIME31 = 0x7FFFFFFF;
    public static long MERSENNE_PRIME61 = new BigInteger("1000000000000000", 16).longValue() - 1;

    private int shift;

    public FastHash(int t, long p, long range) {
        super(t, p, range);
        if (p == MERSENNE_PRIME17)
            shift = 17;
        else if (p == MERSENNE_PRIME31)
            shift = 31;
        else if (p == MERSENNE_PRIME61)
            shift = 61;
        else
            throw new IllegalArgumentException("Invalid Prime Number p");
    }

    public long hash(Object key) {
        long x = Integer.toUnsignedLong(key.hashCode());
        long x_k = x;
        long prod = a[0];   // Sum of polynomial
        for (int i = 1; i < a.length; i++) {
            prod += fastMod(a[i] * x_k);
            prod = fastMod(prod);
            x_k = fastMod(x_k * x);
        }
        return prod % range;
    }

    private long fastMod(long x) {
        x = (x >> shift) + (x & p);
        x = (x >> shift) + (x & p);
        return x;
    }

    public static long mod17(long x) {
        x = (x >> 17) + (x & MERSENNE_PRIME17);
        x = (x >> 17) + (x & MERSENNE_PRIME17);
        return x;
    }

    public static long mod31(long x) {
        x = (x >> 31) + (x & MERSENNE_PRIME31);
        x = (x >> 31) + (x & MERSENNE_PRIME31);
        return x;
    }

    public static long mod61(long x) {
        x = (x >> 61) + (x & MERSENNE_PRIME61);
        x = (x >> 61) + (x & MERSENNE_PRIME61);
        return x;
    }

    public static void main(String[] args) {
//        Map<String, Integer> counter = new HashMap<>();
//        for (int i = 0; i < 1000000; i++) {
//            FastHash tHash = new FastHash(4, FastHash.MERSENNE_PRIME31, 10);
//            String key = Long.toString(tHash.hash(25));
//            counter.put(key, counter.getOrDefault(key, 0) + 1);
//        }
//        StdOut.println(MapUtil.sortByValue(counter));

        System.out.println(mod31(-2));
    }
}
