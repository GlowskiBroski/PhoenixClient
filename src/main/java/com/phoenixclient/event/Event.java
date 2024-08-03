package com.phoenixclient.event;

import com.phoenixclient.event.events.*;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class Event {

    public static final KeyPressEvent EVENT_KEY_PRESS = new KeyPressEvent();
    public static final MouseClickEvent EVENT_MOUSE_CLICK = new MouseClickEvent();

    public static final PacketEvent EVENT_PACKET = new PacketEvent();

    public static final Event EVENT_RENDER_HUD = new Event();
    public static final RenderScreenEvent EVENT_RENDER_SCREEN = new RenderScreenEvent();
    public static final RenderItemTooltipEvent EVENT_DRAW_ITEM_TOOLTIP = new RenderItemTooltipEvent(); //This one is used every time the draw tooltip is called
    public static final RenderItemTooltipEvent EVENT_RENDER_INVENTORY_ITEM_TOOLTIP = new RenderItemTooltipEvent(); //this one is used every time a tooltip is rendered in a container

    public static final Event EVENT_PLAYER_UPDATE = new Event();

    public static final RenderLevelEvent EVENT_RENDER_LEVEL = new RenderLevelEvent();

    public static final RenderDebugEvent EVENT_RENDER_DEBUG = new RenderDebugEvent();


    private final ArrayList<EventAction> eventActions = new ArrayList<>();

    private Object[] args = new Object[]{};

    public void post(Object... args) {
        this.args = args;
        try {
            for (EventAction event : getActions()) event.run();
        } catch (ConcurrentModificationException ignored) {
            //If a concurrent modification exception ever occurs, think about potentially adding a queue subscribe/unsubscribe system
        }
    }

    public boolean subscribeAction(EventAction action) {
        try {
            if (!getActions().contains(action)) {
                action.subscribed = true;
                return getActions().add(action);
            }
        } catch (ConcurrentModificationException | NullPointerException e) {
            return false;
        }
        return false;
    }


    public boolean unsubscribeAction(EventAction action) {
        try {
            action.subscribed = false;
            return getActions().remove(action);
        } catch (ConcurrentModificationException | NullPointerException e) {
            return false;
        }
    }


    public Object[] getArgs() {
        return args;
    }

    public ArrayList<EventAction> getActions() {
        return eventActions;
    }

}
