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
        return getEvent().subscribeAction(this);
    }

    public boolean unsubscribe() {
        return getEvent().unsubscribeAction(this);
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