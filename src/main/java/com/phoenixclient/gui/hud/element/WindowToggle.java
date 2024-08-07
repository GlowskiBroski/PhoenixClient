package com.phoenixclient.gui.hud.element;

import com.phoenixclient.gui.element.GuiToggle;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

//TODO: This is NEARLY identical to ModuleToggle. Think about inheritance

public class WindowToggle extends GuiToggle {

    private final GuiWindow window;

    public boolean selectedSettings;
    public int selectionFade = 200;

    public WindowToggle(Screen screen, GuiWindow window, Vector pos, Vector size, ColorManager colorManager) {
        super(screen,window.getTitle().replace("Window",""),(SettingGUI<Boolean>) window.getEnabledContainer(),pos,size,colorManager);
        this.window = window;
        this.selectedSettings = false;
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        super.drawWidget(graphics, mousePos);
        //Draw Selection Blip
        if (selectedSettings && getWindow().isEnabled())
            DrawUtil.drawRectangleRound(graphics,pos.get(),size.get(),new Color(255,255,255, MathUtil.getBoundValue(selectionFade,0,255).intValue()));
    }

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (state != Mouse.ACTION_CLICK || !isMouseOver()) return;
        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT -> getWindow().toggle();
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                selectedSettings = true;
                getWindow().openSettingWindow();
            }
        }
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        if (selectionFade <= 0) {
            selectedSettings = false;
            selectionFade = 200;
        }
        if (selectedSettings) selectionFade -= speed;
    }

    public GuiWindow getWindow() {
        return window;
    }
}
