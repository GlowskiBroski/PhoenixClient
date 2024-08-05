package com.phoenixclient.gui.hud;

import com.phoenixclient.gui.GUI;
import com.phoenixclient.gui.hud.element.*;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class HUDGUI extends GUI {

    // DEFINE WINDOWS
    // -----------------------------------------------------------------

    private final ArmorWindow armor = new ArmorWindow(this, Vector.NULL());
    private final CoordinatesWindow coordinates = new CoordinatesWindow(this, Vector.NULL());
    private final DirectionWindow direction = new DirectionWindow(this, Vector.NULL());
    private final FPSWindow fps = new FPSWindow(this, Vector.NULL());
    private final InventoryWindow inventory = new InventoryWindow(this, Vector.NULL());
    private final SpeedWindow speed = new SpeedWindow(this, Vector.NULL());
    private final TPSWindow tps = new TPSWindow(this, Vector.NULL());
    private final ModuleListWindow moduleList = new ModuleListWindow(this, Vector.NULL());

    //Optional windows: TODO: Add the ability to disable these shitters
    private final EntityListWindow entityList = new EntityListWindow(this, Vector.NULL()); //Disable by default
    private final EntityDataWindow entityData = new EntityDataWindow(this, Vector.NULL()); //Disable by default
    private final StorageListWindow storageList = new StorageListWindow(this, Vector.NULL()); //Disable by default
    private final ModuleKeybindListWindow moduleKeybindListWindow = new ModuleKeybindListWindow(this, Vector.NULL()); //Disable by default
    private final SignTextListWindow signTextListWindow = new SignTextListWindow(this, Vector.NULL()); //Disable by default
    private final PacketFlowListWindow packetFlowList = new PacketFlowListWindow(this, Vector.NULL()); //Disable by default
    private final ChunkTrailsWindow chunkRadar = new ChunkTrailsWindow(this, Vector.NULL()); //Disable by default

    private final LogoWindow logoWindow = new LogoWindow(this, Vector.NULL());

    // -----------------------------------------------------------------

    public HUDGUI(Component title) {
        super(title);
        addGuiElements(
                inventory,
                armor,

                fps,
                tps,
                direction,
                speed,
                coordinates,

                entityList,
                storageList,
                signTextListWindow,
                //packetFlowList,
                chunkRadar,

                moduleKeybindListWindow,
                moduleList,

                entityData,

                logoWindow
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        drawHintText(guiGraphics, "Shift Click a window to see options!");
    }

}
