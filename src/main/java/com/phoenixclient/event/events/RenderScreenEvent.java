package com.phoenixclient.event.events;

import com.phoenixclient.event.Event;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.GuiGraphics;

public class RenderScreenEvent extends Event {

    private GuiGraphics graphics;
    private Vector mousePos;

    @Override
    public void post(Object... args) {
        this.graphics = (GuiGraphics) args[0];
        this.mousePos = new Vector((int) args[1], (int) args[2]);
        super.post(args);
    }

    public Vector getMousePos() {
        return mousePos;
    }

    public GuiGraphics getGraphics() {
        return graphics;
    }


}
