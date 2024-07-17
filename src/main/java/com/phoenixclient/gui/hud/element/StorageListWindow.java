package com.phoenixclient.gui.hud.element;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.chunk.LevelChunk;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
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

    //TODO: This is stupidly laggy. Find a better way to get block entities

    @Override
    protected LinkedHashMap<String , ListInfo> getListMap() {
        LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();
        LinkedHashMap<String, ListInfo> nextList = new LinkedHashMap<>();
        for (BlockEntity blockEntity : getBlockEntities()) {

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

    private static ArrayList<BlockEntity> getBlockEntities() {
        ArrayList<BlockEntity> list = new ArrayList<>();
        for (LevelChunk chunk : getLoadedChunks()) list.addAll(chunk.getBlockEntities().values());
        return list;
    }

    //TODO: Rework this using a mixin because its slow
    private static ArrayList<LevelChunk> getLoadedChunks() {
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