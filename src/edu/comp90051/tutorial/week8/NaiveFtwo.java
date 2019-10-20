package edu.comp90051.tutorial.week8;// NaiveFTwo
// A space-hungry approach to calculate F_2 via a Hashmap
// awirth for COMP90056
// Sept 2019

import java.util.HashMap;

public class NaiveFtwo implements Ftwo {
    private HashMap<String, Integer> map;

    NaiveFtwo() {
        map = new HashMap<String, Integer>();
    }

    public void add(String s, int c) {
        if (map.containsKey(s)) {
            int x = map.get(s);
            map.replace(s, x + c);
        } else {
            map.put(s, c);
        }
    }

    public double ftwo() {
        double d = 0;
        for (String s : map.keySet()) {
            int c = map.get(s);
            d += c * c;
        }
        return d;
    }
}
