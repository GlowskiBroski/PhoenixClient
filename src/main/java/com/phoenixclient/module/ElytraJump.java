package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.input.Mouse;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.phys.Vec3;

import static com.phoenixclient.PhoenixClient.MC;


public class ElytraJump extends Module {

    public ElytraJump() {
        super("ElytraJump", "Uses Elytra packets to jump higher", Category.MOTION, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (MC.options.keyJump.isDown()) {
            if (!MC.player.isFallFlying()) {
                MC.player.startFallFlying();
                MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }
        }
    }
}
