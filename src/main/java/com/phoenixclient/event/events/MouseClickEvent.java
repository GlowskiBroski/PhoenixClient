package com.phoenixclient.event.events;

import com.phoenixclient.event.Event;

public class MouseClickEvent extends Event {

    private double x;
    private double y;

    private int button;
    private int state;

    @Override
    public void post(Object... args) {
        x = (double) args[0];
        y = (double) args[1];
        button = (int) args[2]; //0 is left, 1 is right, 2 is middle
        state = (int) args[3]; //1 is click, 0 is release
        super.post(args);
    }

    public double getMouseX() {
        return x;
    }

    public double getMouseY() {
        return y;
    }

    public int getButton() {
        return button;
    }

    public int getState() {
        return state;
    }

    public boolean isClick() {
        return getState() == 1;
    }

    public boolean isRelease() {
        return getState() == 0;
    }

}
