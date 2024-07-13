package com.phoenixclient.util.actions;

public class OnChange<T> {

    private T prevValue;

    public OnChange() {
        this.prevValue = null;
    }

    public void run(T value, Runnable action) {
        boolean toNull = value == null && prevValue != null;
        boolean fromNull = value != null && prevValue == null;
        boolean constNull = value == null && prevValue == null;
        if (toNull || fromNull || (!constNull && !value.equals(prevValue))) {
            action.run();
        }
        prevValue = value;
    }

    public void reset() {
        prevValue = null;
    }

    public T getPrevValue() {
        return prevValue;
    }
}
