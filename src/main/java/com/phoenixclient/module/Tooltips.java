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
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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
            Vector pos = new Vector(2, 2);

            //This is SO janky, but it works lol
            float scale = .5f;
            int prevI = 0;
            for (int i = 0; i < str.toCharArray().length; i++) {
                char c = str.toCharArray()[i];
                String print = str.substring(prevI, Math.clamp(i + 1,0,str.length() - 1));

                if (c == '{') {
                    TextBuilder.start(print, pos, Color.WHITE).defaultFont().scale(scale).draw(graphics);
                    prevI = Math.clamp(i + 1,0,str.length());
                    pos.add(new Vector(10, 10));
                }
                if (c == '}') {
                    if (!print.equals("}") && print.indexOf("}") == print.length() - 1) {
                        print = print.replace("}","");
                    }

                    int bracketCount = 0;
                    for (char y : print.toCharArray()) if (y == '}') bracketCount ++;
                    if (bracketCount > 1) print = print.substring(0,print.length() - 1);

                    TextBuilder.start(print, pos, Color.WHITE).defaultFont().scale(scale).draw(graphics);
                    prevI = Math.clamp(i,0,str.length());
                    pos.add(new Vector(-10, 10));
                }
            }
            TextBuilder.start("}", pos, Color.WHITE).defaultFont().scale(scale).draw(graphics);

/*
        {
            components:{
                "minecraft:custom_data":{
                    Damage:0,Enchantments:[{
                        id:"minecraft:mending",lvl:1s
                    },{
                        id:"minecraft:protection",lvl:4s
                    },{
                        id:"minecraft:thorns",lvl:3s
                    },{
                        id:"minecraft:unbreaking",lvl:3s
                    },{
                        id:"minecraft:vanishing_curse",lvl:1s
                    }],RepairCost:11,"VV|Protocol1_20_3To1_20_5":1b,display:{
                        Name:'{
                            "text":"Kino\'s Stacked Chestplate"
                        }'
                    }
                },"minecraft:custom_name":'"Kino\'s Stacked Chestplate"',"minecraft:enchantments":{
                    levels:{
                        "minecraft:mending":1,"minecraft:protection":4,"minecraft:thorns":3,"minecraft:unbreaking":3,"minecraft:vanishing_curse":1
                    }
                },"minecraft:repair_cost":11
            },count:1,id:"minecraft:netherite_chestplate"
        }

             */



            //* Try and get it into this format. But im stupid so i guess not
        /*
        {
            components:{
                "minecraft:damage":51, "minecraft:enchantments":{
                    levels:{
                        "minecraft:lure":3, "minecraft:unbreaking":3
                    }
                }
            },count:1, id:"minecraft:fishing_rod"
        } */



            /*
            String[] layers = str.split("\\{");
            for (int i = 0; i < layers.length; i++) {
                String s = layers[i];
                if (!s.isEmpty()) s = "{" + s;
                TextBuilder.start(s,pos,Color.WHITE).defaultFont().scale(.5f).draw(graphics);
                pos.add(new Vector(0,10));
            }

             */





        }
    }
}