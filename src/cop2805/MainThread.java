package cop2805;

/**
 * MainThread.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 * 
 * This serves as the main activity class. It creates one 
 * instance of each solver then starts both threads. Then 
 * it uses .join() to wait for both to finish before printing 
 * a comparison summary. It also runs a result-match to confirm 
 * that both algorithms produced the same answer. This is where 
 * you would change the "N" value for both solvers. I've added
 * some subtle touches to make the output look more presentable.
 */

public class MainThread {
	 
	// Here I have tested this with different values.
	// I have found that recursive becomes unstable around n=50+
    private static final int N = 40;
 
    public static void main(String[] args) {
        System.out.println("=".repeat(55));
        System.out.printf("Fibonacci Sequence Benchmark  (n = %d)%n", N);
        System.out.println("=".repeat(55));
 
        // Here we create both threads
        FibonacciThread recursive = new RecursiveFibonacci(N);
        FibonacciThread dynamic   = new DynamicFibonacci(N);
 
        // Start both threads
        recursive.start();
        dynamic.start();
 
        // We wait for both threads to complete BEFORE outputting the summary. Preventing race condition.
        try {
            recursive.join();
            dynamic.join();
        } catch (InterruptedException e) {
            System.err.println("A thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
 
        // Print summary comparison
        printSummary(recursive, dynamic);
    }
 
    private static void printSummary(FibonacciThread recursive, FibonacciThread dynamic) {
        System.out.println("-".repeat(55));
        System.out.println("Summary");
        System.out.println("-".repeat(55));
 
        long recMs = recursive.getElapsedMs();
        long dynMs = dynamic.getElapsedMs();
 
        System.out.printf("Recursive  : %d ms%n", recMs);
        System.out.printf("Dynamic DP : %d ms%n", dynMs);
 
        if (dynMs == 0) {
            System.out.println("Dynamic was so fast it completed in < 1 ms!");
        } else {
            System.out.printf("Recursive was ~%dx slower%n", recMs / dynMs);
        }
 
        // Here we check that both algorithms produce the same result
        if (recursive.getResult() != dynamic.getResult()) {
            System.err.println("[WARNING] Results do not match. Check calculations or threads!");
        } else {
            System.out.println("Hooray! Results match.");
        }
 
        System.out.println("=".repeat(55));
    }
}