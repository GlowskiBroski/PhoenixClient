package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.awt.*;
import java.util.LinkedHashMap;

import static com.phoenixclient.PhoenixClient.MC;

public class SignTextListWindow extends ListWindow {

    private final SettingGUI<Integer> range;
    private final SettingGUI<Boolean> coordinates;

    public SignTextListWindow(Screen screen) {
        super(screen, "SignTextListWindow", "Lists all nearby sign text, and their count.",false);
        this.coordinates = new SettingGUI<>(this, "Show Coordinates", "Shows the coordinates for each sign", false);
        this.range = new SettingGUI<>(this,"Range","Block range away from player of entities",320).setSliderData(5,1000,5);
        addSettings(coordinates,range);
    }

    @Override
    protected String getLabel() {
        return "Sign List";
    }

    @Override
    protected LinkedHashMap<String , ListInfo> getListMap() {
        LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();
        for (BlockEntity blockEntity : StorageListWindow.getBlockEntities() ) {
            if (distanceTo(MC.player,blockEntity) > range.get()) continue;

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

    private static float distanceTo(Entity entity, BlockEntity be) {
        float x = (float)(entity.getX() - be.getBlockPos().getX());
        float y = (float)(entity.getY() - be.getBlockPos().getY());
        float z = (float)(entity.getZ() - be.getBlockPos().getZ());
        return Mth.sqrt(x * x + y * y + z * z);
    }
}