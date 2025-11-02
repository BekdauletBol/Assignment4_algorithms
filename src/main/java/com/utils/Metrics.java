package com.utils;

/**
 * Metrics for counting
 * the number of operations and execution time in nanoseconds.
 */
public class Metrics {
    private long operationCount;
    private long startNano;
    private long endNano;

    /**
     * Initializes a new Metrics instance with all counters reset.
     */
    public Metrics() {
        clear();
    }

    public void incrementOperations() {
        operationCount++;
    }

    public void addOperations(long count) {
        operationCount += count;
    }

    public long getOperations() {
        return operationCount;
    }

    public void startTiming() {
        startNano = System.nanoTime();
    }

    public void stopTiming() {
        endNano = System.nanoTime();
    }

    public long getElapsedNanos() {
        return endNano - startNano;
    }

    public double getElapsedMillis() {
        return (endNano - startNano) / 1_000_000.0;
    }

    public void reset() {
        clear();
    }

    /**
     * Internal method to clear all performance counters.
     */
    private void clear() {
        operationCount = 0;
        startNano = 0;
        endNano = 0;
    }
}
