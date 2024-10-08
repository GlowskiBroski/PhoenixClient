package com.phoenixclient.gui.hud.element;

import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class FPSWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;

    public FPSWindow(Screen screen) {
        super(screen, "FPSWindow", "Displays the framerate of the game", Vector.NULL(),true);
        this.label = new SettingGUI<>(this, "Label","Show the label",true);
        addSettings(label);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        String label = (this.label.get() ? "FPS " : "");
        String text =  (Minecraft.getInstance().fpsString.substring(0,3)).replace("/","").replace("f","").replace(" ","");

        setSize(new Vector((int) DrawUtil.getFontTextWidth(label + text) + 6,12));

        TextBuilder.start(label,getPos().getAdded(new Vector(2,2)),colorManager.getHudLabelColor()).draw(graphics).nextAdj().text(text).color(Color.WHITE).dynamic().draw(graphics);

    }

}
