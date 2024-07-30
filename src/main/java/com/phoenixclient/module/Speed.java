package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.item.Items;

import static com.phoenixclient.PhoenixClient.MC;

public class Speed extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of speed",
            "BHop").setModeData("BHop", "OldNCP");


    private int step = 0;

    public Speed() {
        super("Speed", "Makes you go faster", Category.MOTION, false, -1);
        addSettings(mode);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onUpdate);
    }

    @Override
    public String getModTag() {
        return mode.get();
    }

    public void onUpdate(Event event) {
        switch (mode.get()) {
            case "BHop" -> bHopSpeed();
            case "OldNCP" -> oldNcpSpeed();
        }
    }

    @Override
    public void onDisabled() {
        step = 0;
        switch (mode.get()) {
            case "BHop" -> {/*Nothing Extra ¯\_(ツ)_/¯*/}
            case "OldNCP" -> {
                MC.player.setDeltaMovement(MC.player.getDeltaMovement().x(), -.4, MC.player.getDeltaMovement().z());
            }
        }
    }

    public void oldNcpSpeed() {
        double acceleration = 0;

        if (!MC.player.onGround()) {
            if (MC.player.getDeltaMovement().y() > 0) {
                acceleration = .19 * Math.log(this.step);
            } else {
                acceleration = .09 * Math.log(this.step);
            }
        }
        MotionUtil.moveEntityStrafe(.075 + acceleration, MC.player);

        if (MC.player.getDeltaMovement().x == 0 && MC.player.getDeltaMovement().z == 0) this.step = 0;

        if (MotionUtil.isInputActive(false)) {
            double offset = .1 * (MC.player.onGround() ? 1 : -1);

            MC.player.setDeltaMovement(MC.player.getDeltaMovement().x(), offset, MC.player.getDeltaMovement().z());

            this.step++;
            int maxStep = 16;
            if (this.step >= maxStep) this.step = maxStep;
        }
    }

    public void bHopSpeed() {
        double speed = .26;
        if (MC.player.getDeltaMovement().x == 0 && MC.player.getDeltaMovement().z == 0) this.step = 0;

        if (MotionUtil.isInputActive(false)) {
            if (MC.player.onGround()) {
                //Hop
                MC.player.setDeltaMovement(MC.player.getDeltaMovement().x(), .4, MC.player.getDeltaMovement().z());

                this.step++;
                int maxStep = 4;
                if (this.step >= maxStep) this.step = maxStep;
                speed += .18 * Math.log(this.step);

            } else {
                if (MC.player.getDeltaMovement().y() < 0) {
                    MC.player.setDeltaMovement(MC.player.getDeltaMovement().x(), MC.player.getDeltaMovement().y() * 1.01, MC.player.getDeltaMovement().z());
                }
            }
        }
        MotionUtil.moveEntityStrafe(speed, MC.player);
    }

}
