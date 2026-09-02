package cop2805;

/*
 * Shape3D.java
 * Copyright (c) 2026 Steve Curtis, PDCStudios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 */

/* This is the main abstract class that all 3D shapes inherit from.
 * It sets up the rules so every shape can tell us its volume and
 * can be compared to other shapes based on volume. It includes
 * an abstract Volume() method (which child classes must fill in)
 * and a compareTo() method that decides if one shape has more,
 * less, or the same volume as another.
 */

public abstract class Shape3D implements Comparable<Shape3D> {
    
    public abstract double Volume();
    
    @Override
    public int compareTo(Shape3D other) {
        double thisVolume = this.Volume();
        double otherVolume = other.Volume();
        
        if (thisVolume > otherVolume) {
            return 1;
        } else if (thisVolume < otherVolume) {
            return -1;
        } else {
            return 0;
        }
    }
}