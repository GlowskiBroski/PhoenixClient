package com.phoenixclient.module;

import com.mojang.blaze3d.vertex.*;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderLevelEvent;
import com.phoenixclient.util.render.Draw3DUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class EntityESP extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Rendering mode",
            "Box").setModeData("Box");

    private final SettingGUI<Boolean> items = new SettingGUI<>(
            this,
            "Items",
            "Renders item ESP",
            true
    );

    private final SettingGUI<Boolean> players = new SettingGUI<>(
            this,
            "Players",
            "Renders player ESP",
            true
    );

    private final SettingGUI<Boolean> passive = new SettingGUI<>(
            this,
            "Passive Mobs",
            "Renders passive mob ESP",
            false
    );

    private final SettingGUI<Boolean> hostile = new SettingGUI<>(
            this,
            "Hostile Mobs",
            "Renders hostile mob ESP",
            false
    );

    public EntityESP() {
        super("EntityESP", "Draws a highlight around entites", Category.RENDER, false, -1);
        addSettings(mode,items,players,passive,hostile);
        addEventSubscriber(Event.EVENT_RENDER_LEVEL, this::onRender);
    }

    public void onRender(RenderLevelEvent event) {
        PoseStack levelStack = event.getLevelPoseStack();
        float partialTicks = event.getPartialTicks();

        for (Entity e : MC.level.entitiesForRendering()) {
            boolean doItems = items.get() && e instanceof ItemEntity;
            boolean doPlayers = players.get() && e instanceof Player && !e.equals(MC.player);
            boolean doHostile = hostile.get() && e instanceof Monster;
            boolean doPassive = passive.get() && e instanceof Animal;

            if (doItems) Draw3DUtil.drawOutlineBox(levelStack,e.getBoundingBox(), Draw3DUtil.getLerpPos(e,partialTicks),new Color(0, 255, 255, 255));
            if (doPlayers) Draw3DUtil.drawOutlineBox(levelStack,e.getBoundingBox(), Draw3DUtil.getLerpPos(e,partialTicks),new Color(0, 72, 255, 255));
            if (doHostile) Draw3DUtil.drawOutlineBox(levelStack,e.getBoundingBox(), Draw3DUtil.getLerpPos(e,partialTicks),new Color(162, 0, 0, 255));
            if (doPassive) Draw3DUtil.drawOutlineBox(levelStack,e.getBoundingBox(), Draw3DUtil.getLerpPos(e,partialTicks),new Color(63, 166, 0, 255));
        }
    }

}
