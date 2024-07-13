package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;

import java.awt.*;

public class DirectionWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    private final SettingGUI<String> mode;

    public DirectionWindow(Screen screen, Vector pos) {
        super(screen, "DirectionWindow", pos, new Vector(60, 13));
        this.label = new SettingGUI<>(this, "Label","Show the label",true);
        this.mode = new SettingGUI<>(this,"Mode","Mode of Direction","All").setModeData("All","Compass","Coordinate");
        addSettings(label,mode);
    }

    @Override
    public void drawWindow(GuiGraphics graphics, Vector mousePos) {
        if (Minecraft.getInstance().player == null) return;

        Direction direction = Minecraft.getInstance().player.getDirection();
        String coordinateDirection = switch (direction) {
            case NORTH -> "[-Z]";
            case SOUTH -> "[+Z]";
            case WEST -> "[-X]";
            case EAST -> "[+X]";
            default -> "NULL";
        };

        String cardinalDirection = switch (direction) {
            case NORTH -> "North";
            case SOUTH -> "South";
            case WEST -> "West";
            case EAST -> "East";
            default -> "NULL";
        };

        String label = (this.label.get() ? "Direction " : "");
        String directionString = "";
            if (mode.get().equals("All")) directionString = coordinateDirection + " " + cardinalDirection;
            if (mode.get().equals("Compass")) directionString = cardinalDirection;
            if (mode.get().equals("Coordinate")) directionString = coordinateDirection;

        setSize(new Vector((int) DrawUtil.getFontTextWidth(label + directionString) + 6, getSize().getY()));

        Vector pos = new Vector(
                getPos().getX() + getSize().getX() / 2 - DrawUtil.getFontTextWidth(label + directionString) / 2,
                getPos().getY() + 2);
        DrawUtil.drawDualColorFontText(graphics, label, directionString, pos, ColorUtil.HUD_LABEL, Color.WHITE);
    }

}
