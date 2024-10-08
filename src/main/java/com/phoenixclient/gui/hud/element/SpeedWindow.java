package com.phoenixclient.gui.hud.element;

import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class SpeedWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    public SettingGUI<String> mode;
    public SettingGUI<String> units;

    public SpeedWindow(Screen screen) {
        super(screen, "SpeedWindow", "Displays the players current speed, in blocks (m) per second", Vector.NULL(),true);
        this.label = new SettingGUI<>(this, "Label", "Show the label", true);
        this.mode = new SettingGUI<>(this, "Mode", "Detects whether speed is in XY or XYZ", "2D").setModeData("2D", "3D");
        this.units = new SettingGUI<>(this, "Units", "The units that the speed is given in", "m/s").setModeData("m/s", "km/hr","mph");
        addSettings(label, mode,units);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        String label = (this.label.get() ? "Speed " : "");

        double rawSpeed = getEntitySpeed(MC.player,mode.get().equals("3D"));
        double speed = switch (units.get()) {
            case "km/hr" -> rawSpeed * 60 * 60 / 1000; //km/hr is 1000 blocks per hour
            case "mph" -> rawSpeed * 2.23694; //mph is completely useless in minecraft
            default -> rawSpeed; //m/s is blocks per second
        };

        String currentSpeed = String.format("%.2f", speed) + units.get();

        setSize(new Vector((int) DrawUtil.getFontTextWidth(label + currentSpeed) + 6, 12));

        TextBuilder.start(label,getPos().getAdded(new Vector(2, 2)),colorManager.getHudLabelColor()).draw(graphics).nextAdj().text(currentSpeed).color(Color.WHITE).dynamic().draw(graphics);
    }

    public static double getEntitySpeed(Entity entity, boolean includeY) {
        if (entity == null) return 0;
        double tickDistX = entity.getX() - entity.xo;
        double tickDistY = includeY ? (entity.getY() - entity.yo) : 0;
        double tickDistZ = entity.getZ() - entity.zo;

        //double tickDistX = entity.getDeltaMovement().x();
        //double tickDistY = includeY ? entity.getDeltaMovement().y() : 0;
        //double tickDistZ = entity.getDeltaMovement().z();
        return 20 * Mth.sqrt((float) (Math.pow(tickDistX, 2) + Math.pow(tickDistY, 2) + Math.pow(tickDistZ, 2)));
    }
}