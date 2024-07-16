package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static com.phoenixclient.PhoenixClient.MC;

public class Step extends Module {

    private final SettingGUI<Double> stepHeight = new SettingGUI<>(
            this,
            "Height",
            "Height of step",
            1d)
            .setSliderData(.5, 5, .5);

    private final SettingGUI<Boolean> stepUp = new SettingGUI<>(
            this,
            "Up",
            "Step up blocks",
            true);

    private final SettingGUI<Boolean> stepDown = new SettingGUI<>(
            this,
            "Down",
            "Step down blocks",
            true);

    private final SettingGUI<Boolean> entityStep = new SettingGUI<>( //TODO: Implement this
            this,
            "Mount Step",
            "Applies step to mounts - UNIMPLEMENTED",
            true);

    public Step() {
        super("Step", "Automatically steps up or down block edges", Category.MOTION, false, -1);
        addSettings(stepHeight, stepUp, stepDown);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onUpdate);
    }

    public void onUpdate(Event event) {
        //Step Up
        if (stepUp.get()) {
            MC.player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(stepHeight.get());
            //if (entityStep.get()) ((VehicleEntity)MC.player.getVehicle()).getAttribute(Attributes.STEP_HEIGHT).setBaseValue(stepHeight.get());
        }

        //DownStep
        if (stepDown.get()) {
            int stepDownHeight = 0;
            BlockPos playerPos = new Vector(MC.player.position()).getBlockPos();
            for (int i = 1; i <= stepHeight.get() + 1; i++) {
                if (!MC.level.getBlockState(playerPos.below(i)).canBeReplaced()) {
                    stepDownHeight = (i - 1);
                    break;
                }
            }

            //Checks if the block is solid where you will be stepping down to
            boolean shouldStepDown = !MC.level.getBlockState(playerPos.below(stepDownHeight + 1)).canBeReplaced();

            //Actually step down
            if (MC.player.onGround() && shouldStepDown)
                MC.player.setDeltaMovement(MC.player.getDeltaMovement().add(0, -stepHeight.get(), 0));
        }
    }

    @Override
    public void onDisabled() {
        MC.player.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(.6);
    }

}
