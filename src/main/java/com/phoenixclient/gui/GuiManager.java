package com.phoenixclient.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.RenderScreenEvent;
import com.phoenixclient.gui.hud.HUDGUI;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.gui.module.ModuleGUI;
import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.FontRenderer;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ConcurrentModificationException;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: Maybe add a set of boolean for each window to be disabled? Maybe not?
//TODO: Implement Color Theming
public class GuiManager extends Module {

    public static final KeyMapping MODULE_KEY_MAPPING = new KeyMapping("Module GUI Toggle", GLFW.GLFW_KEY_RIGHT_CONTROL, "Phoenix Client");
    public static final KeyMapping HUD_KEY_MAPPING = new KeyMapping("HUD GUI Toggle", GLFW.GLFW_KEY_RIGHT_ALT, "Phoenix Client");

    private HUDGUI hudGUI;
    private ModuleGUI moduleGUI;

    /**
     * This action is subscribed on instantiation and detects for key presses to open any of the client's GUIs
     */
    public EventAction constUpdateGuiOpen = new EventAction(Event.EVENT_KEY_PRESS, this::updateGuiOpen);
    /**
     * This action is subscribed on instantiation and force draws the help hint when loading into the world
     */
    public EventAction constUpdateRenderStartingHint = new EventAction(Event.EVENT_RENDER_HUD, this::drawStartingHint);

    private final SettingGUI<String> font = new SettingGUI<>(
            this,
            "Font",
            "Custom font for the HUD",
            "Verdana").setModeData("Segoe Print", "Arial","Verdana","Impact","Default");

    private final SettingGUI<String> theme = new SettingGUI<>(
            this,
            "Theme",
            "Color theme for the HUD",
            "Sea Blue").setModeData("Sea Blue", "Blue", "Orange");

    public GuiManager() {
        super("GUI", "GUI manager for Phoenix Client", Category.RENDER, true, -1);
        addSettings(font,theme);
        addEventSubscriber(Event.EVENT_RENDER_HUD, (event) -> {
            font.runOnChange(() -> PhoenixClient.setFontRenderer(new FontRenderer(font.get(), Font.PLAIN)));
            theme.runOnChange(() -> {
                ColorManager.Theme theme = switch (this.theme.get()) {
                    case "Sea Blue" -> ColorManager.Theme.SEABLUE;
                    case "Blue" -> ColorManager.Theme.BLUE;
                    case "Orange" -> ColorManager.Theme.ORANGE;
                    default -> throw new IllegalStateException("Unexpected value: " + this.theme.get());
                };
                PhoenixClient.getColorManager().setTheme(theme);
            });
        });
        addEventSubscriber(Event.EVENT_RENDER_HUD, this::renderHud);
    }

    @Override
    public boolean showInList() {
        return false;
    }

    public void renderHud(Event event) {
        if (MC.options.hideGui) return;

        GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());
        if (MC.screen == null || !MC.screen.equals(getHudGui())) {
            for (GuiWidget element : PhoenixClient.getGuiManager().getHudGui().getGuiElementList()) {
                if (!(element instanceof GuiWindow window)) continue;
                window.setSettingsOpen(false); //Whenever the GUI is closed, close all setting windows
                if (!window.isPinned()) continue;
                element.draw(graphics, new Vector(-1, -1)); //Draw each element on the HUD with a mousePos of NULL
                element.runAnimation(9);
            }
        }
    }

    private double hintFade = 255;
    private void drawStartingHint() {
        if (hintFade > 0) {
            GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());
            hintFade -= .25;
            String hint = "Press " + Component.translatable(GuiManager.MODULE_KEY_MAPPING.saveString()).getString() + " to open the module menu!";
            DrawUtil.drawFontText(graphics, hint, new Vector((double) MC.getWindow().getGuiScaledWidth() / 2 - DrawUtil.getFontTextWidth(hint) / 2, 2), new Color(255, 255, 255, MathUtil.getBoundValue(hintFade, 0, 255).intValue()));
            String hint2 = "Change this in default Minecraft controls menu!";
            DrawUtil.drawFontText(graphics, hint2, new Vector((double) MC.getWindow().getGuiScaledWidth() / 2 - DrawUtil.getFontTextWidth(hint2) / 2, 2 + DrawUtil.getFontTextHeight() + 2), new Color(255, 255, 255, MathUtil.getBoundValue(hintFade, 0, 255).intValue()));
        }
    }

    private void updateGuiOpen() {
        int key = Event.EVENT_KEY_PRESS.getKey();
        int action = Event.EVENT_KEY_PRESS.getState();

        if (action != GLFW.GLFW_PRESS) return;

        if (key == Key.KEY_ESC.getId()) PhoenixClient.getSettingManager().saveAll(); //Whenever the GUI closes, save all settings

        String mapping = InputConstants.getKey(key,action).getName();
        if (HUD_KEY_MAPPING.saveString().equals(mapping)) getHudGui().toggleOpen();
        if (MODULE_KEY_MAPPING.saveString().equals(mapping)) getModuleGui().toggleOpen();
    }

    @Deprecated
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
}