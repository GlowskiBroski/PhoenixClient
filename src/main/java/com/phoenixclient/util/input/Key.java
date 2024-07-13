package com.phoenixclient.util.input;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.ConsoleUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;


public class Key {

    public static final HashMap<Integer,Key> KEY_LIST = new HashMap<>();

    public static Key KEY_A = new Key(GLFW.GLFW_KEY_A);
    public static Key KEY_B = new Key(GLFW.GLFW_KEY_B);
    public static Key KEY_C = new Key(GLFW.GLFW_KEY_C);
    public static Key KEY_D = new Key(GLFW.GLFW_KEY_D);
    public static Key KEY_E = new Key(GLFW.GLFW_KEY_E);
    public static Key KEY_F = new Key(GLFW.GLFW_KEY_F);
    public static Key KEY_G = new Key(GLFW.GLFW_KEY_G);
    public static Key KEY_H = new Key(GLFW.GLFW_KEY_H);
    public static Key KEY_I = new Key(GLFW.GLFW_KEY_I);
    public static Key KEY_J = new Key(GLFW.GLFW_KEY_J);
    public static Key KEY_K = new Key(GLFW.GLFW_KEY_K);
    public static Key KEY_L = new Key(GLFW.GLFW_KEY_L);
    public static Key KEY_M = new Key(GLFW.GLFW_KEY_M);
    public static Key KEY_N = new Key(GLFW.GLFW_KEY_N);
    public static Key KEY_O = new Key(GLFW.GLFW_KEY_O);
    public static Key KEY_P = new Key(GLFW.GLFW_KEY_P);
    public static Key KEY_Q = new Key(GLFW.GLFW_KEY_Q);
    public static Key KEY_R = new Key(GLFW.GLFW_KEY_R);
    public static Key KEY_S = new Key(GLFW.GLFW_KEY_S);
    public static Key KEY_T = new Key(GLFW.GLFW_KEY_T);
    public static Key KEY_U = new Key(GLFW.GLFW_KEY_U);
    public static Key KEY_V = new Key(GLFW.GLFW_KEY_V);
    public static Key KEY_W = new Key(GLFW.GLFW_KEY_W);
    public static Key KEY_X = new Key(GLFW.GLFW_KEY_X);
    public static Key KEY_Y = new Key(GLFW.GLFW_KEY_Y);
    public static Key KEY_Z = new Key(GLFW.GLFW_KEY_Z);

    public static Key KEY_1 = new Key(GLFW.GLFW_KEY_1);
    public static Key KEY_2 = new Key(GLFW.GLFW_KEY_2);
    public static Key KEY_3 = new Key(GLFW.GLFW_KEY_3);
    public static Key KEY_4 = new Key(GLFW.GLFW_KEY_4);
    public static Key KEY_5 = new Key(GLFW.GLFW_KEY_5);
    public static Key KEY_6 = new Key(GLFW.GLFW_KEY_6);
    public static Key KEY_7 = new Key(GLFW.GLFW_KEY_7);
    public static Key KEY_8 = new Key(GLFW.GLFW_KEY_8);
    public static Key KEY_9 = new Key(GLFW.GLFW_KEY_9);
    public static Key KEY_0 = new Key(GLFW.GLFW_KEY_0);

    public static Key KEY_F1 = new Key(GLFW.GLFW_KEY_F1);
    public static Key KEY_F2 = new Key(GLFW.GLFW_KEY_F2);
    public static Key KEY_F3 = new Key(GLFW.GLFW_KEY_F3);
    public static Key KEY_F4 = new Key(GLFW.GLFW_KEY_F4);
    public static Key KEY_F5 = new Key(GLFW.GLFW_KEY_F5);
    public static Key KEY_F6 = new Key(GLFW.GLFW_KEY_F6);
    public static Key KEY_F7 = new Key(GLFW.GLFW_KEY_F7);
    public static Key KEY_F8 = new Key(GLFW.GLFW_KEY_F8);
    public static Key KEY_F9 = new Key(GLFW.GLFW_KEY_F9);
    public static Key KEY_F10 = new Key(GLFW.GLFW_KEY_F10);
    public static Key KEY_F11 = new Key(GLFW.GLFW_KEY_F11);
    public static Key KEY_F12 = new Key(GLFW.GLFW_KEY_F12);

