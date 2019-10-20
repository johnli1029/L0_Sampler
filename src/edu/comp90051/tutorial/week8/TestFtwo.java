package edu.comp90051.tutorial.week8;// TestFtwo.java
// Testing function for F2 estimator
// awirth for COMP90056
// Aug 2018, Sept 2019

import java.io.*;

public class TestFtwo{
	
	public static void main(String args[]){
		
		if (args.length != 1){
		    System.err.println("Should be only one argument, the filename.\n");
		    System.exit(1);
		}
		String filename = args[0]; // argument is filename.
		Ftwo f = new NaiveFtwo(); // initial naive approach-- replace with
					  // your own

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;
		    while ((line = br.readLine()) != null) {
                        String [] words = line.split(" ");//separate with ' '
                        int c = Integer.parseInt(words[1]);
                        f.add(words[0],c);
                        //System.out.println("This is the line: " + line);
		    }
		    System.out.println(f.ftwo());
		}
		catch(FileNotFoundException e){
		    System.err.println("File '" + filename + "' not found.\n");
		    System.exit(1);
		}
		catch(IOException e){
			System.err.println("File '" + filename + "' has IO issues.\n");
		    System.exit(1);
		}
	

	}
}
