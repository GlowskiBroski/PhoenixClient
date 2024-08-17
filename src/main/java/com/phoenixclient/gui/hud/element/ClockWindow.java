package com.phoenixclient.gui.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class ClockWindow extends GuiWindow {

    public ClockWindow(Screen screen) {
        super(screen, "ClockWindow","Displays the worlds time as a nice rotating clock", new Vector(60, 13),false);
    }

    @Override
    public void drawWindow(GuiGraphics graphics, Vector mousePos) {
        setSize(new Vector(60,60));
        //TODO: Have a display that says: Time till day/night: 00:00 mins

        double dayPercentage = (double) (MC.level.dayTime() - 100) / 24000;
        while (dayPercentage > 1) dayPercentage -= 1;

        Vector center = getPos().getAdded(getSize().getMultiplied(.5));//.y(getPos().getY() + getSize().getY());


        if (dayPercentage < .50) {
            DrawUtil.drawRectangleRound(graphics,getPos(),getSize(),new Color(68, 136, 178));
        } else {
            DrawUtil.drawRectangleRound(graphics,getPos(),getSize(),new Color(0, 0, 0));
        }

        int mul = 20;

        Vector unit = new Angle(dayPercentage * 360,true).getUnitVector().getMultiplied(mul);

        DrawUtil.drawRectangle(graphics,center.getSubtracted(unit).getSubtracted(6,6),new Vector(12,12),Color.YELLOW);
        DrawUtil.drawRectangle(graphics,center.getSubtracted(unit).getSubtracted(4,4),new Vector(8,8),new Color(229, 200, 59));

        DrawUtil.drawRectangle(graphics,center.getAdded(unit).getSubtracted(6,6),new Vector(12,12),Color.WHITE);
        DrawUtil.drawRectangle(graphics,center.getAdded(unit).getSubtracted(4,4),new Vector(8,8),new Color(124, 124, 124));

        DrawUtil.drawRectangleRound(graphics,getPos().getAdded(0,getSize().getY()/2),new Vector(getSize().getX(),getSize().getY() / 2),new Color(42, 147, 0));

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
