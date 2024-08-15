package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.ConsoleUtil;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import static com.phoenixclient.PhoenixClient.MC;

public class ElytraFly extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of ElytaFly",
            "Firework").setModeData("Firework", "Boost", "Ground", "Bounce")
            .setModeDescriptions();

    //Boost
    private final SettingGUI<Double> speedBoost = new SettingGUI<>(
            this,
            "Speed",
            "Speed of ElytaFly Boost Mode",
            1d)
            .setSliderData(.1, 2, .1).setDependency(mode, "Boost");

    //Ground
    private final SettingGUI<Integer> speedCapGround = new SettingGUI<>(
            this,
            "Ground Speed Cap",
            "Top speed of hold mode",
            100)
            .setSliderData(0, 200, 5).setDependency(mode, "Ground");

    private final SettingGUI<Integer> accelerationGround = new SettingGUI<>(
            this,
            "Acceleration",
            "Acceleration for the speed mod",
            7)
            .setSliderData(1, 15, 1).setDependency(mode, "Ground");

    private final SettingGUI<Boolean> fastFireworkGround = new SettingGUI<>(
            this,
            "Fast Firework",
            "Adds Fast Firework mode capabilities to Ground when in the air. This is useful if you like to fly like an airplane",
            false).setDependency(mode,"Ground");

    //Bounce
    private final SettingGUI<Integer> pitchBounce = new SettingGUI<>(
            this,
            "Pitch",
            "Lock pitch of ElytraBounce",
            75).setSliderData(0, 85, 1).setDependency(mode, "Bounce");


    //Firework
    private final SettingGUI<Integer> speedFastFirework = new SettingGUI<>(
            this,
            "Firework Speed",
            "Top speed of the firework rocket",
            80)
            .setSliderData(30, 120, 5).setDependency(mode, "Firework");

    //Broken Fly
    private final SettingGUI<Boolean> brokenFly = new SettingGUI<>(
            this,
            "Broken Fly",
            "Allows the player to continue flight even if the Elytra is broken",
            false);

    //Rubberband Detector
    private final SettingGUI<Boolean> rubberBandDetection = new SettingGUI<>(
            this,
            "RubberBand Detection",
            "Detects if the player is rubberbanding and takes measures to negate it",
            false).setDependency(mode,"Ground");

    private int fireworkStep = 14;

    private final StopWatch rubberBandWatch = new StopWatch();
    private boolean isRubberBanding = false;
    private int rubberBandCount = 0;

    public ElytraFly() {
        super("ElytraFly", "Allows for different movement methods surrounding the Elytra", Category.MOTION, false, -1);
        addSettings(mode, speedBoost, speedCapGround, pitchBounce, accelerationGround, speedFastFirework, brokenFly,rubberBandDetection,fastFireworkGround);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE, this::onPlayerUpdate);
        addEventSubscriber(Event.EVENT_PACKET, this::onPacket);
    }

    public void onPlayerUpdate(Event event) {
        if (!MC.player.inventoryMenu.getSlot(6).getItem().getItem().equals(Items.ELYTRA)) return;

        mode.runOnChange(() -> MixinHooks.keepElytraOnGround = false);

        switch (mode.get()) {
            case "Boost" -> boost();
            case "Ground" -> ground();
            case "Bounce" -> bounce();
            case "Firework" -> firework(speedFastFirework.get());
        }

        if (brokenFly.get()) {
            ItemStack item = MC.player.inventoryMenu.getSlot(6).getItem();
            if (item.getDamageValue() + 1 >= item.getMaxDamage() && !MC.player.isFallFlying()) {
                MC.player.startFallFlying();
                MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }
        }

    }

    public void onPacket(PacketEvent event) {
        if (rubberBandDetection.get()) {
            rubberBandWatch.start();
            if (event.getPacket() instanceof ClientboundPlayerPositionPacket p) {
                rubberBandWatch.restart();
                rubberBandCount++;
                if (rubberBandCount > 5) isRubberBanding = true; //If there are 5 set position packets, we are def rubberbanding
            }
            if (rubberBandWatch.hasTimePassedMS(250)) {
                isRubberBanding = false;
            }
        } else {
            isRubberBanding = false;
            rubberBandCount = 0;
            rubberBandWatch.stop();
        }
    }

    @Override
    public void onEnabled() {
        if (updateDisableOnEnabled()) return;
        fireworkStep = 0;
    }

    @Override
    public void onDisabled() {
        if (MC.player == null) return;
        MixinHooks.keepElytraOnGround = false;
        switch (mode.get()) {
            case "Bounce" -> {
                MC.options.keyJump.setDown(false);
            }
        }
    }

    @Override
    public String getModTag() {
        return mode.get();
    }

    private void boost() {
        if (MC.player.isFallFlying()) {
            double speed = this.speedBoost.get() * (double) 1 / 10;
            MotionUtil.addEntityMotionInLookDirection(MC.player, speed);
        }
    }

    /**
     * Doesn't require jumping to work. Still bypasses GRIM
     */
    public void ground() {
        if (MC.options.keyUp.isDown() && MC.player.inventoryMenu.getSlot(6).getItem().getItem().equals(Items.ELYTRA)) {

            if (isRubberBanding) {
                MixinHooks.keepElytraOnGround = false;
                MC.player.stopFallFlying();
                return;
            }

            if (!MC.player.isFallFlying()) {
                MC.player.startFallFlying();
                MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }

            if (MC.player.isFallFlying() && MC.player.onGround()) {
                MixinHooks.keepElytraOnGround = true;
                MC.player.setSprinting(false);

                double acceleration = (double) accelerationGround.get() / 100;
                Angle yaw = new Angle(MC.player.getRotationVector().y, true);
                Angle pitch = new Angle(10, true);

                double hMom = 20 * Math.sqrt(Math.pow(MC.player.getDeltaMovement().x, 2) + Math.pow(MC.player.getDeltaMovement().z, 2));
                if (hMom <= speedCapGround.get() && !MC.options.keyShift.isDown())
                    MC.player.addDeltaMovement(new Vector(yaw, pitch, acceleration).getVec3());

                if (MC.player.touchingUnloadedChunk()) MC.player.setDeltaMovement(0, 0, 0);
            }
        } else {
            MixinHooks.keepElytraOnGround = false;
        }

        if (fastFireworkGround.get()) firework(Math.clamp(speedCapGround.get(),0,100));

    }

    public void bounce() {
        MixinHooks.keepElytraOnGround = true;
        if (!MC.player.isFallFlying()) {
            MC.player.startFallFlying();
            MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
        if (MotionUtil.isInputActive(false) && MC.player.isFallFlying()) {
            MC.player.setXRot(pitchBounce.get());//Potential good pitched: 20, 45, 70
            if (MC.player.onGround()) MC.options.keyJump.setDown(true);
        }
    }

    //This mode is kind of a combination between Ground mode and Bounce mode. Therefore, it is entirely deprecated by Ground mode
    @Deprecated
    public void bounceAlt() {
        MixinHooks.keepElytraOnGround = true;
        if (MotionUtil.isInputActive(false)) {
            MC.player.setXRot(pitchBounce.get());//Potential good pitched: 20, 45, 70

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

    //TODO: Find a better way to detect if a firework is active or not
    public void firework(double speed) {
        if (MC.player.isFallFlying()) {
            boolean fireworkActive = false;
            for (Entity entity : MC.level.entitiesForRendering()) {
                if (entity instanceof FireworkRocketEntity) {
                    fireworkActive = true;
                    break;
                }
            }

            if (fireworkActive) {
                Angle yaw = new Angle(MC.player.getRotationVector().y, true);
                Angle pitch = new Angle(MC.player.getRotationVector().x, true);

                MC.player.setDeltaMovement(new Vector(yaw, pitch, .12 * fireworkStep).getVec3());
                fireworkStep = Math.clamp(fireworkStep + 3, 0, (int) (speed / 2.38));
            } else {
                fireworkStep = 14;
            }
        }
    }

}
