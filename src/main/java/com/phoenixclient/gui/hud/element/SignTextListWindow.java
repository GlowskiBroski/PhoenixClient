package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.phoenixclient.PhoenixClient.MC;

public class SignTextListWindow extends ListWindow {

    //TODO: Add Range Slider

    private final SettingGUI<Boolean> coordinates;

    public SignTextListWindow(Screen screen, Vector pos) {
        super(screen, "SignTextListWindow", pos);
        this.coordinates = new SettingGUI<>(this, "Show Coordinates", "Shows the coordinates for each sign", false);
        addSettings(coordinates);
    }

    @Override
    protected String getLabel() {
        return "Sign List";
    }

    @Override
    protected LinkedHashMap<String , ListInfo> getListMap() {
        LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();
        for (BlockEntity blockEntity : getBlockEntities()) {

            if (!(blockEntity instanceof SignBlockEntity s)) continue;
            String entityName = "";

            //Add Front Text
            for (int i = 0; i < 4; i++) {
                String lineText = s.getFrontText().getMessage(i,true).getString();
                if (lineText.isEmpty()) continue;
                entityName = entityName.concat(lineText + " ");
            }

            //Add Back Text
            for (int i = 0; i < 4; i++) {
                String lineText = s.getBackText().getMessage(i,true).getString();
                if (lineText.isEmpty()) continue;
                entityName = entityName.concat(lineText + " ");
            }

            if (entityName.isEmpty()) continue;

            if (coordinates.get()) entityName = "(" + s.getBlockPos().getX() + ", " + s.getBlockPos().getY() + ", " + s.getBlockPos().getZ() + "): " + entityName;

            if (currentList.containsKey(entityName)) {
                if (currentList.get(entityName).tag().isEmpty()) currentList.put(entityName, new ListInfo("(1)",Color.WHITE,Color.CYAN));
                ListInfo count = new ListInfo("(" + (Integer.parseInt(currentList.get(entityName).tag().replace("(","").replace(")","")) + 1) + ")",Color.WHITE,Color.CYAN);
                currentList.put(entityName, count);
                continue;
            }
            currentList.put(entityName,new ListInfo("",Color.WHITE,Color.CYAN));
        }

        return forceAddedToBottom(currentList);
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