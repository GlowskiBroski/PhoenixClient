package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.vehicle.MinecartTNT;

import static com.phoenixclient.PhoenixClient.MC;

public class Speed extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of speed",
            "BHop").setModeData("BHop", "OldNCP", "ElytraBounce", "ElytraSprint");

    private final SettingGUI<Integer> bouncePitch = new SettingGUI<>(
            this,
            "Pitch",
            "Lock pitch of ElytraBounce",
            45).setSliderData(0, 85, 1).setDependency(mode, "ElytraBounce");

    private final SettingGUI<Integer> speedCap = new SettingGUI<>(
            this,
            "Speed Cap",
            "Top speed of ElytraSprint",
            100)
            .setSliderData(0, 200, 5).setDependency(mode, "ElytraSprint");

    private final SettingGUI<Integer> esAcc = new SettingGUI<>(
            this,
            "Acceleration",
            "Acceleration for the speed mod",
            7)
            .setSliderData(1, 15, 1).setDependency(mode, "ElytraSprint");

    private double step = 0;


    public Speed() {
        super("Speed", "Makes you go faster", Category.MOTION, false, -1);
        addSettings(mode, bouncePitch,speedCap, esAcc);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onUpdate);
    }

    public void onUpdate(Event event) {
        switch (mode.get()) {
            case "BHop" -> bHopSpeed();
            case "OldNCP" -> oldNcpSpeed();
            case "ElytraBounce" -> elytraBounce();
            case "ElytraSprint" -> elytraSprint();
        }
    }

    @Override
    public void onDisabled() {
        step = 0;
        switch (mode.get()) {
            case "BHop" -> {

            }
            case "OldNCP" -> {
                MC.player.setDeltaMovement(MC.player.getDeltaMovement().x(), -.4, MC.player.getDeltaMovement().z());
            }
            case "ElytraBounce" -> {
                MC.options.keyJump.setDown(false);
                MixinHooks.keepElytraOnGround = false;
            }
            case "ElytraSprint" -> {
                MixinHooks.keepElytraOnGround = false;
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

    public void elytraBounce() {
        MixinHooks.keepElytraOnGround = true;
        if (!MC.player.isFallFlying()) {
            MC.player.startFallFlying();
            MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
        if (MotionUtil.isInputActive(false) && MC.player.isFallFlying()) {
            MC.player.setXRot(bouncePitch.get());//Potential good pitched: 20, 45, 70
            if (MC.player.onGround()) MC.options.keyJump.setDown(true);
        }
    }

    @Deprecated
    public void elytraBounceOld() {
        MixinHooks.keepElytraOnGround = true;
        if (MotionUtil.isInputActive(false)) {
            MC.player.setXRot(bouncePitch.get());//Potential good pitched: 20, 45, 70

            double hMom = 20 * Math.sqrt(Math.pow(MC.player.getDeltaMovement().x, 2) + Math.pow(MC.player.getDeltaMovement().z, 2));

            boolean shouldExtend = hMom >= 18;

            if (MC.player.onGround()) {
                MC.options.keyJump.setDown(true);

                if (shouldExtend) {
                    // in case you want to try horizontal //MC.player.addDeltaMovement(new Vector(yaw, 0, speed).getVec3());
                    MotionUtil.addEntityMotionInLookDirection(MC.player, .12);
                    if (hMom > 30) MotionUtil.addEntityMotionInLookDirection(MC.player, .01);
                    if (hMom > 35) MotionUtil.addEntityMotionInLookDirection(MC.player, .01);
                    if (hMom > 40) MotionUtil.addEntityMotionInLookDirection(MC.player, .01);
                    if (hMom > 45) MotionUtil.addEntityMotionInLookDirection(MC.player, .022);
                    if (hMom > 48) MotionUtil.addEntityMotionInLookDirection(MC.player, .022);
                    if (hMom > 50) MotionUtil.addEntityMotionInLookDirection(MC.player, .022);
                    if (hMom > 52) MotionUtil.addEntityMotionInLookDirection(MC.player, .022);
                    if (hMom > 54) MotionUtil.addEntityMotionInLookDirection(MC.player, .022);
                }
            } else {
                MC.options.keyJump.setDown(!MC.options.keyJump.isDown());
                if (MC.player.isFallFlying()) {

                    if (shouldExtend) {
                        double inc = .000125;

                        if (hMom > 30) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 32) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 34) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 36) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 38) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 40) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 42) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 44) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 46) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 48) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 50) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 52) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 54) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 56) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 58) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                        if (hMom > 60) MotionUtil.addEntityMotionInLookDirection(MC.player, inc);
                    }
                }
            }
        }
    }

    /**
     * Doesn't require jumping to work. Still bypasses GRIM
     */
    public void elytraSprint() {
        if (MC.options.keyUp.isDown()) {
            if (!MC.player.isFallFlying()) {
                MC.player.startFallFlying();
                MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }

            if (MC.player.isFallFlying() && MC.player.onGround()) {
                MixinHooks.keepElytraOnGround = true;
                MC.player.setSprinting(false);

                double acceleration = (double) esAcc.get() / 100;
                Angle yaw = new Angle(MC.player.getRotationVector().y, true);
                Angle pitch = new Angle(10, true);

                double hMom = 20 * Math.sqrt(Math.pow(MC.player.getDeltaMovement().x, 2) + Math.pow(MC.player.getDeltaMovement().z, 2));
                if (hMom <= speedCap.get() && !MC.options.keyShift.isDown()) MC.player.addDeltaMovement(new Vector(yaw, pitch, acceleration).getVec3());
            }

        } else {
            MixinHooks.keepElytraOnGround = false;
        }
    }

}
