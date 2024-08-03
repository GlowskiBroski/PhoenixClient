package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class ChunksRadarWindow extends GuiWindow {

    //TODO: add this. Maybe look into DynamicTexture and replace each pixel in the image with a new or old chunk?

    public ChunksRadarWindow(Screen screen, Vector pos) {
        super(screen, "ChunksRadarWindow", pos, Vector.NULL());
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {

    }

}
