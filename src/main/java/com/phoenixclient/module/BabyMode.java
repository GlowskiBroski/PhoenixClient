package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.mixin.MixinHooks;

public class BabyMode extends Module {

    public BabyMode() {
        super("BabyMode", "Renders all players as babies. Awww", Category.RENDER, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        MixinHooks.renderAsBaby = true;
    }

    @Override
    public void onDisabled() {
        MixinHooks.renderAsBaby = false;
    }

}
