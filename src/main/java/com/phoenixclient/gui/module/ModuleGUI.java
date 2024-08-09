package com.phoenixclient.gui.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.GUI;
import com.phoenixclient.gui.GuiManager;
import com.phoenixclient.gui.module.element.ModuleOptionsMenu;
import com.phoenixclient.gui.module.element.ModuleToggle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.gui.module.element.ModuleMenu;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;

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


        // TEST DRAWING A BETTER GUI WINDOW COLOR PALETTE: TODO: Work on this more

        if (true) return;

        Vector pos = new Vector(2,400);
        Vector size = new Vector(60,50);

        Color base = new Color(Color.HSBtoRGB(.50f,200/255f,200/255f));
        Color widget = new Color(Color.HSBtoRGB(.54f,200/255f,255/255f));

        //Background
        Vector backgroundPos = pos.getAdded(pos.getX() + size.getX() - 9, 3);
        //Vector backgroundSize = new Vector((double) (62 * (buttonList.size() % 2 == 0 ? buttonList.size() : buttonList.size() + 1)) / 2 + 9 + 4, size.getY() - 6);
        Vector backgroundSize = new Vector(372 + 9 + 4, size.getY() - 6);
        Color backgroundColor = new Color(25, 25, 25, 175);
        DrawUtil.drawRectangleRound(guiGraphics, backgroundPos, backgroundSize, backgroundColor, 1.5, false);
        DrawUtil.drawRectangleRound(guiGraphics, backgroundPos, backgroundSize, base, 1.5, true);
        DrawUtil.drawArrowHead(guiGraphics, backgroundPos.getAdded(backgroundSize).y(pos.getY() + 3), (float) size.getY() - 6, backgroundColor, false, false);


        //Buttons
        boolean even;
        int i = 0;
        int j = 0;
        for (Module module : PhoenixClient.getModules()) {
            if (module.getCategory().equals(Module.Category.MOTION)) {
                even = j % 2 == 0;
                DrawUtil.drawRectangleRound(guiGraphics,pos.getAdded(pos.getX() + size.getX() + 4 + (62 * (even ? j - i : i)), even ? 5 : 26), new Vector(60, 19),widget);
                j++;
                if (!even) i++;
            }
        }


        //Body
        DrawUtil.drawArrow(guiGraphics, pos, (float)size.getY(), base, false);
        //DrawUtil.drawArrow(guiGraphics, pos, (float)size.getY(), new Color(0, 194, 255), true);
        DrawUtil.drawArrow(guiGraphics, pos.getAdded(size.getMultiplied(.25 / 1.5)).getAdded(2, 0), (float) size.getY() / 1.5f, new Color(Color.HSBtoRGB(.61f,175/255f,100/255f)), false);

        //Title
        Vector cpos = pos.getAdded(new Vector(size.getX() / 2 - DrawUtil.getFontTextWidth("Title") / 2, 1 + size.getY() / 2 - DrawUtil.getFontTextHeight() / 2));
        TextBuilder.start("Title", cpos.getAdded(7, 0), Color.WHITE).scale((float) 1).draw(guiGraphics);



    }

}
