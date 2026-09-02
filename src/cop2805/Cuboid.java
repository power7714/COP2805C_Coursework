package cop2805;

/*
 * Cuboid.java
 * Copyright (c) 2026 Steve Curtis, PDCStudios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* This class creates a 3D rectangular box.
 * It gets three whole-number measurements: width, depth, and height.
 * The Volume() method multiplies those three numbers to find the total space
 * inside the box. This can be used to work with or compare box-shaped objects.
 */

public class Cuboid extends Shape3D {
    
    private int width;
    private int depth;
    private int height;
    
    public Cuboid(int width, int depth, int height) {
        this.width = width;
        this.depth = depth;
        this.height = height;
    }
    
    @Override
    public double Volume() {
        return width * depth * height;
    }
}