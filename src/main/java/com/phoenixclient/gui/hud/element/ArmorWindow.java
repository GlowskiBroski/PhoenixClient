package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.phoenixclient.PhoenixClient.MC;

public class ArmorWindow extends GuiWindow {

    public SettingGUI<String> mode;
    public SettingGUI<Double> scale;

    public ArmorWindow(Screen screen, Vector pos) {
        super(screen, "ArmorWindow",pos, new Vector(65,15));
        this.mode = new SettingGUI<>(this,"Mode","The direction the armor is rendered","Horizontal").setModeData("Horizontal","Vertical");
        this.scale = new SettingGUI<>(this,"Scale","The scale of the armor",1d).setSliderData(.25d,1d,.05d);
        addSettings(mode,scale);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        Vector nPos = new Vector((int)getPos().getX(),(int)getPos().getY());

        float scale = this.scale.get().floatValue();
        setSize(new Vector(178,82).getMultiplied(scale));
        graphics.pose().scale(scale,scale,1f);

        Vector texSize = new Vector(16,16);
        Vector addVec = Vector.NULL();
        switch (mode.get()) {
            case "Horizontal" -> {
                setSize(new Vector(65,16).getMultiplied(scale));
                addVec = new Vector(texSize.getX(),0);
            }
            case "Vertical" -> {
                setSize(new Vector(17,63).getMultiplied(scale));
                nPos.add(new Vector(1,0));
                addVec = new Vector(0,texSize.getY());
            }
        }

        nPos.multiply(1 / scale);

        if (Minecraft.getInstance().screen == getScreen()) {
            ResourceLocation helmet = ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_helmet.png");
            ResourceLocation chestplate = ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_chestplate.png");
            ResourceLocation leggings = ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_leggings.png");
            ResourceLocation boots = ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_boots.png");

            RenderSystem.setShaderColor(.2F,.2F,.2F,1F);

            DrawUtil.drawTexturedRect(graphics,helmet, nPos, texSize);
            nPos.add(addVec);
            DrawUtil.drawTexturedRect(graphics,chestplate, nPos, texSize);
            nPos.add(addVec);
            DrawUtil.drawTexturedRect(graphics,leggings, nPos, texSize);
            nPos.add(addVec);
            DrawUtil.drawTexturedRect(graphics,boots, nPos, texSize);

            RenderSystem.setShaderColor(1F,1F,1F,1F);
            nPos.add(addVec.getMultiplied(-3));
        }

        for (int j = 0; j < 4; j++) {
            ItemStack stack = getArmorItem(MC.player, j);
            DrawUtil.drawItemStack(graphics,stack,nPos);
            nPos.add(addVec);
            graphics.flush();

            //TODO: add enchantment rendering
        }
        graphics.pose().scale(1 / scale,1 / scale,1f);
    }

    private ItemStack getArmorItem(Player Player, int slot) {
        return Player.inventoryMenu.getSlot(5 + slot).getItem();
    }

    
}
