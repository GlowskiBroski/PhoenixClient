package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class RotationWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;

    public RotationWindow(Screen screen) {
        super(screen, "RotationWindow", "Displays the player Pitch/Yaw", Vector.NULL(),false);
        this.label = new SettingGUI<>(this, "Label","Show the label",true);
        addSettings(label);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        String pitchLabel = (this.label.get() ? "Pitch " : "");
        String pitchText = String.valueOf(MathUtil.roundDouble(MC.player.getXRot(),1));


        String yawLabel = (this.label.get() ? "Yaw " : "");
        String yawText = String.valueOf(MathUtil.roundDouble(MC.player.getYRot(),1));

        setSize(new Vector(Math.max((int) DrawUtil.getFontTextWidth(yawLabel + yawText),(int) DrawUtil.getFontTextWidth(pitchLabel + pitchText)) + 6,24));

        TextBuilder.start(pitchLabel,getPos().getAdded(new Vector(2,2)),colorManager.getHudLabelColor()).draw(graphics).nextAdj().text(pitchText).color(Color.WHITE).dynamic().draw(graphics);
        TextBuilder.start(yawLabel,getPos().getAdded(new Vector(2,14)),colorManager.getHudLabelColor()).draw(graphics).nextAdj().text(yawText).color(Color.WHITE).dynamic().draw(graphics);

    }

}
