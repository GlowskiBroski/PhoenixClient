package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.mixin.MixinHooks;

public class Banners extends Module {

    public Banners() {
        super("Banners", "Allows for loom patterns over 6 layers", Category.SERVER, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        MixinHooks.allowOverloadedBanners = true;
    }

    @Override
    public void onDisabled() {
        MixinHooks.allowOverloadedBanners = false;
    }
}
