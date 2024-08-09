package com.phoenixclient.gui.hud.element;

import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class DirectionWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    private final SettingGUI<String> mode;

    private final SettingGUI<Boolean> compass;
    private final SettingGUI<Boolean> coordinate;

    public DirectionWindow(Screen screen) {
        super(screen, "DirectionWindow","Displays the players cardinal/coordinate direction", new Vector(60, 13),true);
        this.label = new SettingGUI<>(this, "Label", "Show the label", false);
        this.mode = new SettingGUI<>(this, "Mode", "Direction type", "Cardinal").setModeData("Cardinal","Ordinal");
        this.compass = new SettingGUI<>(this, "Compass", "Enable NSEW directions", true);
        this.coordinate = new SettingGUI<>(this, "Coordinate", "Enable +-XZ directions", true);
        addSettings(label, mode, compass, coordinate);
    }

    @Override
    public void drawWindow(GuiGraphics graphics, Vector mousePos) {
        if (Minecraft.getInstance().player == null) return;

        String coordinateDirection = "";
        String cardinalDirection = "";

        switch (mode.get()) {
            case "Cardinal" -> {
                Direction direction = MC.player.getDirection();
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
            }
            case "Ordinal" -> {
                Angle yaw = new Angle(MC.player.getYRot(),true).getSimplified();
                if (yaw.getDegrees() > 360 - 22.5 || yaw.getDegrees() < 22.5) {
                    coordinateDirection = "[-Z]";
                    cardinalDirection = "South";
                }
                if (yaw.getDegrees() > 22.5 && yaw.getDegrees() < 90 - 22.5) {
                    coordinateDirection = "[-Z,-X]";
                    cardinalDirection = "SW";
                }
                if (yaw.getDegrees() > 90 - 22.5 && yaw.getDegrees() < 90 + 22.5) {
                    coordinateDirection = "[-X]";
                    cardinalDirection = "West";
                }
                if (yaw.getDegrees() > 90 + 22.5 && yaw.getDegrees() < 180 - 22.5) {
                    coordinateDirection = "[+Z,-X]";
                    cardinalDirection = "NW";
                }
                if (yaw.getDegrees() > 180 - 22.5 && yaw.getDegrees() < 180 + 22.5) {
                    coordinateDirection = "[+Z]";
                    cardinalDirection = "North";
                }
                if (yaw.getDegrees() > 180 + 22.5 && yaw.getDegrees() < 270 - 22.5) {
                    coordinateDirection = "[+Z,+X]";
                    cardinalDirection = "NE";
                }
                if (yaw.getDegrees() > 270 - 22.5 && yaw.getDegrees() < 270 + 22.5) {
                    coordinateDirection = "[+X]";
                    cardinalDirection = "East";
                }
                if (yaw.getDegrees() > 270 + 22.5 && yaw.getDegrees() < 360 - 22.5) {
                    coordinateDirection = "[-Z,+X]";
                    cardinalDirection = "SE";
                }
            }
        }


        String label = (this.label.get() ? "Direction " : "");
        String directionString = (coordinate.get() ? coordinateDirection : "") + (compass.get() ? (coordinate.get() ? " " : "") + cardinalDirection : "");

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
