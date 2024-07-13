package com.phoenixclient.gui;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.gui.hud.HUDGUI;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.gui.module.ModuleGUI;
import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ConcurrentModificationException;

import static com.phoenixclient.PhoenixClient.MC;

public class GuiManager {

    private HUDGUI hudGUI;
    private ModuleGUI moduleGUI;

    public Setting<Integer> hudGuiOpenKey = new Setting<>(PhoenixClient.getSettingManager(), "hudGuiOpenKey", GLFW.GLFW_KEY_SLASH);
    public Setting<Integer> moduleGuiOpenKey = new Setting<>(PhoenixClient.getSettingManager(), "moduleGuiOpenKey", GLFW.GLFW_KEY_RIGHT_SHIFT);

    /**
     * This event action is subscribed ONCE on initialization.
     * The action will check, every key press, whether to open the HUDGUI or ModuleGUI
     */
    public EventAction guiOpenAction = new EventAction(Event.EVENT_KEY_PRESS, () -> {
        int key = Event.EVENT_KEY_PRESS.getKey();
        int action = Event.EVENT_KEY_PRESS.getState();

        if (action != GLFW.GLFW_PRESS) return;

        if (key == Key.KEY_ESC.getId()) PhoenixClient.getSettingManager().saveAll(); //Whenever the GUI closes, save all settings

        if (key == hudGuiOpenKey.get()) getHudGui().toggleOpen();
        if (key == moduleGuiOpenKey.get()) getModuleGui().toggleOpen();
    });


    private double hintFade = 255;
    /**
     * This event action is subscribed ONCE on initialization.
     * The action will draw all windows when the HUD is closed
     */
    public EventAction renderHudAction = new EventAction(Event.EVENT_RENDER_HUD, () -> {
        GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());

        //Draw Starting Hint
        if (hintFade > 0) {
            hintFade -= .25;
            String hint = "Press " + "RSHIFT" + " to open the module menu!";
            DrawUtil.drawFontText(graphics, hint, new Vector((double) MC.getWindow().getGuiScaledWidth() / 2 - DrawUtil.getFontTextWidth(hint) / 2, 2), new Color(255, 255, 255, MathUtil.getBoundValue(hintFade, 0, 255).intValue()));
        }

        if (MC.screen == null || !MC.screen.equals(getHudGui())) {
            for (GuiWidget element : PhoenixClient.getGuiManager().getHudGui().getGuiElementList()) {
                if (!(element instanceof GuiWindow window)) continue;
                window.setSettingsOpen(false); //Whenever the GUI is closed, close all setting windows
                if (!window.isPinned()) continue;
                element.draw(graphics, new Vector(-1, -1)); //Draw each element on the HUD with a mousePos of NULL
                element.runAnimation(9);
            }
        }
    });

    public void instantiateHUDGUI() {
        hudGUI = new HUDGUI(Component.translatable("HUDGUI"));
    }

    public void instantiateModuleGUI() {
        moduleGUI = new ModuleGUI(Component.translatable("ModuleGUI"));
    }

    public HUDGUI getHudGui() {
        return hudGUI;
    }

    public ModuleGUI getModuleGui() {
        return moduleGUI;
    }

    public void startAnimationThread() {
        Thread animationThread = new Thread(() -> {
            while (true) {
                try {
                    int speed = 1;
                    Thread.sleep(speed);
                    for (GuiWidget widget : getHudGui().getGuiElementList()) widget.runAnimation(speed);
                    for (GuiWidget widget : getModuleGui().getGuiElementList()) widget.runAnimation(speed);
                } catch (ConcurrentModificationException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        animationThread.setName("Animation Thread");
        animationThread.setDaemon(true);
        animationThread.start();
    }

}