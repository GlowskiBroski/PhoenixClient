package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.setting.SettingGUI;

import static com.phoenixclient.PhoenixClient.MC;

public class Flight extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of Fly",
            "Vanilla").setModeData("Vanilla","Packet");

    public Flight() {
        super("Flight", "Allows the player to fly", Category.MOTION, false, -1);
        addSettings(mode);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        switch (mode.get()) {
            case "Vanilla" -> MC.player.getAbilities().flying = true;
            case "Packet" -> {
                //TODO: Implement Packetfly
            }
        }
    }

    @Override
    public void onDisabled() {
        MC.player.getAbilities().flying = false;
    }

}
