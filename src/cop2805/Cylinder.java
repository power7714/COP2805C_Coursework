package cop2805;

/*
 * Cylinder.java
 * Copyright (c) 2026 Steve Curtis, PDCStudios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* This class creates a 3D cylinder (like a can or
 * pipe standing upright). It stores two whole-number
 * values: the radius of the circular base and the height.
 * The Volume() method uses the formula π × radius × radius × height
 * to calculate the space inside. You use it to represent and
 * compare cylinder-shaped objects.
 */

public class Cylinder extends Shape3D {
    
    private int radius;
    private int height;
    
    public Cylinder(int radius, int height) {
        this.radius = radius;
        this.height = height;
    }
    
    @Override
    public double Volume() {
        return Math.PI * radius * radius * height;
    }
}