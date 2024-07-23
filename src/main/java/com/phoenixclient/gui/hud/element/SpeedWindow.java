package com.phoenixclient.gui.hud.element;

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

public class SpeedWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    public SettingGUI<String> mode;

    public SpeedWindow(Screen screen, Vector pos) {
        super(screen, "SpeedWindow", pos, Vector.NULL());
        this.label = new SettingGUI<>(this, "Label", "Show the label", true);
        this.mode = new SettingGUI<>(this, "Mode", "Detects whether speed is in XY or XYZ", "2D").setModeData("2D", "3D");
        addSettings(label, mode);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        String label = (this.label.get() ? "Speed " : "");
        String currentSpeed = String.format("%.2f", getEntitySpeed(Minecraft.getInstance().player, mode.get().equals("3D"))) + "m/s";

        setSize(new Vector((int) DrawUtil.getFontTextWidth(label + currentSpeed) + 6, 13));

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