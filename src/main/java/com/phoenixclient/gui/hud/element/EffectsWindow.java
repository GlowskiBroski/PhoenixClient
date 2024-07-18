package com.phoenixclient.gui.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;

import static com.phoenixclient.PhoenixClient.MC;

public class EffectsWindow extends ListWindow {


    public EffectsWindow(Screen screen, Vector pos) {
        super(screen, "EffectsWindow",pos);
    }

    @Override
    protected LinkedHashMap<String, ListInfo> getListMap() {
        return null;
    }

    @Override
    protected String getLabel() {
        return "Effects List";
    }
}
