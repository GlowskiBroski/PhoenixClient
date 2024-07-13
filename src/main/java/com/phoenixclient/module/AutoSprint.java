package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoSprint extends Module {

    public AutoSprint() {
        super("AutoSprint", "Automatically sprints", Category.MOTION, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (MC.options.keyUp.isDown()) MC.player.setSprinting(true);
        if (MC.options.keyShift.isDown()) MC.player.setSprinting(false);
    }

}
