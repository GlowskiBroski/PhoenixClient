package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderScreenEvent;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoRespawn extends Module {

    private int delay;

    private final SettingGUI<Integer> tickDelay = new SettingGUI<>(
            this,
            "Delay",
            "Tick delay of auto respawn",
            1).setSliderData(0,100,1);

    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawns the player", Category.PLAYER, false, -1);
        addSettings(tickDelay);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
        addEventSubscriber(Event.EVENT_RENDER_SCREEN,this::onRender);
    }

    public void onPlayerUpdate(Event event) {
        if (!MC.player.isAlive()) {
            if (delay > 0) delay --;
            if (delay <= 0) MC.player.respawn();
        }
        delay = tickDelay.get();
    }

    public void onRender(RenderScreenEvent event) {

    }

}