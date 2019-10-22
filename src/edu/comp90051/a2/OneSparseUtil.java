package edu.comp90051.a2;

public class OneSparseUtil {

    public static long[] update(int item, int delta, long w1, long w2, long w3, long q, long p) {
        w1 += delta;
        w2 += item * delta;
        if (p == FastHash.MERSENNE_PRIME31)
            w3 = FastHash.mod31(w3 + FastHash.mod31(largePower31(q, item) * delta));
        else if (p == FastHash.MERSENNE_PRIME17)
            w3 = FastHash.mod17(w3 + FastHash.mod17(largePower17(q, item) * delta));
        else
            throw new IllegalArgumentException("P");

        return new long[]{w1, w2, w3};
    }

    private static long largePower31(long base, int exp) {
        long result = 1;
        while (exp-- > 0) {
            result = FastHash.mod31(result * base);
        }

        return result;
    }

    private static long largePower17(long base, int exp) {
        assert exp >= 0;

        long result = 1;
        while (exp-- > 0) {
            result = FastHash.mod17(result * base);
        }

        return result;
    }

    public static long[] turnstileUpdate(int item, int delta, long w1, long w2, long w3) {
        w1 += delta;
        w2 += item * delta;
        w3 = FastHash.mod61(w3 + FastHash.mod61((long) item * item * delta));

        return new long[]{w1, w2, w3};
    }

    public static boolean fingerprintTest(long w1, long w2, long w3, long q, long p) {
//            StdOut.println("w1 " + w1);
//            StdOut.println("w2 " + w2);
//            StdOut.println("w3 " + w3);

        if (p == FastHash.MERSENNE_PRIME31)
            return w1 != 0 && w2 % w1 == 0 && FastHash.mod31(w1 * largePower31(q, (int) (w2 / w1))) == w3;
        else if (p == FastHash.MERSENNE_PRIME17)
            return w1 != 0 && w2 % w1 == 0 && FastHash.mod17(w1 * largePower17(q, (int) (w2 / w1))) == w3;
        else
            throw new IllegalArgumentException("P");
    }

    public static boolean ganuglyTest(long w1, long w2, long w3) {
//        System.out.println(w1);
//        System.out.println(w2);
//        System.out.println(w3);
        return w1 != 0 && FastHash.mod61(w2 / w1 * w2) == w3;
    }


}
