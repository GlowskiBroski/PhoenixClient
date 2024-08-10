package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.SettingGUI;

public class NoSlow extends Module {

    public NoSlow() {
        super("NoSlow", "Stops slowdown on use item - UNIMPLEMENTED", Category.PLAYER, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onUpdate);
    }

    public void onUpdate(Event event) {
        //TODO: Implement a mixin for this
    }

}
