package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.mixin.MixinHooks;

public class NoPush extends Module {

    //TODO: Add no water/Lava push here

    public NoPush() {
        super("NoPush", "Stops entity collisions", Category.MOTION, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        MixinHooks.noPushEntities = true;
    }

    @Override
    public void onDisabled() {
        MixinHooks.noPushEntities = false;
    }

}
