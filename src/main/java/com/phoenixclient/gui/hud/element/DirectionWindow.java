package com.phoenixclient.gui.hud.element;

import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;

import java.awt.*;

public class DirectionWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    private final SettingGUI<String> mode;

    public DirectionWindow(Screen screen) {
        super(screen, "DirectionWindow","Displays the players cardinal/coordinate direction", new Vector(60, 13),true);
        this.label = new SettingGUI<>(this, "Label", "Show the label", false);
        this.mode = new SettingGUI<>(this, "Mode", "Mode of Direction", "All").setModeData("All", "Compass", "Coordinate");
        addSettings(label, mode);
    }

    @Override
    public void drawWindow(GuiGraphics graphics, Vector mousePos) {
        if (Minecraft.getInstance().player == null) return;

        Direction direction = Minecraft.getInstance().player.getDirection();

        String coordinateDirection;
        String cardinalDirection;
        switch (direction) {
            case NORTH -> {
                coordinateDirection = "[-Z]";
                cardinalDirection = "North";
            }
            case SOUTH -> {
                coordinateDirection = "[+Z]";
                cardinalDirection = "South";
            }
            case WEST -> {
                coordinateDirection = "[-X]";
                cardinalDirection = "West";
            }
            case EAST -> {
                coordinateDirection = "[+X]";
                cardinalDirection = "East";
            }
            default -> {
                coordinateDirection = "NULL";
                cardinalDirection = "NULL";
            }
        }

        String label = (this.label.get() ? "Direction " : "");
        String directionString = "";

        switch (mode.get()) {
            case "All" -> {
                directionString = coordinateDirection + " " + cardinalDirection;
                setSize(new Vector(DrawUtil.getFontTextWidth(label + "[+Z] South") + 2, getSize().getY()));
            }
            case "Compass" -> {
                directionString = cardinalDirection;
                setSize(new Vector(DrawUtil.getFontTextWidth(label + "South") + 2, getSize().getY()));
            }
            case "Coordinate" -> {
                directionString = coordinateDirection;
                setSize(new Vector(DrawUtil.getFontTextWidth(label + "[+Z]") + 2, getSize().getY()));
            }
        }
        Vector pos = new Vector(getPos().getX() + getSize().getX() / 2 - DrawUtil.getFontTextWidth(label + directionString) / 2 - 1, getPos().getY() + 2);
        TextBuilder.start(label, pos, colorManager.getHudLabelColor()).draw(graphics).nextAdj().text(directionString).color(Color.WHITE).draw(graphics);

    }

    protected void updateWindowPositionFromSize() {
        /* This doesn't work so swell, don't enable it for now
        onWidthChange.run(getSize().getX(), () -> {
            if (onWidthChange.getPrevValue() != null)
                posScale.set(getPos().getSubtracted((getSize().getX() - onWidthChange.getPrevValue()) / 2, 0).getScaled((double) 1 / MC.getWindow().getGuiScaledWidth(), (double) 1 / MC.getWindow().getGuiScaledHeight()));
        });
         */
    }

}
