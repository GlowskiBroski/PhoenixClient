package com.phoenixclient.gui.hud.element;

import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class ServerInfoWindow extends GuiWindow {

    public ServerInfoWindow(Screen screen, Vector pos) {
        super(screen, "ServerInfoWindow","Displays server IP, type, and ping", new Vector(65,15),false);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        //TODO: Implement this
        // Have Server IP, Server Type, Ping
    }
    
}
