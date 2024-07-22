package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderItemTooltipEvent;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class Tooltips extends Module {

    private final SettingGUI<Boolean> nbt = new SettingGUI<>(
            this,
            "NBT Data",
            "Draws all NBT data on the left of the screen",
            false
    );

    public Tooltips() {
        super("Tooltips", "Renders better tooltips for specific items", Category.RENDER, false, -1);
        addSettings(nbt);
        addEventSubscriber(Event.EVENT_RENDER_ITEM_TOOLTIP, this::onRenderTooltip);
    }

    public void onRenderTooltip(RenderItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        boolean isShulkerBox = stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
        if (nbt.get() && !stack.getItem().equals(Items.WRITTEN_BOOK) && !isShulkerBox) {
            GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());
            String str = stack.save(MC.level.registryAccess()).getAsString();
            Vector pos = new Vector(2, 2 - 10);


            String[] layers = str.split("\\{");
            for (int i = 0; i < layers.length; i++) {
                String s = layers[i];
                if (!s.isEmpty()) s = "{" + s;
                TextBuilder.start(s,pos,Color.WHITE).draw(graphics);
                pos.add(new Vector(0,10));
            }


            /* Try and get it into this format. But im stupid so i guess not
            {components:
                {"minecraft:damage":51, "minecraft:enchantments":
                    {levels:
                        {"minecraft:lure":3, "minecraft:unbreaking":3
                        }
                    }
                },count:1, id:"minecraft:fishing_rod"
            } */

        }
    }
}