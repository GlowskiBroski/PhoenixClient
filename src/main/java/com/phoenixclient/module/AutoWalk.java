package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoWalk extends Module {

    public AutoWalk() {
        super("AutoWalk", "Automatically walks forward", Category.MOTION, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        MC.options.keyUp.setDown(true);
    }

    @Override
    public void onDisabled() {
        MC.options.keyUp.setDown(false);
    }

}
