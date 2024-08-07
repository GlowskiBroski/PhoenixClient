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

    // -------------------------------------------------------------------------------

    private final ArrayList<EventAction> eventActions = new ArrayList<>();

    private final ArrayList<EventAction> addition = new ArrayList<>();
    private final ArrayList<EventAction> removalQueue = new ArrayList<>();

    private Object[] args = new Object[]{};

    public void post(Object... args) {
        this.args = args;

        try {//TODO: This is a new queue system. Do more checking with this to see if its viable
            removalQueue.forEach((action) -> getActions().remove(action));
            removalQueue.clear();
            addition.forEach((action) -> getActions().add(action));
            addition.clear();
        } catch (ConcurrentModificationException ignored) {
        }

        for (EventAction event : getActions()) event.run();

    }

    public boolean subscribeAction(EventAction action) {
        if((getActions().contains(action))) return false;
        action.subscribed = true;
        return addition.add(action);
    }


    public boolean unsubscribeAction(EventAction action) {
        action.subscribed = false;
        return removalQueue.add(action);
    }


    public Object[] getArgs() {
        return args;
    }

    public ArrayList<EventAction> getActions() {
        return eventActions;
    }

}
