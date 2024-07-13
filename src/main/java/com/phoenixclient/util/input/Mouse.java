package com.phoenixclient.util.input;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.ConsoleUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;


public class Mouse {

    private static final HashMap<Integer, Mouse> BUTTON_LIST = new HashMap<>();

    public static final int ACTION_CLICK = 1;
    public static final int ACTION_RELEASE = 0;

    public static final Mouse BUTTON_LEFT = new Mouse(0);
    public static final Mouse BUTTON_RIGHT = new Mouse(1);
    public static final Mouse BUTTON_MIDDLE = new Mouse(2);


    public static final EventAction MOUSE_CLICK_ACTION = new EventAction(Event.EVENT_MOUSE_CLICK, () -> {
        int button = Event.EVENT_MOUSE_CLICK.getButton();
        int action = Event.EVENT_MOUSE_CLICK.getState();
        try {
            BUTTON_LIST.get(button).update(button, action);
        } catch (NullPointerException e) {
            System.out.println(ConsoleUtil.PREFIX + ": Mouse Button Object Not Registered!");
        }
    });

    //------------------------------------------

    private final int id;
    private boolean isButtonDown;

    public Mouse(int id) {
        this.id = id;
        BUTTON_LIST.put(id,this);
    }

    public void update(int key, int action) {
        if (getId() == key) {
            if (action == Mouse.ACTION_CLICK) setButtonDown(true);
            if (action == Mouse.ACTION_RELEASE) setButtonDown(false);
        }
    }

    public void setButtonDown(boolean buttonDown) {
        isButtonDown = buttonDown;
    }

    public boolean isButtonDown() {
        return isButtonDown;
    }

    public final int getId() {
        return id;
    }
}
