package cop2805;

/**
 * FibonacciThread.java
 * Copyright (c) 2026 Steve Curtis, Six Actual Studios
 * All rights reserved.
 * 
 * This code is proprietary and confidential.
 * 
 * This is an abstract class that all Fibonacci solvers inherit from. 
 * It extends Thread and handles everything that both solvers 
 * share: accepting n as input, timing the execution with 
 * currentTimeMillis(), storing the result, and printing 
 * the formatted output. Subclasses only plug in their 
 * algorithm via compute() and supply  * a name via getAlgorithmName().
 */

public abstract class FibonacciThread extends Thread {

    protected int n;
    protected long result;
    protected long elapsedMs;

    public FibonacciThread(int n) {
        this.n = n;
    }

    protected abstract long compute(int n);

    protected abstract String getAlgorithmName();

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        result = compute(n);
        long endTime = System.currentTimeMillis();

        elapsedMs = endTime - startTime;
        printResult();
    }

    private void printResult() {
        System.out.printf("[%s] fibonacci(%d) = %d | Time: %d ms%n",
                getAlgorithmName(), n, result, elapsedMs);
    }

    // --- Getters for post-run inspection ---

    public long getResult()    { return result; }
    public long getElapsedMs() { return elapsedMs; }
    public int  getN()         { return n; }
}