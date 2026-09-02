package cop2805;

/*
 * ShapesTest.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* This class makes one example of a cuboid and another example
 * of a cylinder then calculates their volumes,
 * compares them using the compareTo() method, and prints which
 * one has the bigger volume (or if they are equal). It shows
 * how the shape classes work together in a real program.
 */

public class ShapesTest {
	
	public static void main(String[] args) {
        
        Cuboid cuboid = new Cuboid(5, 6, 7);
        
        Cylinder cylinder = new Cylinder(4, 10);
        
        System.out.println("Cuboid volume: " + cuboid.Volume());
        System.out.println("Cylinder volume: " + cylinder.Volume());
        
        int comparison = cuboid.compareTo(cylinder);
        
        System.out.println("\nComparison result:");
        if (comparison > 0) {
            System.out.println("The cuboid has greater volume than the cylinder.");
        } else if (comparison < 0) {
            System.out.println("The cylinder has greater volume than the cuboid.");
        } else {
            System.out.println("Both shapes have equal volume.");
        }
        
        
    }
}
