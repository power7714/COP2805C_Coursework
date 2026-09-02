package cop2805;

/*
 * ReadFile.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* This class reads a list of Doubles in String format
 * from a text file, casts those Strings as Doubles and
 * adds them to a list. That list is sorted and written
 * to a new text file. The list is output to the console
 * to confirm read accuracy.
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ReadFile {
	
	//Reads list of String numerical values from "data.txt",
	//converts the String numerical values to Double and adds
	//them to an ArrayList
	public static List<Double> ReadFile(String file) {
        List<Double> data = new ArrayList<>();
        
        try {
            // Read all lines from the file
            List<String> lines = Files.readAllLines(Paths.get(file));
            
            // Here we convert each line to a Double and add it to the list
            for (String line : lines) {
                // Here we trim the white space before parsing the double
            	// and adding to the list
                if (!line.trim().isEmpty()) {
                    data.add(Double.parseDouble(line.trim()));
                }
            }
            
            String log = String.format("Successfully read " + data.size() + " values from " + file);
            printMessage(log, false);
            
        } catch (IOException e) {
            String errorText = getStackTraceAsString(e);
            printMessage(errorText, true);
        } catch (NumberFormatException e) {
            String errorText = getStackTraceAsString(e);
            printMessage(errorText, true);
        }
        
        return data;
    }
    
    //Writes the Double values from the ArrayList to a new
	//text file after being sorted with Collections.sort
	//Note: Collections.sort is done in the main function
    public static void WriteFile(List<Double> data, String file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write each Double value to the file, one per line
            for (Double value : data) {
                writer.println(value);
            }
            
            String log = String.format("Successfully wrote " + data.size() + " values to " + file);
            printMessage(log, false);
            
        } catch (IOException e) {
            String errorText = getStackTraceAsString(e);
            printMessage(errorText, true);
        }
    }
    
    // ====================== Helper Methods =====================================
	
	//Helper method for printing to the console without having to write
	//"System.out.println" multiple times. This method is normally in my
	//custom helper class that I have but for the sake of this class, I
	//have included it here
	public static void printMessage(String message, boolean isError) 
	{
        if (isError) {
            System.err.println(message);
        } else {
            System.out.println(message);
        }
    }
	
	//Helper method from my custom helper class that converts
	//a StackTrace to a string to be output to the console.
	//Placed here for the sake of the course.
	public static String getStackTraceAsString(Exception e) 
	{
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    return sw.toString();
	}
	
	//Helper method created to verify that the list was
	//sorted in ascending order and written correctly to
	//the new data-sorted.txt file. This was added just
	//for good measure
	public static void verifyResults(String file) {
        try {
            // Read all lines from the sorted file
            List<String> lines = Files.readAllLines(Paths.get(file));
            
            // Track if data is sorted
            boolean isSorted = true;
            Double previousValue = null;
            
            // Print each line and verify ascending order
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    printMessage(line, false);
                    
                    // Parse and compare to verify sorting
                    Double currentValue = Double.parseDouble(line.trim());
                    if (previousValue != null && currentValue < previousValue) {
                        isSorted = false;
                    }
                    previousValue = currentValue;
                }
            }
            
            // Print verification message
            if (isSorted) {
                printMessage("Data successfully sorted", false);
            } else {
                printMessage("Error: Data is not properly sorted", true);
            }
            
        } catch (IOException e) {
        	String errorIOE = getStackTraceAsString(e);
            printMessage(errorIOE, true);
        } catch (NumberFormatException e) {
        	String errorNFE = getStackTraceAsString(e);
            printMessage(errorNFE, true);
        }
    }
	
	public static void main(String[] args)
	{
		//Assignment Tasks
		// 1. Read the data from the data.txt
        List<Double> data = ReadFile("data.txt");
        
        // 2. Sort the data in ascending order
        Collections.sort(data);
        
        // 3. Write the sorted data to the output file
        WriteFile(data, "data-sorted.txt");
        
        //Verify sort and write for good measure
        verifyResults("data-sorted.txt");
	}
	
}
