package com.phoenixclient.event;


public class EventAction implements Runnable {

    private final Event event;
    private final Runnable action;
    protected boolean subscribed;

    public EventAction(Event event, Runnable action) {
        this.event = event;
        this.action = action;
        this.subscribed = false;
    }


    @Override
    public void run() {
        getAction().run();
    }


    public boolean subscribe() {
        try {
            return getEvent().subscribeAction(this);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean unsubscribe() {
        try {
            return getEvent().unsubscribeAction(this);
        } catch (Exception e) {
            return false;
        }
    }


    public boolean isSubscribed() {
        return subscribed;
    }

    public Event getEvent() {
        return event;
    }

    private Runnable getAction() {
        return action;
    }

}