package edu.comp90051.tutorial.week5;// edu.comp90051.tut5.TestDistinct.java
// Testing function for distinct elements counter
// awirth for COMP90056
// Aug 2017,8,9

import edu.princeton.cs.algs4.StdRandom;

public class TestDistinct {

    public static void main(String args[]) {
        int i;
        int N = Integer.parseInt(args[0]);
        boolean A[] = new boolean[N];
        int d = 0;

        // Different ways of implementing distinct element counting

        //edu.comp90051.tut5.Distinct a = new AMS(0x0fffffff,10);


        // A silly little test with 20 times the N on the input line

        for (i = 0; i < 20 * N; i++) {
            int x = StdRandom.uniform(N);
            // a.add(x);
            if (!A[x]) {
                A[x] = true;
                d++;
            }
        }

        //System.out.format("items %6d distinct %6d estimate %6.0f%n",i,d,a.distinct());

    }
}
