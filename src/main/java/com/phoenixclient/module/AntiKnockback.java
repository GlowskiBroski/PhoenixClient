package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;

public class AntiKnockback extends Module {

    public AntiKnockback() {
        super("AntiKnockback", "Stops player knockback", Category.COMBAT, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        //TODO: Implement a mixin for this
    }

}
