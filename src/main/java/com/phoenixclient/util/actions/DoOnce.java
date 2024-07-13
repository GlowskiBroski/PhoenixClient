package com.phoenixclient.util.actions;

public class DoOnce {

    private boolean shouldRun;

    public DoOnce(boolean runImmediately) {
        this.shouldRun = runImmediately;
    }

    public DoOnce() {
        this(true);
    }

    public void run(Runnable action) {
        if (shouldRun) {
            action.run();
            this.shouldRun = false;
        }
    }

    public void reset() {
        shouldRun = true;
    }

}
