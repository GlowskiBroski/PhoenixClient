package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import static com.phoenixclient.PhoenixClient.MC;

public class BoatMod extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of BoatMod",
            "Jump")
            .setModeData("Jump","Fly");

    private final SettingGUI<Double> power = new SettingGUI<>(
            this,
            "Power",
            "Jump Power",
            1d)
            .setSliderData(.1,2,.1)
            .setDependency(mode,"Jump");

    private final SettingGUI<Double> speed = new SettingGUI<>(
            this,
            "Speed",
            "Fly Speed",
            1d)
            .setSliderData(.1,5,.05)
            .setDependency(mode,"Fly");

    private final SettingGUI<Double> glideSpeed = new SettingGUI<>(
            this,
            "Glide Speed",
            "Boatfly Glide Speed",
            0d)
            .setSliderData(0,2,.1)
            .setDependency(mode,"Fly");

    private final SettingGUI<Boolean> phase = new SettingGUI<>(
            this,
            "Phase",
            "Enables NoClip for the boat",
            false).setDependency(mode,"Fly");

    private final SettingGUI<Boolean> yawlock = new SettingGUI<>(
            this,
            "Yaw Lock",
            "Have the boat lock its YAW to the player's",
            false);

    //TODO: Add boat strafing option

    public BoatMod() {
        super("BoatMod", "Allows for different boat movement types", Category.MOTION, false, -1);
        addSettings(mode,power, yawlock,speed, glideSpeed,phase);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (MC.player.getVehicle() != null && MC.player.getVehicle() instanceof Boat boat) {

            if (yawlock.get()) MC.player.getVehicle().setYRot(MC.player.getYRot());

            switch (mode.get()) {
                case "Jump" -> {
                    boat.setNoGravity(false);
                    if (phase.get()) MixinHooks.mountNoClip = false;
                    if (boat.onGround() && MC.options.keyJump.isDown()) boat.addDeltaMovement(new Vec3(0,power.get() * 1/3,0));
                }
                case "Fly" -> {
                    MixinHooks.mountNoClip = phase.get();
                    boat.setNoGravity(true);
                    MotionUtil.setEntityMotionInLookDirection(boat,speed.get());

                    boolean isVerticalInputDown = MC.options.keySprint.isDown() || MC.options.keyJump.isDown();
                    if (!isVerticalInputDown)
                        boat.setDeltaMovement(boat.getDeltaMovement().x(),-glideSpeed.get() / 20,boat.getDeltaMovement().z());
                }
            }
        } else {
            if (phase.get()) MixinHooks.mountNoClip = false;
        }
    }

    @Override
    public void onEnabled() {
        if (mode.get().equals("Fly") && phase.get()) MixinHooks.mountNoClip = true;
    }

    @Override
    public void onDisabled() {
        if (MC.player.getVehicle() != null) MC.player.getVehicle().setNoGravity(false);
        MixinHooks.mountNoClip = false;
    }

}
