package cop2805;

/*
 * ArrayListManager.java
 * Copyright (c) 2025 Steve Curtis, PDCStudios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* This class populates an ArrayList with specified double values. 
 * The assignment has been broken down into smaller, more manageable
 * methods for reusability and for better debugging.
 * Each method is commented to specify what it does.
 * Each method takes an ArrayList parameter
 * SearchAndPrint() takes a Double value as the second parameter
 * to search the list for.
 * The reason for passing the ArrayList as the parameter is to be able
 * to diagnose/debug each method individually. That way we can pinpoint
 * when and where an error occurred.
 */


import java.util.ArrayList;
import java.util.Collections;

public class ArrayListManager 
{
	
	static ArrayList<Double> myList = new ArrayList<Double>();
	
	//Helper method to iterate through list and print results
	public static void PrintList(ArrayList<Double> list) 
	{
		try 
		{
	        for (Double element : list) 
	        {
	            System.out.println(element);
	        }
	    } catch (Exception e) {
	        System.out.println("An error occurred while printing: " + e.getMessage());
	    }
    }
	
	//Step 1: Populate list
	public static void PopulateList(ArrayList<Double> popList)
	{
		popList.add(1.5);
        popList.add(2.35);
        popList.add(-4.7);
        popList.add(0.01);
	}
    
	//Step 2: Print original list
    public static void PrintOrigList(ArrayList<Double> origList)
    {
    	System.out.println("--- Original List ---");
        PrintList(origList);
    }
    
    //Step 3: Sort list and print results
    public static void SortAndPrint(ArrayList<Double> listOrig)
    {
    	Collections.sort(listOrig);
    	System.out.println("\n--- Sorted List ---");
        PrintList(listOrig);
    }
    
    //Step 4: Search list for specific value and print result
    public static void SearchAndPrint(ArrayList<Double> listSearch, Double searchValue)
    {
    	int index = Collections.binarySearch(listSearch, searchValue);
    	System.out.println("\n--- Searching list for: " + searchValue.toString() + " ---");
        System.out.println("\nFound " + searchValue + " at: " + index);
    }
    
    //Step 5: Zero out list and print result
    public static void ZeroListAndPrint(ArrayList<Double> zeroList)
    {
    	Collections.fill(zeroList, 0.0);

        System.out.println("\n--- Zero List ---");
        PrintList(zeroList);
    }

    public static void main(String[] args) 
    {
    	PopulateList(myList);
    	PrintOrigList(myList);
    	SortAndPrint(myList);
    	SearchAndPrint(myList, 1.5);
    	ZeroListAndPrint(myList);
    }
}
