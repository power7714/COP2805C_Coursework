package cop2805;

/*
 * FindMin.java
 * Copyright (c) 2026 Steve Curtis, PDCStudios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* This class finds the min in an array using
 * generic comparable. Output has been separated into
 * separate functions for readability and ease of
 * debugging. The function min() provides the logic
 * to iterate through the array passed in the argument
 * to find the min and return the value.
 */

public class SteveFindMin {
	
    // Logic for finding the generic min in an array
    public static <E extends Comparable<E>> E min(E[] inputArray) {
        E minElement = inputArray[0];

        for (int i = 1; i < inputArray.length; i++) {
            if (inputArray[i].compareTo(minElement) < 0) {
                minElement = inputArray[i];
            }
        }
        return minElement;
    }
	
    // Prints the min for "colors" list
    public static void printColorsMin(String[] vals) {
        System.out.println("Color: " + min(vals));
    }
	
    // Prints the min for the "numbers" list
    public static void printNumbersMin(Integer[] vals) {
        System.out.println("Numbers: " + min(vals));
    }
	
    // Prints the min for the "circleRadius" list
    public static void printRadiusMin(Double[] vals) {
        System.out.println("Circle Radius: " + min(vals));
    }
	
    public static void main(String[] args) {
        String[] colors = {"Red", "Green", "Blue"};
        Integer[] numbers = {1, 2, 3};
        Double[] circleRadius = {3.0, 5.9, 2.9};

        printColorsMin(colors);
        printNumbersMin(numbers);
        printRadiusMin(circleRadius);
    }
}
