package com.phoenixclient.gui.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.MathUtil;
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
        setSize(new Vector(60,40));
        //TODO: Have a display that says: Time till day/night: 00:00 mins

        double dayPercentage = (double) (MC.level.dayTime()) / 24000;
        while (dayPercentage > 1) dayPercentage -= 1;

        Vector center = getPos().getAdded(getSize().getMultiplied(.5)).y(getPos().getY() + getSize().getY() - 12);

        boolean isDay = dayPercentage < .50;

        if (drawBackground.get()) DrawUtil.drawRectangleRound(graphics,getPos(),getSize(),isDay ? new Color(58, 171, 243) : Color.BLACK);

        int mul = 20;
        Vector unit = new Angle(dayPercentage * 360,true).getUnitVector().getMultiplied(mul);

        Vector sunPos = center.getSubtracted(unit);
        Vector moonPos = center.getAdded(unit);

        //SUN
        if (sunPos.getY() - 6 < getPos().getY() + getSize().getY() - 12) {
            DrawUtil.drawRectangle(graphics, sunPos.getSubtracted(6, 6), new Vector(12, 12), Color.YELLOW);
            DrawUtil.drawRectangle(graphics, sunPos.getSubtracted(4, 4), new Vector(8, 8), new Color(229, 200, 59));
        }

        //MOON
        if (moonPos.getY() - 6 < getPos().getY() + getSize().getY() - 12) {
            DrawUtil.drawRectangle(graphics, moonPos.getSubtracted(6, 6), new Vector(12, 12), Color.WHITE);
            DrawUtil.drawRectangle(graphics, moonPos.getSubtracted(4, 4), new Vector(8, 8), new Color(124, 124, 124));
        }

        //GROUND
        DrawUtil.drawRectangleRound(graphics,getPos().getAdded(0,getSize().getY() - 12),new Vector(getSize().getX(),12),new Color(42, 147, 0));

        double timeToNext = MathUtil.roundDouble(isDay ? (.497 - dayPercentage) * 20 : (1 - dayPercentage) * 20,1);
        String dayTime = !isDay ? "Day in: " + timeToNext + "min" : "Night in: " + timeToNext + "min";
        TextBuilder.start(dayTime,getPos().getAdded(2,getSize().getY() - 5 - 3),Color.WHITE).scale(.5f).defaultFont().draw(graphics);

        int dayCount = (int) (MC.level.dayTime() / 24000);;
        TextBuilder.start("D:" + dayCount,getPos().getAdded(getSize().getX() - DrawUtil.getDefaultTextWidth("D:" + dayCount,.5) - 1,2),Color.WHITE).scale(.5f).defaultFont().draw(graphics);

        //Draw Clock Outline
        if (drawBackground.get()) DrawUtil.drawRectangleRound(graphics,getPos(),getSize(),Color.GRAY,true);
    }

}
