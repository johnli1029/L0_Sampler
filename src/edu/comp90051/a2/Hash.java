package edu.comp90051.a2;

/*
 * Name: Haiyue Li
 * username: haiyuel
 * Student No.: 946453
 */

public class Hash {
    private int p = 1073741789; //smaller than 2^30
    private int a, b;        // only use for hash tables < 24593 in size

    public Hash() {
        a = StdRandom.uniform(p - 1) + 1;
        b = StdRandom.uniform(p); // changed from p-1
        System.out.format("a %16d b %12d p %12d %n", a, b, p);
    }

    private int h2u(long x, int range) {
        long prod = (long) a * x;
        prod += (long) b;
        long y = prod % (long) p;
        //System.out.format("x %12d y %12d r %12d %n", x,y,r);
        return (int) y % range;
    }

    public int h_basic(Object key, int domain) {
        // domain should be something like 0x0fffffff
        long key_int = Integer.toUnsignedLong(key.hashCode());
        return h2u(key_int, domain);
    }

    public static void main(String[] args) {
        Hash h = new Hash();

        System.out.println(h.h_basic(0x3FFFFFFF, 0x7FFFFFFF));
    }


}
