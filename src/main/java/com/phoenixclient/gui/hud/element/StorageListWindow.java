package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.chunk.LevelChunk;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.phoenixclient.PhoenixClient.MC;

public class StorageListWindow extends ListWindow {

    private final SettingGUI<Integer> range;

    public StorageListWindow(Screen screen) {
        super(screen, "StorageListWindow", "Lists all nearby storage blocks, and their counts",false);
        this.range = new SettingGUI<>(this,"Range","Block range away from player of entities",320).setSliderData(5,1000,5);
        addSettings(range);
    }

    @Override
    protected String getLabel() {
        return "Storage List";
    }

    //TODO: This is stupidly laggy. Find a better way to get block entities

    @Override
    protected LinkedHashMap<String , ListInfo> getListMap() {

        LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();
        for (BlockEntity blockEntity : getBlockEntities()) {
            if (distanceTo(MC.player,blockEntity) > range.get()) continue;

            String rawName = blockEntity.getBlockState().getBlock().toString();
            String entityName = rawName.replace("minecraft:","").replace("Block","").replace("{","").replace("}","");//rawName.replace("minecraft:","").replace("_"," ");
            entityName = entityName.replaceFirst(entityName.charAt(0) + "",(entityName.charAt(0) + "").toUpperCase()).replace("_"," ");

            boolean isStorage = entityName.equalsIgnoreCase("chest")
                    || entityName.toLowerCase().contains("shulker box")
                    || entityName.equalsIgnoreCase("trapped chest")
                    || entityName.equalsIgnoreCase("hopper")
                    || entityName.equalsIgnoreCase("dropper")
                    || entityName.equalsIgnoreCase("dispenser")
                    || entityName.equalsIgnoreCase("furnace")
                    || entityName.equalsIgnoreCase("barrel");

            if (!isStorage) continue;


            Color entityColor = Color.WHITE;
            if (entityName.toLowerCase().contains("shulker box")) {
                entityName = "Shulker box";
                entityColor = new Color(179, 86, 255);
            }

            if (entityName.equalsIgnoreCase("hopper") || entityName.equalsIgnoreCase("furnace") || entityName.equalsIgnoreCase("dropper") || entityName.equalsIgnoreCase("dispenser"))
                entityColor = new Color(176, 176, 176);

            if (currentList.containsKey(entityName)) {
                ListInfo count = new ListInfo("(" + (Integer.parseInt(currentList.get(entityName).tag().replace("(","").replace(")","")) + 1) + ")",entityColor,Color.CYAN);
                currentList.put(entityName, count);
                continue;
            }
            currentList.put(entityName,new ListInfo("(1)",entityColor,Color.CYAN));
        }

        return forceAddedToBottom(currentList);
    }

    private static float distanceTo(Entity entity, BlockEntity be) {
        float x = (float)(entity.getX() - be.getBlockPos().getX());
        float y = (float)(entity.getY() - be.getBlockPos().getY());
        float z = (float)(entity.getZ() - be.getBlockPos().getZ());
        return Mth.sqrt(x * x + y * y + z * z);
    }

    public static ArrayList<BlockEntity> getBlockEntities() {
        ArrayList<BlockEntity> list = new ArrayList<>();
        for (LevelChunk chunk : getLoadedChunks()) list.addAll(chunk.getBlockEntities().values());
        return list;
    }

    //TODO: Rework this using a mixin because its slow
    public static ArrayList<LevelChunk> getLoadedChunks() {
        ArrayList<LevelChunk> chunkList = new ArrayList<>();
        int renderDistance = MC.options.renderDistance().get();

        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                LevelChunk chunk = MC.level.getChunkSource().getChunk((int) MC.player.getX() / 16 + x, (int) MC.player.getZ() / 16 + z,true);
                if (chunk != null) chunkList.add(chunk);
            }
        }
        return chunkList;
    }
}