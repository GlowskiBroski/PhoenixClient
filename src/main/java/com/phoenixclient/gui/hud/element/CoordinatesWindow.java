package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class CoordinatesWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    private final SettingGUI<Boolean> showNether;
    private final SettingGUI<String> coordinatesMode;
    private final SettingGUI<String> coordinatesSide;

    public CoordinatesWindow(Screen screen, Vector pos) {
        super(screen, "CoordinatesWindow", pos, Vector.NULL());
        this.label = new SettingGUI<>(this, "Label", "Show the label", true);
        this.showNether = new SettingGUI<>(this, "Nether", "Shows the nether coordinates conversion", false);
        this.coordinatesMode = new SettingGUI<>(this, "Mode", "Mode of coordinates", "Horizontal").setModeData("Horizontal", "Vertical");
        this.coordinatesSide = new SettingGUI<>(this, "Side", "Side of coordinates", "Left").setModeData("Left", "Right", "Center");
        addSettings(label, showNether, coordinatesMode, coordinatesSide);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        if (MC.player == null) return;

        String xLabel = (label.get() ? "X " : "");
        String yLabel = (label.get() ? "Y " : "");
        String zLabel = (label.get() ? "Z " : "");

        String currDim = MC.level.dimension().toString();
        String overworld = BuiltinDimensionTypes.OVERWORLD.toString().replace("_type", "");
        String nether = BuiltinDimensionTypes.NETHER.toString().replace("_type", "");

        boolean shouldConvert = showNether.get() && (currDim.equals(overworld) || currDim.equals(nether));
        float conversion = (currDim.equals(overworld) ? (float) 1 / 8 : 8);

        String x = String.format("%.1f", MC.player.getX()) + (shouldConvert ? " (" + String.format("%.1f", MC.player.getX() * conversion) + ")" : "");
        String y = String.format("%.1f", MC.player.getY());
        String z = String.format("%.1f", MC.player.getZ()) + (shouldConvert ? " (" + String.format("%.1f", MC.player.getZ() * conversion) + ")" : "");

        float xLength = (float) DrawUtil.getFontTextWidth(xLabel + x);
        float yLength = (float) DrawUtil.getFontTextWidth(yLabel + y);
        float zLength = (float) DrawUtil.getFontTextWidth(zLabel + z);

        Color color = Color.WHITE;
        Color labelColor = colorManager.getHudLabelColor();

        Vector startPos;

        Vector xPos = Vector.NULL();
        Vector yPos = Vector.NULL();
        Vector zPos = Vector.NULL();

        //Set Coordinate Positions and Window Size
        switch (coordinatesMode.get()) {
            case "Horizontal" -> {
                switch ((coordinatesSide.get())) {
                    case "Left" -> {
                        startPos = getPos().getAdded(2, 2);
                        xPos = startPos;
                        yPos = xPos.getAdded(xLength + 10, 0);
                        zPos = yPos.getAdded(yLength + 10, 0);
                    }
                    case "Right" -> {
                        startPos = getPos().getAdded(getSize().getX() - 3, 2);
                        zPos = startPos.getSubtracted(zLength, 0);
                        yPos = zPos.getSubtracted(10 + yLength, 0);
                        xPos = yPos.getSubtracted(10 + xLength, 0);
                    }
                    case "Center" -> {
                        startPos = getPos().getAdded(getSize().getX() / 2, 2);
                        yPos = startPos.getAdded(-yLength / 2, 0);
                        xPos = yPos.getSubtracted(xLength + 10, 0);
                        zPos = yPos.getAdded(yLength + 10, 0);
                    }
                }
                setSize(new Vector(xLength + yLength + zLength + 30, 12));
            }
            case "Vertical" -> {
                switch ((coordinatesSide.get())) {
                    case "Left" -> {
                        startPos = getPos().getAdded(2, 2);
                        xPos = startPos;
                        yPos = startPos.getAdded(0, 10);
                        zPos = startPos.getAdded(0, 20);
                    }
                    case "Right" -> {
                        startPos = getPos().getAdded(getSize().getX() - 3, 2);
                        xPos = startPos.getAdded(-xLength, 0);
                        yPos = startPos.getAdded(-yLength, 10);
                        zPos = startPos.getAdded(-zLength, 20);
                    }
                    case "Center" -> {
                        startPos = getPos().getAdded(getSize().getX() / 2, 2);
                        xPos = startPos.getAdded(-xLength / 2, 0);
                        yPos = startPos.getAdded(-yLength / 2, 10);
                        zPos = startPos.getAdded(-zLength / 2, 20);
                    }
                }
                float longestString = 0;
                if (xLength > longestString) longestString = xLength;
                if (yLength > longestString) longestString = yLength;
                if (zLength > longestString) longestString = zLength;
                setSize(new Vector(longestString + 5, 32));
            }
        }


        TextBuilder.start(xLabel, xPos, labelColor).draw(graphics).nextAdj().text(x).color(color).dynamic().draw(graphics);
        TextBuilder.start(yLabel, yPos, labelColor).draw(graphics).nextAdj().text(y).color(color).dynamic().draw(graphics);
        TextBuilder.start(zLabel, zPos, labelColor).draw(graphics).nextAdj().text(z).color(color).dynamic().draw(graphics);
    }

    @Override
    protected void updateWindowPositionFromSize() {
        //Set Window Position from the size change
        switch (coordinatesSide.get()) {
            case "Left" -> {
                //None
            }
            case "Center" -> onWidthChange.run(getSize().getX(), () -> {
                if (onWidthChange.getPrevValue() != null)
                    posScale.set(getPos().getSubtracted((getSize().getX() - onWidthChange.getPrevValue()) / 2, 0).getScaled((double) 1 / MC.getWindow().getGuiScaledWidth(), (double) 1 / MC.getWindow().getGuiScaledHeight()));

            });
            case "Right" -> onWidthChange.run(getSize().getX(), () -> {
                if (onWidthChange.getPrevValue() != null)
                    posScale.set(getPos().getSubtracted(getSize().getX() - onWidthChange.getPrevValue(), 0).getScaled((double) 1 / MC.getWindow().getGuiScaledWidth(), (double) 1 / MC.getWindow().getGuiScaledHeight()));
            });
        }
    }

}