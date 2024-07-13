package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.ConsoleUtil;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;

import static com.phoenixclient.PhoenixClient.MC;


public class DeathSpot extends Module {

    private final SettingGUI<Boolean> waypoint = new SettingGUI<>(
            this,
            "Waypoint",
            "Spawns a waypoint to your last death location",
            true
    );

    private final SettingGUI<Boolean> chatMessage = new SettingGUI<>(
            this,
            "Chat",
            "Sends your coordinates to chat upon death",
            true
    );

    private final DoOnce doOnce = new DoOnce();

    private Vector previousDeathLocation = Vector.NULL();

    public DeathSpot() {
        super("DeathSpot", "Tells you your death location", Category.SERVER, false, -1);
        addSettings(waypoint, chatMessage);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (MC.player.isAlive()) {
            doOnce.reset();
        } else {
            doOnce.run(() -> {
                Vector pos = (previousDeathLocation = new Vector(MC.player.getPosition(0)));
                if (chatMessage.get()) {
                    ConsoleUtil.sendMessage("Death Location: (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                }
            });
        }
    }

}
