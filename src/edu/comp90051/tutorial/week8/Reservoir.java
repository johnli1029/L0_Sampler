package edu.comp90051.tutorial.week8;// Reservoir.java
// Reservoir sampler class
// awirth for COMP90056
// Aug 2017,8

import edu.princeton.cs.algs4.StdRandom;

public class Reservoir {
    // Stores k items from the "stream"
    private int[] A;
    private int m = 0;
    private int k;

    public Reservoir(int k) {
        this.k = k;
        A = new int[k];
    }

    public void considerItem(int x) {
        // to make things a bit simpler, focus on the case of sampling k integers from a stream
        m++;
        if (m <= k) {
            A[m - 1] = x;
        } else {
            int i = StdRandom.uniform(m);
            if (i < k) {
                A[i] = x;
            }
        }
    }


    public int report(int S[]) {
        // tell us what's inside, by copying into S
        // use the int to say how many integers are in the sample! (what if n<k?!)

        // actually, this is a very ugly C-style way of doing this.
        if (m < k) {
            System.arraycopy(A, 0, S, 0, m);
            return m;
        } else {
            System.arraycopy(A, 0, S, 0, k);
            return k;
        }
    }

    public static void main(String args[]) {
        int i;
        Reservoir r;
        int f;
        int k = 10;
        int S[] = new int[k];

        r = new Reservoir(k);
        for (i = 0; i < 200; i++) {
            r.considerItem(i);
        }
        f = r.report(S);
        for (i = 0; i < f; i++) {
            System.out.println(S[i]);
        }
    }
}
