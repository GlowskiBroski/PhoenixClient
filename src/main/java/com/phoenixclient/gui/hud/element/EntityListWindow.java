package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.LinkedHashMap;

import static com.phoenixclient.PhoenixClient.MC;

public class EntityListWindow extends ListWindow {

    private final SettingGUI<Integer> range;
    private final SettingGUI<Boolean> combineItems;

    public EntityListWindow(Screen screen) {
        super(screen, "EntityListWindow","Lists all nearby entities, with their count. This includes items",false);
        this.range = new SettingGUI<>(this,"Range","Block range away from player of entities",70).setSliderData(5,1000,5);
        this.combineItems = new SettingGUI<>(this,"Combine Items","Combines items into 1 category",true);
        addSettings(range,combineItems);
    }

    @Override
    protected String getLabel() {
        return "Entity List";
    }

    @Override
    protected LinkedHashMap<String , ListInfo> getListMap() {
        LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();

        for (Entity entity : MC.level.entitiesForRendering()) {
            if (entity.distanceTo(MC.player) > range.get()) continue;
            if (entity instanceof LocalPlayer) continue;
            String rawName = entity.getType().toShortString();
            String entityName = rawName.replaceFirst(String.valueOf(rawName.charAt(0)), String.valueOf(rawName.charAt(0)).toUpperCase()).replace("_"," ");

            Color entityColor = entity instanceof Player ? new Color(79, 105, 245) : Color.WHITE;
            int mul = 1;
            if (!combineItems.get() && entity instanceof ItemEntity e) {
                entityName = "Item: " + e.getName().getString();
                entityColor = new Color(255, 86, 86);
                if (entityName.toLowerCase().contains("shulker box")) entityColor = new Color(179, 86, 255);
                mul = e.getItem().getCount();
            }


            if (currentList.containsKey(entityName)) {
                ListInfo count = new ListInfo("(" + (Integer.parseInt(currentList.get(entityName).tag().replace("(","").replace(")","")) + mul) + ")",entityColor,Color.CYAN);
                currentList.put(entityName, count);
                continue;
            }
            currentList.putIfAbsent(entityName,new ListInfo("(" + mul + ")",entityColor,Color.CYAN));
        }

        return forceAddedToBottom(currentList);
    }

}