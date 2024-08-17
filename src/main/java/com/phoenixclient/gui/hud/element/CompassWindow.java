package com.phoenixclient.gui.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class CompassWindow extends GuiWindow {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of the compass",
            "Compass").setModeData("Compass","Coordinate");

    public CompassWindow(Screen screen) {
        super(screen, "CompassWindow","Displays the players cardinal/coordinate direction", new Vector(60, 60),false);
        addSettings(mode);
    }

    @Override
    public void drawWindow(GuiGraphics graphics, Vector mousePos) {
        Vector center = getSize().getMultiplied(.5).getAdded(getPos());

        double mul = 20;

        Vector north = new Angle(-MC.player.getYRot() + 90,true).getUnitVector().getMultiplied(mul);
        Vector east = new Angle(-MC.player.getYRot() + 180,true).getUnitVector().getMultiplied(mul);
        Vector south = new Angle(-MC.player.getYRot() + 270,true).getUnitVector().getMultiplied(mul);
        Vector west = new Angle(-MC.player.getYRot(),true).getUnitVector().getMultiplied(mul);

        String northString = "N";
        String eastString = "E";
        String southString = "S";
        String westString = "W";

        switch (mode.get()) {
            case "Compass" -> {
                northString = "N";
                eastString = "E";
                southString = "S";
                westString = "W";
            }
            case "Coordinate" -> {
                northString = "+Z";
                eastString = "+X";
                southString = "-Z";
                westString = "-X";
            }
        }


        Vector northPos = center.getAdded(north).getSubtracted(DrawUtil.getFontTextWidth(northString)/2,DrawUtil.getFontTextHeight()/2);
        Vector eastPos = center.getAdded(east).getSubtracted(DrawUtil.getFontTextWidth(eastString)/2,DrawUtil.getFontTextHeight()/2);
        Vector southPos = center.getAdded(south).getSubtracted(DrawUtil.getFontTextWidth(southString)/2,DrawUtil.getFontTextHeight()/2);
        Vector westPos = center.getAdded(west).getSubtracted(DrawUtil.getFontTextWidth(westString)/2,DrawUtil.getFontTextHeight()/2);

        Color nullColor = drawBackground.get() ? Color.GRAY : Color.WHITE;

        Color northColor = northPos.getY() < center.getY() - 10 ? Color.GREEN : nullColor;
        Color eastColor = eastPos.getY() < center.getY() - 10 ? Color.GREEN : nullColor;
        Color southColor = southPos.getY() < center.getY() - 10 ? Color.GREEN : nullColor;
        Color westColor = westPos.getY() < center.getY() - 10 ? Color.GREEN : nullColor;

        TextBuilder.start(northString,northPos,northColor).draw(graphics);
        TextBuilder.start(eastString,eastPos,eastColor).draw(graphics);
        TextBuilder.start(southString,southPos,southColor).draw(graphics);
        TextBuilder.start(westString,westPos,westColor).draw(graphics);


    }

    private void drawLine(GuiGraphics graphics, Vector pos1, Vector pos2, Color color) {
        //Draw Direction Line
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        bufferBuilder.addVertex(matrix, (float) pos1.getX(),(float) pos1.getY(), 0).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bufferBuilder.addVertex(matrix, (float) pos2.getX(),(float) pos2.getY(), 0).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        BufferUploader.drawWithShader(bufferBuilder.build());

    }

}
