package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import static com.phoenixclient.PhoenixClient.MC;

public class ElytraJump extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of ElytraJump",
            "Launch").setModeData("Launch","Hold");


    public ElytraJump() {
        super("ElytraJump", "Jump and move with the elytra", Category.MOTION, false, -1);
        addSettings(mode);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE, this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        //Launch elytra on jump
        if (MC.options.keyJump.isDown()) {

            //Move forward a tad when jumping
            //if (MC.player.onGround())MC.player.addDeltaMovement(new Vector(new Angle(MC.player.getYHeadRot(),true), new Angle(0), .1).getVec3());

            //Launch the Elytra
            if (!MC.player.isFallFlying()) {
                MC.player.startFallFlying();
                MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }

            if (mode.get().equals("Hold")) {
                if (MC.options.keyJump.isDown()) MC.player.addDeltaMovement(new Vector(0,.5,0).getVec3());
            }

        }
    }
}
