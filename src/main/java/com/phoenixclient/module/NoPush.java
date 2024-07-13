package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.mixin.MixinHooks;

import static com.phoenixclient.PhoenixClient.MC;

public class NoPush extends Module {

    public NoPush() {
        super("NoPush", "Stops entity collisions", Category.MOTION, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        MixinHooks.noPush = true;
    }

    @Override
    public void onDisabled() {
        MixinHooks.noPush = false;
    }

}
