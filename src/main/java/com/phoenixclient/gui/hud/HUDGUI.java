package com.phoenixclient.gui.hud;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.GUI;
import com.phoenixclient.gui.hud.element.*;
import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.phoenixclient.PhoenixClient.MC;

public class HUDGUI extends GUI {

    private final ArrayList<GuiWindow> windowList = new ArrayList<>();

    public HUDGUI(Component title) {
        super(title);
        addWindows(
                new FPSWindow(this),
                new TPSWindow(this),
                new DirectionWindow(this),
                new RotationWindow(this),
                new SpeedWindow(this),
                new CoordinatesWindow(this),

                new InventoryWindow(this),
                new ArmorWindow(this),

                new ModuleListWindow(this),
                new EntityListWindow(this),
                new StorageListWindow(this),
                new SignTextListWindow(this),
                new PacketFlowListWindow(this),

                new ModuleKeybindListWindow(this),

                new EntityDataWindow(this),
                new ChunkTrailsWindow(this),

                new LogoWindow(this, Vector.NULL())
        );

        addGuiElements(windowList.toArray(new GuiWindow[0]));

        WindowToggleMenu toggleMenu = new WindowToggleMenu(this,Vector.NULL(),new Vector(100,20));
        addGuiElements(toggleMenu);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        drawHintText(guiGraphics, "Hold SPACE to open window enable/disable menu!\\nShift Click a window to see options!");

        //Display Center Bars
        if (PhoenixClient.getGuiManager().guideBars.get()) {
            boolean isDraggingElement = false;
            for (GuiWindow window : getWindows()) {
                if (window.isDragging()) {
                    isDraggingElement = true;
                    break;
                }
            }
            if (isDraggingElement) {
                Color highlight = new Color(255, 255, 255, 50);
                DrawUtil.drawRectangle(guiGraphics, new Vector(MC.getWindow().getGuiScaledWidth() / 2, 0), new Vector(1, MC.getWindow().getGuiScaledHeight()), highlight);
                DrawUtil.drawRectangle(guiGraphics, new Vector(0, MC.getWindow().getGuiScaledHeight() / 2), new Vector(MC.getWindow().getGuiScaledWidth(), 1), highlight);
            }
        }
    }

    private void addWindows(GuiWindow... windows) {
        windowList.addAll(Arrays.asList(windows));
    }

    public ArrayList<GuiWindow> getWindows() {
        return windowList;
    }

}
