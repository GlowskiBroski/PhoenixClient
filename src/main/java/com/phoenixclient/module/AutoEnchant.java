package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderScreenEvent;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: Add way to automatically sort all unenchanted items in inventory

public class AutoEnchant extends Module {

    public AutoEnchant() {
        super("AutoEnchant", "Automatically moves lapis to the enchantment table", Category.SERVER, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE, this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (!(MC.screen instanceof EnchantmentScreen e)) return;
        NonNullList<ItemStack> inv = MC.player.containerMenu.getItems();
        if (inv.get(1).getItem().equals(Items.LAPIS_LAZULI)) return;
        for (int i = 0; i < inv.size(); i++) {
            if (inv.get(i) == ItemStack.EMPTY) continue;
            if (inv.get(i).getItem() == Items.LAPIS_LAZULI) {
                clickWindow(e.getMenu(),i);
                clickWindow(e.getMenu(),1);
                break;
            }
        }

        //If you're somehow still holding the item, put it down
        if (e.getMenu().getCarried().getItem().equals(Items.LAPIS_LAZULI)) clickWindow(e.getMenu(), 1);
    }


    private void clickWindow(AbstractContainerMenu menu, int index) {
        MC.gameMode.handleInventoryMouseClick(menu.containerId, index, 0, ClickType.PICKUP, MC.player);
    }

}