    public static Key KEY_LBRACKET = new Key(GLFW.GLFW_KEY_LEFT_BRACKET);
    public static Key KEY_RBRACKET = new Key(GLFW.GLFW_KEY_RIGHT_BRACKET);
    public static Key KEY_BACK_SLASH = new Key(GLFW.GLFW_KEY_BACKSLASH);
    public static Key KEY_SEMICOLON = new Key(GLFW.GLFW_KEY_SEMICOLON);
    public static Key KEY_APOSTROPHE = new Key(GLFW.GLFW_KEY_APOSTROPHE);

    public static Key KEY_INSERT = new Key(GLFW.GLFW_KEY_INSERT);
    public static Key KEY_PRT_SC = new Key(GLFW.GLFW_KEY_PRINT_SCREEN);
    public static Key KEY_DELETE = new Key(GLFW.GLFW_KEY_DELETE);
    public static Key KEY_HOME = new Key(GLFW.GLFW_KEY_HOME);
    public static Key KEY_PG_UP = new Key(GLFW.GLFW_KEY_PAGE_UP);
    public static Key KEY_PG_DN = new Key(GLFW.GLFW_KEY_PAGE_DOWN);
    public static Key KEY_END = new Key(GLFW.GLFW_KEY_END);

    public static Key KEY_ESC = new Key(GLFW.GLFW_KEY_ESCAPE);
    public static Key KEY_GRAVE = new Key(GLFW.GLFW_KEY_GRAVE_ACCENT);
    public static Key KEY_TAB = new Key(GLFW.GLFW_KEY_TAB);
    public static Key KEY_CAPS_LOCK = new Key(GLFW.GLFW_KEY_CAPS_LOCK);

    public static Key KEY_LSHIFT = new Key(GLFW.GLFW_KEY_LEFT_SHIFT);
    public static Key KEY_LCONTROL = new Key(GLFW.GLFW_KEY_LEFT_CONTROL);
    public static Key KEY_LALT = new Key(GLFW.GLFW_KEY_LEFT_ALT);

    public static Key KEY_RSHIFT = new Key(GLFW.GLFW_KEY_RIGHT_SHIFT);
    public static Key KEY_RCONTROL = new Key(GLFW.GLFW_KEY_RIGHT_CONTROL);
    public static Key KEY_RALT = new Key(GLFW.GLFW_KEY_RIGHT_ALT);

    public static Key KEY_COMMA = new Key(GLFW.GLFW_KEY_COMMA);
    public static Key KEY_PERIOD = new Key(GLFW.GLFW_KEY_PERIOD);
    public static Key KEY_FORWARD_SLASH = new Key(GLFW.GLFW_KEY_SLASH);

    public static Key KEY_SPACE = new Key(GLFW.GLFW_KEY_SPACE);
    public static Key KEY_BACKSPACE = new Key(GLFW.GLFW_KEY_BACKSPACE);
    public static Key KEY_ENTER = new Key(GLFW.GLFW_KEY_ENTER);

    public static Key KEY_WINDOWS = new Key(343);

    public static Key KEY_UP = new Key(GLFW.GLFW_KEY_UP);
    public static Key KEY_DOWN = new Key(GLFW.GLFW_KEY_DOWN);
    public static Key KEY_LEFT = new Key(GLFW.GLFW_KEY_LEFT);
    public static Key KEY_RIGHT = new Key(GLFW.GLFW_KEY_RIGHT);


    public static final int ACTION_PRESS = 1;
    public static final int ACTION_RELEASE = 0;
    public static final int ACTION_HOLD = 2;


    public static final EventAction KEY_PRESS_ACTION = new EventAction(Event.EVENT_KEY_PRESS, () -> {
        int key = Event.EVENT_KEY_PRESS.getKey();
        int scancode = Event.EVENT_KEY_PRESS.getScancode();
        int action = Event.EVENT_KEY_PRESS.getState();
        int mods = Event.EVENT_KEY_PRESS.getModifiers();
        try {
            KEY_LIST.get(key).update(key, scancode, action, mods);
        } catch (NullPointerException e) {
            System.out.println(ConsoleUtil.PREFIX + ": Key Object Not Registered!");
        }
    });

    //------------------------------------------

    private final int id;
    private boolean isKeyDown;

    public Key(int id) {
        this.id = id;
        KEY_LIST.put(id,this);
    }

    public void update(int key, int scancode, int action, int mods) {
        if (getId() == key) {
            if (action == GLFW.GLFW_PRESS) setKeyDown(true);
            if (action == GLFW.GLFW_RELEASE) setKeyDown(false);
        }
    }

    public void setKeyDown(boolean keyDown) {
        isKeyDown = keyDown;
    }

    public boolean isKeyDown() {
        return isKeyDown;
    }

    public int getId() {
        return id;
    }

}
