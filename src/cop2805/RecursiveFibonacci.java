package cop2805;

/**
 * RecursiveFibonacci.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 * 
 * A subclass of FibonacciThread that solves Fibonacci
 * using naive recursion. It blindly calls itself twice for every 
 * value it doesn't already know, which causes an explosion of 
 * redundant calculations. Computing fibonacci(38) alone gets 
 * called hundreds of millions of times for n=40. 
 * This was not my favorite approach. It was too slow in my opinion.
 */

public class RecursiveFibonacci extends FibonacciThread {
	 
    public RecursiveFibonacci(int n) {
        super(n);
    }
 
    @Override
    protected long compute(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return compute(n - 1) + compute(n - 2);
    }
 
    @Override
    protected String getAlgorithmName() {
        return "Recursive";
    }
}