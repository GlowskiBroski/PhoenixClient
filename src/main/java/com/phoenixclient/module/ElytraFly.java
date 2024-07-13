package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.gui.hud.element.SpeedWindow;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import static com.phoenixclient.PhoenixClient.MC;

public class ElytraFly extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of ElytaFly",
            "Boost").setModeData("Boost","Hold");

    private final SettingGUI<Double> speed = new SettingGUI<>(
            this,
            "Speed",
            "Speed of ElytaFly",
            1d)
            .setSliderData(.1, 2, .1).setDependency(mode, "Boost");

    private final SettingGUI<Double> glideSpeed = new SettingGUI<>(
            this,
            "Glide Speed",
            "Glide speed of Hold mode",
            0d)
            .setSliderData(0, 2, .1).setDependency(mode, "Hold");

    private final SettingGUI<Integer> speedCap = new SettingGUI<>(
            this,
            "Speed Cap",
            "Top speed of hold mode",
            120)
            .setSliderData(0, 200, 5).setDependency(mode, "Hold");


    public ElytraFly() {
        super("ElytraFly", "Allows control of the Elytra", Category.MOTION, false, -1);
        addSettings(mode, speed, glideSpeed, speedCap);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    int step = 0;
    private final StopWatch watch = new StopWatch();

    public void onPlayerUpdate(Event event) {
        switch (mode.get()) {
            case "Boost" -> {
                if (MC.player.isFallFlying()) {
                    double speed = this.speed.get() * (double) 1 / 10;
                    MotionUtil.addEntityMotionInLookDirection(MC.player, speed);
                }
            }
            case "Hold" -> {
                if (MC.player.isFallFlying()) {
                    MC.player.setXRot(0);

                    int speedLimit = speedCap.get();
                    float accSpeed = 1;
                    Angle yaw = new Angle(MC.player.getRotationVector().y, true);
                    if (MC.options.keyUp.isDown()) {
                        watch.start();
                        if (watch.hasTimePassedS(2) && MC.player.getMainHandItem().getItem() instanceof FireworkRocketItem) {
                            startUseItem();
                            watch.restart();
                        }
                        //Add Auto Rocket Code Here, when W is down
                        MC.player.setDeltaMovement(new Vector(yaw, new Angle(0), .1 * step).getVec3());
                        if (step < speedLimit / 2) step += accSpeed;
                    } else {
                        step = 0;
                    }

                    if (MC.options.keyDown.isDown()) MC.player.setDeltaMovement(0, 0, 0);


                    MC.player.setDeltaMovement(MC.player.getDeltaMovement().x(), .4 / 20 + -glideSpeed.get() / 20, MC.player.getDeltaMovement().z());
                }
            }
        }
    }

    @Override
    public void onEnabled() {
        watch.restart();
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
