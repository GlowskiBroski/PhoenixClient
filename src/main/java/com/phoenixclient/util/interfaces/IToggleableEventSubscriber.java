package com.phoenixclient.util.interfaces;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.module.Module;

import java.util.ArrayList;

public interface IToggleableEventSubscriber extends IToggleable {

    @Override
    default void enable() {
        IToggleable.super.enable();
        for (EventAction action : getEventActions()) action.subscribe();
    }

    @Override
    default void disable() {
        for (EventAction action : getEventActions()) action.unsubscribe();
        IToggleable.super.disable();
    }

    default <T extends Event> void addEventSubscriber(T event, Module.ActionEvent<T> subscriber) {
        getEventActions().add(new EventAction(event,() -> subscriber.run(event)));
    }

    ArrayList<EventAction> getEventActions();

}
