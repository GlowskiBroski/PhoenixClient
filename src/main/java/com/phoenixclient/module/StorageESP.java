package com.phoenixclient.module;

import com.mojang.blaze3d.vertex.*;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderLevelEvent;
import com.phoenixclient.gui.hud.element.StorageListWindow;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.Draw3DUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.AABB;

import java.awt.*;

public class StorageESP extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Rendering mode",
            "Box").setModeData("Box");

    private final SettingGUI<Boolean> chests = new SettingGUI<>(
            this,
            "Chests",
            "Renders chest & trapped chest ESP",
            true
    );

    private final SettingGUI<Boolean> barrels = new SettingGUI<>(
            this,
            "Barrels",
            "Renders barrel ESP",
            true
    );

    private final SettingGUI<Boolean> shulkers = new SettingGUI<>(
            this,
            "Shulker Boxes",
            "Renders shulker box ESP",
            true
    );

    private final SettingGUI<Boolean> enderChests = new SettingGUI<>(
            this,
            "Ender Chests",
            "Renders ender chest ESP",
            false
    );

    private final SettingGUI<Boolean> hoppers = new SettingGUI<>(
            this,
            "Hoppers",
            "Renders hopper ESP",
            false
    );

    private final SettingGUI<Boolean> furnaces = new SettingGUI<>(
            this,
            "Furnaces",
            "Renders furnace ESP",
            false
    );

    private final SettingGUI<Boolean> dispensers = new SettingGUI<>(
            this,
            "Dispensers",
            "Renders dispenser & dropper ESP",
            false
    );

    public StorageESP() {
        super("StorageESP", "Draws a highlight around storage blocks", Category.RENDER, false, -1);
        addSettings(mode, chests, shulkers, enderChests, hoppers, furnaces, dispensers, barrels);
        addEventSubscriber(Event.EVENT_RENDER_LEVEL, this::onRender);
    }

    public void onRender(RenderLevelEvent event) {
        PoseStack levelStack = event.getLevelPositionStack();

        for (BlockEntity e : StorageListWindow.getBlockEntities()) {
            AABB bb = AABB.ofSize(new Vector(e.getBlockPos()).getVec3(), 1, 1, 1);
            AABB chestBB = AABB.ofSize(new Vector(e.getBlockPos()).getVec3(), .875, .875, .875);

            boolean doChests = chests.get() && e instanceof ChestBlockEntity;
            boolean doBarrels = barrels.get() && e instanceof BarrelBlockEntity;
            boolean doShulkers = shulkers.get() && e instanceof ShulkerBoxBlockEntity;
            boolean doHoppers = hoppers.get() && e instanceof HopperBlockEntity;
            boolean doEnderChests = enderChests.get() && e instanceof EnderChestBlockEntity;
            boolean doFurnaces = furnaces.get() && e instanceof FurnaceBlockEntity;
            boolean doDispensers = dispensers.get() && e instanceof DispenserBlockEntity;

            Vector pos = new Vector(e.getBlockPos()).getAdded(.5, 0, .5);
            if (doChests || doBarrels) Draw3DUtil.drawOutlineBox(levelStack, chestBB, pos, new Color(171, 99, 0, 255));
            if (doEnderChests) Draw3DUtil.drawOutlineBox(levelStack, chestBB, pos, new Color(156, 89, 211, 255));
            if (doShulkers) Draw3DUtil.drawOutlineBox(levelStack, bb, pos, new Color(236, 0, 255, 255));
            if (doHoppers || doFurnaces || doDispensers) Draw3DUtil.drawOutlineBox(levelStack, bb, pos, new Color(105, 105, 105, 255));

        }
    }
}
