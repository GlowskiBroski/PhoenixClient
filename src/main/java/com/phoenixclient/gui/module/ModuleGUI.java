package com.phoenixclient.gui.module;

import com.phoenixclient.gui.GUI;
import com.phoenixclient.gui.GuiManager;
import com.phoenixclient.gui.module.element.ModuleOptionsMenu;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.gui.module.element.ModuleMenu;
import com.phoenixclient.module.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static com.phoenixclient.PhoenixClient.MC;

public class ModuleGUI extends GUI {

    public final ModuleOptionsMenu moduleOptionsMenu = new ModuleOptionsMenu(this, Vector.NULL());

    int topHeight = 2;//32;
    private final ModuleMenu combatMenu = new ModuleMenu(this, Module.Category.COMBAT,new Vector(2,topHeight + 52 * 0));
    private final ModuleMenu playerMenu = new ModuleMenu(this, Module.Category.PLAYER,new Vector(2,topHeight + 52 * 1));
    private final ModuleMenu movementMenu = new ModuleMenu(this, Module.Category.MOTION,new Vector(2,topHeight + 52 * 2));
    private final ModuleMenu renderMenu = new ModuleMenu(this, Module.Category.RENDER,new Vector(2,topHeight + 52 * 3));
    private final ModuleMenu serverMenu = new ModuleMenu(this, Module.Category.SERVER,new Vector(2,topHeight + 52 * 4));
    private final ModuleMenu managerMenu = new ModuleMenu(this, Module.Category.MANAGERS,new Vector(2,topHeight + 52 * 5));

    public ModuleGUI(Component title) {
        super(title);
        addGuiElements(combatMenu,playerMenu, movementMenu, renderMenu,serverMenu,moduleOptionsMenu,managerMenu);
    }

    //TODO: Autoscale this gui against the gui scale: use the code you made in ShulkerView to accomplish :)

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        drawHintText(guiGraphics,"Press " + Component.translatable(GuiManager.HUD_KEY_MAPPING.saveString()).getString() + " to open the HUD menu!");
    }

}
