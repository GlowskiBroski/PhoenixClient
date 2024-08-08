package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoLog extends Module {

    //TODO: Add "below totem count"

    private final SettingGUI<Boolean> minecartTNT = new SettingGUI<>(
            this,
            "TNT Minecarts",
            "Log off if a TNT minecart is in range",
            false);

    private final SettingGUI<Boolean> health = new SettingGUI<>(
            this,
            "Health",
            "Logs out at a specific health",
            true);

    private final SettingGUI<Integer> healthVal = new SettingGUI<>(
            this,
            "HealthVal",
            "Health value to log out with",
            4).setSliderData(1,20,1).setDependency(health,true);

    public AutoLog() {
        super("AutoLog", "Automatically log off from a select event occuring", Category.SERVER, false, -1);
        addSettings(minecartTNT,health,healthVal);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    @Override
    public String getModTag() {
        String tag = "";
        if (health.get()) tag = tag.concat("H: " + healthVal.get());
        if (health.get() && minecartTNT.get()) tag = tag.concat(", ");
        if (minecartTNT.get()) tag = tag.concat("TNT");
        return tag;
    }

    public void onPlayerUpdate(Event event) {
        if (minecartTNT.get()) {
            for (Entity e : MC.level.entitiesForRendering()) {
                if (e instanceof MinecartTNT) logOut("TNT Minecart In Range");
            }
        }
        if (health.get()) {
            if (MC.player.getHealth() <= healthVal.get()) logOut("Health below: " + healthVal.get());
        }
    }

    private void logOut(String reason) {
        MC.getConnection().handleDisconnect(new ClientboundDisconnectPacket(Component.translatable(reason)));
        disable();
        PhoenixClient.getModule("AutoWalk").disable();
    }

}
