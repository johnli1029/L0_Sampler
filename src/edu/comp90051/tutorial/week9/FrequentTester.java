package edu.comp90051.tutorial.week9;//FrequentTester.java
//Testing the behaviour of Frequent Item sketches: so far, primed
//for Misra Gries
// awirth for COMP90056
// Aug 2017


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class FrequentTester {

    public static void main(String args[]) {

        String fileName = args[0];
        int k = Integer.parseInt(args[1]);

// for students to fill in

        Map<Object, Integer> check = new HashMap<Object, Integer>();

        try {
            File f = new File(fileName);
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextInt()) {
                int x = scanner.nextInt();
                // students fill in
            }
            scanner.close();

            // students fill in

            // second pass to check whether the sketch correctly identified
            // frequent items

            f = new File(fileName);
            scanner = new Scanner(f);
            while (scanner.hasNextInt()) {
                int x = scanner.nextInt();
                // students fill in
                // total++;
            }

            //      This might be useful
            //	System.out.format("False positive: %8d wasn't a 1/%4d player%n", x,k);
            //	System.out.format("Success: %8d IS a 1/%4d player%n", x,k);
        } catch (FileNotFoundException ex) {
            System.err.println("No file: " + fileName);

        }
    }
}
