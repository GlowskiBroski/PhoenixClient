package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.phoenixclient.PhoenixClient.MC;

public class EntityListWindow extends ListWindow {

    private final SettingGUI<Integer> range;
    private final SettingGUI<Boolean> combineItems;

    public EntityListWindow(Screen screen, Vector pos) {
        super(screen, "EntityListWindow", pos);
        this.range = new SettingGUI<>(this,"Range","Block range away from player of entities",64).setSliderData(1,200,1);
        this.combineItems = new SettingGUI<>(this,"Combine Items","Combines items into 1 category",true);
        addSettings(range,combineItems);
    }

    @Override
    protected String getLabel() {
        return "Entity List";
    }

    //TODO: This is VERY laggy, especially if the range is higher. Figure that out. No its not from the nextListOrdering, i tested it. Its the list itself
    @Override
    protected LinkedHashMap<String , ListInfo> getListMap() {
        LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();
        LinkedHashMap<String, ListInfo> nextList = new LinkedHashMap<>();

        for (Entity entity : MC.level.entitiesForRendering()) {
            if (entity.distanceTo(MC.player) > range.get()) continue;
            if (entity instanceof LocalPlayer) continue;
            String rawName = entity.getType().toShortString();
            String entityName = rawName.replaceFirst(String.valueOf(rawName.charAt(0)), String.valueOf(rawName.charAt(0)).toUpperCase()).replace("_"," ");

            if (!combineItems.get() && entity instanceof ItemEntity e) {
                entityName = "Item: " + e.getName().getString();
            }

            if (currentList.containsKey(entityName)) {
                ListInfo count = new ListInfo("(" + (Integer.parseInt(currentList.get(entityName).tag().replace("(","").replace(")","")) + 1) + ")",Color.WHITE,Color.CYAN);
                currentList.put(entityName, count);
                continue;
            }
            currentList.putIfAbsent(entityName,new ListInfo("(1)",Color.WHITE,Color.CYAN));
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