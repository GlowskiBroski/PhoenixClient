package com.phoenixclient.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.gui.hud.HUDGUI;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.gui.module.ModuleGUI;
import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.FontRenderer;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ConcurrentModificationException;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: Maybe add a set of boolean for each window to be disabled? Maybe not?
public class GuiManager extends Module {

    public static final KeyMapping MODULE_KEY_MAPPING = new KeyMapping("Module GUI Toggle", GLFW.GLFW_KEY_RIGHT_CONTROL, "Phoenix Client");
    public static final KeyMapping HUD_KEY_MAPPING = new KeyMapping("HUD GUI Toggle", GLFW.GLFW_KEY_RIGHT_ALT, "Phoenix Client");

    private HUDGUI hudGUI;
    private ModuleGUI moduleGUI;

    /**
     * This action is subscribed on instantiation and detects for key presses to open any of the client's GUIs
     */
    public EventAction constUpdateGuiOpen = new EventAction(Event.EVENT_KEY_PRESS, this::updateGuiOpen);

    private boolean isFirstEnable = true;

    private final SettingGUI<String> font = new SettingGUI<>(
            this,
            "Font",
            "Custom font for the HUD",
            "Arial").setModeData("Segoe Print", "Arial", "Verdana", "Impact", "Default");

    public final SettingGUI<Boolean> blur = new SettingGUI<>(
            this,
            "Blur",
            "Adds a blur effect to the background of the GUI menus",
            true);

    private final SettingGUI<String> theme = new SettingGUI<>(
            this,
            "Theme",
            "Color theme for the HUD",
            "Sea Green").setModeData("Red", "Orange", "Green", "Sea Green", "Blue", "Light Blue", "Purple", "Rainbow", "Custom");

    public final SettingGUI<Double> baseColorHue = new SettingGUI<>(
            this,
            "Base Color",
            "Hue for the Base Color of the theme",
            .52d).setSliderData(0, 1, .01).setDependency(theme, "Custom");

    public final SettingGUI<Double> depthColorHue = new SettingGUI<>(
            this,
            "Depth Color",
            "Hue for the Base Color of the theme",
            .59d).setSliderData(0, 1, .01).setDependency(theme, "Custom");

    public final SettingGUI<Double> widgetColorHue = new SettingGUI<>(
            this,
            "Widget Color",
            "Hue for the Base Color of the theme",
            .52d).setSliderData(0, 1, .01).setDependency(theme, "Custom");

    public GuiManager() {
        super("GUI", "GUI manager for Phoenix Client", Category.RENDER, true, -1);
        addSettings(font, theme, blur, baseColorHue, depthColorHue, widgetColorHue);
        addEventSubscriber(Event.EVENT_RENDER_HUD, (event) -> {
            font.runOnChange(() -> PhoenixClient.setFontRenderer(new FontRenderer(font.get(), Font.PLAIN)));
            theme.runOnChange(() -> {
                ColorManager.Theme theme = switch (this.theme.get()) {
                    case "Red" -> ColorManager.Theme.RED;
                    case "Orange" -> ColorManager.Theme.ORANGE;
                    case "Green" -> ColorManager.Theme.GREEN;
                    case "Sea Green" -> ColorManager.Theme.SEAGREEN;
                    case "Blue" -> ColorManager.Theme.BLUE;
                    case "Light Blue" -> ColorManager.Theme.LIGHTBLUE;
                    case "Purple" -> ColorManager.Theme.PURPLE;
                    default -> ColorManager.Theme.LIGHTBLUE;
                };
                PhoenixClient.getColorManager().setTheme(theme);
                PhoenixClient.getColorManager().setRainbow(this.theme.get().equals("Rainbow"));
                PhoenixClient.getColorManager().setCustom(this.theme.get().equals("Custom"));
            });
        });
        addEventSubscriber(Event.EVENT_RENDER_HUD, this::renderHud);
    }

    @Override
    public boolean showInList() {
        return false;
    }

    @Override
    public void onEnabled() {
        if (isFirstEnable) {
            PhoenixClient.getNotificationManager().sendNotification("Press " + Component.translatable(GuiManager.MODULE_KEY_MAPPING.saveString()).getString() + " to open the module menu!",Color.WHITE,.25f);
            PhoenixClient.getNotificationManager().sendNotification("Change this in default Minecraft controls menu!",Color.WHITE,.25f);
            isFirstEnable = false;
        }
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

    private void updateGuiOpen() {
        int key = Event.EVENT_KEY_PRESS.getKey();
        int action = Event.EVENT_KEY_PRESS.getState();

        if (action != GLFW.GLFW_PRESS) return;

        if (key == Key.KEY_ESC.getId())
            PhoenixClient.getSettingManager().saveAll(); //Whenever the GUI closes, save all settings

        String mapping = InputConstants.getKey(key, action).getName();
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