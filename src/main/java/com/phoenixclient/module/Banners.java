package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.mixin.MixinHooks;

//TODO: Look into a "Too Expensive" bypass for anvils?

public class Banners extends Module {

    public Banners() {
        super("Banners", "Overrides the maximum banner pattern limit of 6 layers", Category.SERVER, false, -1);
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
