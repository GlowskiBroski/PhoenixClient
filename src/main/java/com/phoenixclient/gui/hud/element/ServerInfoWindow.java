package com.phoenixclient.gui.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.phoenixclient.PhoenixClient.MC;

public class ServerInfoWindow extends GuiWindow {

    public ServerInfoWindow(Screen screen, Vector pos) {
        super(screen, "ServerInfoWindow",pos, new Vector(65,15));
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        //TODO: IMmlement this
        // Have Server IP, Server Type, Ping
    }
    
}
