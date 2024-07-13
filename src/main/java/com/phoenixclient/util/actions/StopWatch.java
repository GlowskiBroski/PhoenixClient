package com.phoenixclient.util.actions;

import com.phoenixclient.util.math.MathUtil;

public class StopWatch {

    private boolean running;
    private long startTime;

    public StopWatch() {
        this.running = false;
        this.startTime = -1;
    }

    public void start() {
        if (!isRunning()) restart();
    }

    public void stop() {
        this.running = false;
        this.startTime = -1;
    }

    public void restart() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }


    public void run(int timeMillis, Runnable action) {
        start();
        if (hasTimePassedMS(timeMillis)) {
            action.run();
            restart();
        }
    }


    public boolean isRunning() {
        return running;
    }

    public boolean hasTimePassedMS(double time) {
        return isRunning() && time < getTimeMS();
    }

    public boolean hasTimePassedS(double time) {
        return isRunning() && time * 1000 < getTimeMS();
    }

    public double getTimeMS() {
        return System.currentTimeMillis() - this.startTime;
    }

    public double getTimeS() {
        return MathUtil.roundDouble(((float) getTimeMS()) / 1000,2);
    }

}
