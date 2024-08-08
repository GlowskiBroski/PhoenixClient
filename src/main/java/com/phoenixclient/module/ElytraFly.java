package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
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
            "Boost").setModeData("Boost","Hold","Ground","Bounce");

    //Boost
    private final SettingGUI<Double> speedBoost = new SettingGUI<>(
            this,
            "Speed",
            "Speed of ElytaFly Boost Mode",
            1d)
            .setSliderData(.1, 2, .1).setDependency(mode, "Boost");

    //Hold
    private final SettingGUI<Double> glideSpeedHold = new SettingGUI<>(
            this,
            "Glide Speed",
            "Glide speed of Hold mode",
            0d)
            .setSliderData(0, 2, .1).setDependency(mode, "Hold");

    private final SettingGUI<Integer> speedCapHold = new SettingGUI<>(
            this,
            "Hold Speed Cap",
            "Top speed of hold mode",
            100)
            .setSliderData(0, 200, 5).setDependency(mode, "Hold");

    private final SettingGUI<Boolean> useRocketsHold = new SettingGUI<>(
            this,
            "Use Rockets",
            "Automatically uses rockets every 2 seconds",
            true).setDependency(mode, "Hold");

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

    //Bounce
    private final SettingGUI<Integer> pitchBounce = new SettingGUI<>(
            this,
            "Pitch",
            "Lock pitch of ElytraBounce",
            75).setSliderData(0, 85, 1).setDependency(mode, "Bounce");


    //Firework Extension
    private final SettingGUI<Boolean> fastFirework = new SettingGUI<>(
            this,
            "Fast Fireworks",
            "Increases the top speed of a firework from 30m/s to something higher",
            false);

    private final SettingGUI<Integer> speedFastFirework = new SettingGUI<>(
            this,
            "Firework Speed",
            "Top speed of the firework rocket",
            80)
            .setSliderData(30, 120, 5).setDependency(fastFirework,true);

    private int step = 0;
    private int fireworkStep = 14;
    private final StopWatch watch = new StopWatch();

    public ElytraFly() {
        super("ElytraFly", "Allows control of the Elytra", Category.MOTION, false, -1);
        addSettings(mode, speedBoost, glideSpeedHold, speedCapHold, useRocketsHold, speedCapGround, pitchBounce, accelerationGround, speedFastFirework,fastFirework);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (!MC.player.inventoryMenu.getSlot(6).getItem().getItem().equals(Items.ELYTRA)) return;
        switch (mode.get()) {
            case "Boost" -> boost();
            case "Hold" -> hold();
            case "Ground" -> ground();
            case "Bounce" -> bounce();
        }

        if (fastFirework.get() && MC.player.isFallFlying()) {
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
                fireworkStep = Math.clamp(fireworkStep + 3,0,(int)(speedFastFirework.get() / 2.38));
            } else {
                fireworkStep = 14;
            }

        }
    }

    @Override
    public void onEnabled() {
        if (updateDisableOnEnabled()) return;
        switch (mode.get()) {
            case "Boost" -> {/*Nothing Extra ¯\_(ツ)_/¯*/}
            case "Hold" -> {
                if (MC.player.getMainHandItem().getItem() instanceof FireworkRocketItem && useRocketsHold.get()) startUseItem();
                watch.restart();
            }
            case "Ground" -> {/*Nothing Extra ¯\_(ツ)_/¯*/}
            case "Bounce" -> {/*Nothing Extra ¯\_(ツ)_/¯*/}
        }
    }

    @Override
    public void onDisabled() {
        if (MC.player == null) return;
        switch (mode.get()) {
            case "Boost" -> {/*Nothing Extra ¯\_(ツ)_/¯*/}
            case "Hold" -> {
                if (MC.player.getMainHandItem().getItem() instanceof FireworkRocketItem && useRocketsHold.get()) startUseItem();
                watch.restart();
            }
            case "Ground" -> {
                MixinHooks.keepElytraOnGround = false;
            }
            case "Bounce" -> {
                MC.options.keyJump.setDown(false);
                MixinHooks.keepElytraOnGround = false;
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

    private void hold() {
        if (MC.player.isFallFlying()) {
            MC.player.setXRot(0);

            int speedLimit = speedCapHold.get();
            float accSpeed = 1;
            Angle yaw = new Angle(MC.player.getRotationVector().y, true);
            if (MC.options.keyUp.isDown()) {
                watch.start();
                if (watch.hasTimePassedS(2) && MC.player.getMainHandItem().getItem() instanceof FireworkRocketItem) {
                    if (useRocketsHold.get()) startUseItem();
                    watch.restart();
                }
                //Add Auto Rocket Code Here, when W is down
                MC.player.setDeltaMovement(new Vector(yaw, new Angle(0), .1 * step).getVec3());
                if (step < speedLimit / 2) step += accSpeed;
            } else {
                step = 0;
            }

            if (MC.options.keyDown.isDown()) MC.player.setDeltaMovement(0, 0, 0);

            MC.player.setDeltaMovement(MC.player.getDeltaMovement().x(), .4 / 20 + -glideSpeedHold.get() / 20, MC.player.getDeltaMovement().z());
            if (MC.player.touchingUnloadedChunk()) MC.player.setDeltaMovement(0,0,0);
        }
    }

    /**
     * Doesn't require jumping to work. Still bypasses GRIM
     */
    public void ground() {
        if (MC.options.keyUp.isDown() && MC.player.inventoryMenu.getSlot(6).getItem().getItem().equals(Items.ELYTRA)) {
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
                if (hMom <= speedCapGround.get() && !MC.options.keyShift.isDown()) MC.player.addDeltaMovement(new Vector(yaw, pitch, acceleration).getVec3());

                if (MC.player.touchingUnloadedChunk()) MC.player.setDeltaMovement(0,0,0);
            }
        } else {
            MixinHooks.keepElytraOnGround = false;
        }
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

    //TODO: Replace this with a mixin accessor
    private void startUseItem() {
        if (MC.gameMode.isDestroying()) {
            return;
        }
        //MC.rightClickDelay = 4;
        if (MC.player.isHandsBusy()) {
            return;
        }
        if (MC.hitResult == null) {
            //LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (InteractionHand interactionHand : InteractionHand.values()) {
            InteractionResult interactionResult3;
            ItemStack itemStack = MC.player.getItemInHand(interactionHand);
            if (!itemStack.isItemEnabled(MC.level.enabledFeatures())) {
                return;
            }
            if (MC.hitResult != null) {
                switch (MC.hitResult.getType()) {
                    case ENTITY: {
                        EntityHitResult entityHitResult = (EntityHitResult) MC.hitResult;
                        Entity entity = entityHitResult.getEntity();
                        if (!MC.level.getWorldBorder().isWithinBounds(entity.blockPosition())) {
                            return;
                        }
                        InteractionResult interactionResult = MC.gameMode.interactAt(MC.player, entity, entityHitResult, interactionHand);
                        if (!interactionResult.consumesAction()) {
                            interactionResult = MC.gameMode.interact(MC.player, entity, interactionHand);
                        }
                        if (!interactionResult.consumesAction()) break;
                        if (interactionResult.shouldSwing()) {
                            MC.player.swing(interactionHand);
                        }
                        return;
                    }
                    case BLOCK: {
                        BlockHitResult blockHitResult = (BlockHitResult) MC.hitResult;
                        int i = itemStack.getCount();
                        InteractionResult interactionResult2 = MC.gameMode.useItemOn(MC.player, interactionHand, blockHitResult);
                        if (interactionResult2.consumesAction()) {
                            if (interactionResult2.shouldSwing()) {
                                MC.player.swing(interactionHand);
                                if (!itemStack.isEmpty() && (itemStack.getCount() != i || MC.gameMode.hasInfiniteItems())) {
                                    MC.gameRenderer.itemInHandRenderer.itemUsed(interactionHand);
                                }
                            }
                            return;
                        }
                        if (interactionResult2 != InteractionResult.FAIL) break;
                        return;
                    }
                }
            }
            if (itemStack.isEmpty() || !(interactionResult3 = MC.gameMode.useItem(MC.player, interactionHand)).consumesAction())
                continue;
            if (interactionResult3.shouldSwing()) {
                MC.player.swing(interactionHand);
            }
            MC.gameRenderer.itemInHandRenderer.itemUsed(interactionHand);
            return;
        }
    }

}
