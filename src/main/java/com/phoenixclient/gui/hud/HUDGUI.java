package com.phoenixclient.gui.hud;

import com.phoenixclient.gui.GUI;
import com.phoenixclient.gui.hud.element.*;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

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
                new SpeedWindow(this),
                new CoordinatesWindow(this),

                new InventoryWindow(this),
                new ArmorWindow(this),

                new ModuleListWindow(this),
                new EntityListWindow(this),
                new StorageListWindow(this),
                new SignTextListWindow(this),
                //new PacketFlowListWindow(this, Vector.NULL()),

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
    }

    private void addWindows(GuiWindow... windows) {
        windowList.addAll(Arrays.asList(windows));
    }

    public ArrayList<GuiWindow> getWindows() {
        return windowList;
    }
}
