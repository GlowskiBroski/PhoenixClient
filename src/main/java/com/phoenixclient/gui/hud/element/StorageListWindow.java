package com.phoenixclient.gui.hud.element;

import com.phoenixclient.mixin.mixins.accessors.AccessorLevel;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.phoenixclient.PhoenixClient.MC;

public class StorageListWindow extends ListWindow {

    public StorageListWindow(Screen screen, Vector pos) {
        super(screen, "StorageListWindow", pos);
    }

    @Override
    protected String getLabel() {
        return "Storage List";
    }

    @Override
    protected LinkedHashMap<String , ListInfo> getListMap() {
        LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();
        LinkedHashMap<String, ListInfo> nextList = new LinkedHashMap<>();
        for (TickingBlockEntity blockEntity : ((AccessorLevel)MC.level).getBlockEntities()) {

            String rawName = blockEntity.getType();
            String entityName = rawName.replace("minecraft:","").replace("_"," ");

            boolean isStorage = entityName.equals("chest")
                    || entityName.equals("shulker box")
                    || entityName.equals("trapped chest")
                    || entityName.equals("hopper")
                    || entityName.equals("dropper")
                    || entityName.equals("dispenser")
                    || entityName.equals("furnace")
                    || entityName.equals("barrel");

            if (!isStorage) continue;

            if (currentList.containsKey(entityName)) {
                ListInfo count = new ListInfo("(" + (Integer.parseInt(currentList.get(entityName).tag().replace("(","").replace(")","")) + 1) + ")",Color.WHITE,Color.CYAN);
                currentList.put(entityName, count);
                continue;
            }
            currentList.put(entityName,new ListInfo("(1)",Color.WHITE,Color.CYAN));
        }

        //Always add to bottom
        if (previousList != null) nextList = (LinkedHashMap<String, ListInfo>) previousList.clone();

        for (Map.Entry<String, ListInfo> prevSet : currentList.entrySet()) {
            if (nextList.containsKey(prevSet.getKey())) nextList.put(prevSet.getKey(),prevSet.getValue());
            else nextList.put(prevSet.getKey(),prevSet.getValue());
        }

        ArrayList<String> removalQueue = new ArrayList<>();
        for (Map.Entry<String, ListInfo> set : nextList.entrySet())
            if (!currentList.containsKey(set.getKey())) removalQueue.add(set.getKey());

        for (String s : removalQueue) nextList.remove(s);

        return nextList;
    }
}