package org.wut;

/**
 * The Time class provides methods for measuring execution time of a code section in milliseconds.
 */
public class Time {
    private static long startTime;

    /**
     * Start the timer.
     */
    public static void start() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Stop the timer and return the elapsed time in milliseconds.
     *
     * @return The elapsed time in milliseconds.
     */
    public static long stop() {
        return System.currentTimeMillis() - startTime;
    }

    public static void stopAndPrint() {
        System.out.println(stop());
    }
}
