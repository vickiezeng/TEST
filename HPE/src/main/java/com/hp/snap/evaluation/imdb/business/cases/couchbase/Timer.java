package com.hp.snap.evaluation.imdb.business.cases.couchbase;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class Timer {
    // Time stat. used at Thread
    public static final int TIME_PREPARE = 0;
    public static final int TIME_BIND = 1;
    public static final int TIME_EXECUTE = 2;
    public static final String[] TIME_NAMES = new String[] { "PREPARE", "BIND", "EXECUTE" };

    private long t;

    public Timer() {
        t = 0;
    }

    public void start() {
        if (t != 0)
            throw new IllegalStateException("Timer already has been started.");

        t = System.nanoTime();
    }

    public long stop() throws IllegalStateException {
        if (t == 0)
            throw new IllegalStateException("Timer has not been started yet.");

        long result = System.nanoTime() - t;
        t = 0;
        return result;
    }

    public static String getNano2Sec(long aNano) {
        return NumberFormat.getInstance().format((double) aNano / 1000000000.0);
    }

    public static String getNano2Milli(long aNano) {
        return NumberFormat.getInstance().format(TimeUnit.NANOSECONDS.toMillis(aNano));
    }
}
