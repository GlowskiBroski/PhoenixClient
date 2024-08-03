package com.phoenixclient.module;

import com.google.common.collect.Lists;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderItemTooltipEvent;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;

import static com.phoenixclient.PhoenixClient.MC;

public class Tooltips extends Module {

    private final SettingGUI<Boolean> nbt = new SettingGUI<>(
            this,
            "NBT Data",
            "Draws all NBT data on the left of the screen",
            false
    );

    private final SettingGUI<Boolean> trueDurability = new SettingGUI<>(
            this,
            "True Durability",
            "Shows the true durability of items outside of their forced range",
            true
    );

    private final SettingGUI<Boolean> showRepairCost = new SettingGUI<>(
            this,
            "Repair Cost",
            "Shows the repair cost of an item if available",
            true
    );

    public Tooltips() {
        super("Tooltips", "Renders better tooltips for specific items", Category.RENDER, false, -1);
        addSettings(nbt, trueDurability, showRepairCost);
        addEventSubscriber(Event.EVENT_RENDER_INVENTORY_ITEM_TOOLTIP, this::onRenderTooltip);
    }


    public void onRenderTooltip(RenderItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        //True Durability
        MixinHooks.showTrueDurability = trueDurability.get();

        //Draw NBT
        boolean isShulkerBox = stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
        if (nbt.get() && !stack.getItem().equals(Items.WRITTEN_BOOK) && !isShulkerBox) drawNbtData(stack);

        //Enchantment Cost
        if (showRepairCost.get() && stack.getOrDefault(DataComponents.REPAIR_COST,0) > 0) {
            int cost = stack.getOrDefault(DataComponents.REPAIR_COST,0);

            float scale = Math.clamp(1 - cost/31f,0,1);

            Color color = ColorManager.getRedGreenScaledColor(scale);

            event.getList().add(Component.literal("Repair Cost: " + cost).withColor(color.hashCode()));
        }


    }

    @Override
    public void onDisabled() {
        MixinHooks.showTrueDurability = false;
    }

    //This is SO janky, but it works lol
    private void drawNbtData(ItemStack stack) {
        GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());
        String str = stack.save(MC.level.registryAccess()).getAsString();
        Vector pos = new Vector(2, 2);

        float scale = .5f;
        int prevI = 0;
        for (int i = 0; i < str.toCharArray().length; i++) {
            char c = str.toCharArray()[i];
            String print = str.substring(prevI, Math.clamp(i + 1, 0, str.length() - 1));

            if (c == '{') {
                TextBuilder.start(print, pos, Color.WHITE).defaultFont().scale(scale).draw(graphics);
                prevI = Math.clamp(i + 1, 0, str.length());
                pos.add(new Vector(10, 10));
            }
            if (c == '}') {
                if (!print.equals("}") && print.indexOf("}") == print.length() - 1) {
                    print = print.replace("}", "");
                }

                int bracketCount = 0;
                for (char y : print.toCharArray()) if (y == '}') bracketCount++;
                if (bracketCount > 1) print = print.substring(0, print.length() - 1);

                TextBuilder.start(print, pos, Color.WHITE).defaultFont().scale(scale).draw(graphics);
                prevI = Math.clamp(i, 0, str.length());
                pos.add(new Vector(-10, 10));
            }
        }
        TextBuilder.start("}", pos, Color.WHITE).defaultFont().scale(scale).draw(graphics);
    }
}