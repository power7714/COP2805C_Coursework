package cop2805;

/**
 * DynamicFibonacci.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 * 
 * A subclass of FibonacciThread that solves 
 * Fibonacci iteratively using only three variables. 
 * It moves through the sequence once, building on the previous two, 
 * never repeating a calculation. This one is my favorite. 
 * I liked how fast this one did the calculations.
 * Same result but so much faster. I think this would be the better
 * course of action.
 */

public class DynamicFibonacci extends FibonacciThread {
	 
    public DynamicFibonacci(int n) {
        super(n);
    }
 
    @Override
    protected long compute(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
 
        long v1 = 0;  // two steps back
        long v2 = 1;  // one step back
        long v3 = 0;  // current
 
        for (int i = 2; i <= n; i++) {
            v3 = v1 + v2;
            v1 = v2;
            v2 = v3;
        }
 
        return v3;
    }
 
    @Override
    protected String getAlgorithmName() {
        return "Dynamic  "; // padded for cleaner console alignment
    }
}