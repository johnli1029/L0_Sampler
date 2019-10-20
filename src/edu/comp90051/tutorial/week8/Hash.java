package edu.comp90051.tutorial.week8;// Hash.java
// Hash class
// awirth for COMP90056
// Aug 2017, Sep 2018 -- version 2

import edu.princeton.cs.algs4.StdRandom;

public class Hash {
    private int p = 1073741789; //smaller than 2^30
    private int a, b;
    private int domain, range;

    public Hash(int domain, int range) {
        a = StdRandom.uniform(p - 1) + 1;
        b = StdRandom.uniform(p);
        this.domain = domain;
        this.range = range;
        //System.out.format("a %16d b %12d p %12d %n", a,b,p);
    }

    public int hash(Object key) {
        int x = key.hashCode() & 0x0fffffff;
        long prod = (long) a * (long) x;
        prod += b;
        long y = prod % (long) p;
        int r = (int) y % range;
        //System.out.format("x %12d y %12d r %12d %n", x,y,r);
        return r;
    }

}
