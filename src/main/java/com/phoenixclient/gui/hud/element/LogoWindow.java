package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.Setting;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.setting.SettingManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class LogoWindow extends GuiWindow {

    private final SettingGUI<Double> scale;

    public LogoWindow(Screen screen, Vector pos) {
        super(screen, "LogoWindow", pos, Vector.NULL());
        this.scale = new SettingGUI<>(this, "Scale", "Scale of the logo", 1.3d).setSliderData(.1f,2,.1);
        addSettings(scale);

        //Default Setting Overrides
        SettingManager manager = PhoenixClient.getSettingManager();
        this.pinned = new Setting<>(manager, getTitle() + "_pinned", true); //default to be true for logo
        this.posScale = new Setting<>(manager, getTitle() + "_posScale", new Vector(0, 0)); //Default to 0,0
        this.drawBackground = new SettingGUI<>(this, "Draw Background", "Draws a dark background around a HUD element", true); //Default to false
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        Vector imgSize = new Vector(160,60).getMultiplied(scale.get());
        Vector windowSize = new Vector(imgSize.getX() * 126/160,imgSize.getY() * 37/60);

        ResourceLocation location = ResourceLocation.fromNamespaceAndPath("phoenixclient","logo.png"); //1600 x 600
        DrawUtil.drawTexturedRect(graphics,location,getPos().getSubtracted(18 * scale.get(),10 * scale.get()),imgSize);
        setSize(windowSize);
    }

}
