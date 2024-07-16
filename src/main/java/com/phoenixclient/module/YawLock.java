package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.math.Vector;

import static com.phoenixclient.PhoenixClient.MC;

public class YawLock extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of YAW",
            "Ordinal")
            .setModeData("Cardinal","Ordinal", "Coordinate", "Custom");

    private final SettingGUI<Boolean> doOnce = new SettingGUI<>(
            this,
            "Do Once",
            "Sets the rotation, then immediately disables the mod",
            false);

    private final SettingGUI<Double> custom = new SettingGUI<>(
            this,
            "Angle",
            "Custom Angle for Custom Mode",
            0d)
            .setSliderData(-180,180,1)
            .setDependency(mode,"Custom");

    private final SettingGUI<String> customX = new SettingGUI<>(
            this,
            "Custom X",
            "Custom X coordinate for Coordinate Mode",
            "0")
            .setTextData(true)
            .setDependency(mode,"Coordinate");

    private final SettingGUI<String> customZ = new SettingGUI<>(
            this,
            "Custom Z",
            "Custom Z coordinate for Coordinate Mode",
            "0")
            .setTextData(true)
            .setDependency(mode,"Coordinate");


    public YawLock() {
        super("YawLock", "Locks the rotation of the player to a desired one", Category.PLAYER, false, -1);
        addSettings(mode,doOnce, customX, customZ, custom);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onUpdate);
    }

    //Potentially replace this with a tick event because it's a little stuttery
    public void onUpdate(Event event) {
        if (MC.player != null) {

            float yaw = 0;
            switch (mode.get()) {
                case "Cardinal" -> {
                    float segments = 4f;
                    yaw = Math.round((MC.player.getRotationVector().y + 1.f) / (360 / segments)) * (360 / segments);
                }
                case "Ordinal" -> {
                    float segments = 8f;
                    yaw = Math.round((MC.player.getRotationVector().y + 1.f) / (360 / segments)) * (360 / segments);
                }
                case "Coordinate" -> {
                    Vector coordinate = new Vector(Double.parseDouble(customX.get()), MC.player.getY(),Double.parseDouble(customZ.get()));
                    Vector pos = new Vector(MC.player.position());
                    yaw = (float) coordinate.getSubtracted(pos).getUnitVector().getYaw().getDegrees();
                }
                case "Custom" -> yaw = custom.get().floatValue();
            }
            MC.player.setYRot(yaw);
            MC.player.setYHeadRot(yaw);

            if (MC.player.getVehicle() != null) MC.player.getVehicle().setYRot(yaw);
        }

        if (doOnce.get()) disable();
    }

    @Override
    public String getModTag() {
        return switch (mode.get()) {
            case "Coordinate" -> customX.get() + ", " + customZ.get();
            case "Custom" -> custom.get().toString();
            default -> "";
        };
    }

}
