package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.*;
import java.util.List;

import static com.phoenixclient.PhoenixClient.MC;

public class InventoryWindow extends GuiWindow {

    public SettingGUI<Integer> transparency;
    public SettingGUI<Double> scale;

    public InventoryWindow(Screen screen, Vector pos) {
        super(screen,"InventoryWindow" ,pos, new Vector(178,82));
        transparency = new SettingGUI<>(this,"Transparency","The transparency of the inventory window",125).setSliderData(0,255,1);
        scale = new SettingGUI<>(this,"Scale","The scale of the inventory window",1d).setSliderData(.25d,1d,.05d);
        addSettings(transparency,scale);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        float scale = this.scale.get().floatValue();
        setSize(new Vector(178,82).getMultiplied(scale));

        graphics.pose().scale(scale,scale,1f);

        Vector adjustedPos = getPos().getAdded(1,1).getMultiplied(1/scale);

        graphics.setColor(1,1,1,transparency.get() / 255f);
        renderChestInventory(graphics,"Inventory",adjustedPos);

        //graphics.setColor(1,1,1,transparency.get() / 255f);
        renderInventoryItems(graphics, MC.player.inventoryMenu.getItems(),9,35,adjustedPos);

        graphics.setColor(1f,1f,1f,1f);
        graphics.pose().scale(1 / scale,1 / scale,1f);
    }

    public static void renderInventoryItems(GuiGraphics graphics, List<ItemStack> list, int lowIndex, int highIndex, boolean drawTooltips, Vector mainPos, Vector mousePos) {
        int itemSize = 16;

        int i = (list.size() % 9 == 0) ? 0 : 1 + list.size()/9;
        for (int l = 0; l < list.size(); l++) {
            if (l >= lowIndex && l <= highIndex) {
                Vector itemPos = new Vector(
                        (mainPos.getX() + 8) + ((itemSize + 2) * (l % 9)),
                        (mainPos.getY() - 126) + (itemSize + 2) * (l / 9 + 1) + (itemSize + 2) * i);

                RenderSystem.disableDepthTest();
                DrawUtil.drawItemStack(graphics,list.get(l),itemPos);
                RenderSystem.enableDepthTest();

                if (drawTooltips) {
                    int mouseX = (int)mousePos.getX();
                    int mouseY = (int)mousePos.getY();
                    boolean mouseOverX = mouseX >= itemPos.getX() && mouseX <= itemPos.getX() + itemSize + 1;
                    boolean mouseOverY = mouseY >= itemPos.getY() && mouseY <= itemPos.getY() + itemSize + 1;
                    if (mouseOverX && mouseOverY) {
                        if (!list.get(l).getItem().equals(Items.AIR)) {
                            graphics.fillGradient(RenderType.guiOverlay(), (int) itemPos.getX(), (int) itemPos.getY(), (int) (itemPos.getX() + itemSize), (int) (itemPos.getY() + itemSize), -2130706433, -2130706433, 0);
                            graphics.renderTooltip(MC.font, AbstractContainerScreen.getTooltipFromItem(MC, list.get(l)), list.get(l).getTooltipImage(), mouseX, mouseY);
                        }
                    }
                }
            }
        }
    }

    public static void renderInventoryItems(GuiGraphics graphics, List<ItemStack> list, int lowIndex, int highIndex, Vector mainPos) {
        renderInventoryItems(graphics,list,lowIndex,highIndex,false,mainPos,Vector.NULL());
    }

    public static void renderChestInventory(GuiGraphics graphics, String title, Vector pos) {
        //INFO: Width = 176, Height = 80
        double x = pos.getX();
        double y = pos.getY();
        ResourceLocation GUI_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/shulker_box.png");
        RenderSystem.enableBlend();
        DrawUtil.drawTexturedRect(graphics,GUI_TEXTURE,pos.getAdded(new Vector(0,74)),new Vector(176,6),new Vector(0,160), new Vector(256,256));
        DrawUtil.drawTexturedRect(graphics,GUI_TEXTURE,pos,new Vector(176,74),Vector.NULL(), new Vector(256,256));
        TextBuilder.start(title, new Vector(x + 7, y + 6), new Color(50, 50, 50, 255)).shadowLess().defaultFont().draw(graphics);
        RenderSystem.disableBlend();
    }

}
