package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoTotem extends Module {

    private int delay;

    private int totemCount = 0;

    private final SettingGUI<Integer> tickDelay = new SettingGUI<>(
            this,
            "Delay",
            "Tick delay of totem replacement",
            1).setSliderData(0,10,1);

    public AutoTotem() {
        super("AutoTotem", "Automatically forces a totem in offhand", Category.COMBAT, false, -1);
        addSettings(tickDelay);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        int totCount = 0;
        for (ItemStack stack : MC.player.inventoryMenu.getItems())
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) totCount ++;
        totemCount = totCount;

        if ((MC.player.getItemBySlot(EquipmentSlot.OFFHAND).getItem() == Items.TOTEM_OF_UNDYING) || totemCount == 0) {
            delay = 0;
            return;
        }
        delay++;

        if (delay >= tickDelay.get()) {
            NonNullList<ItemStack> inv = MC.player.inventoryMenu.getItems();
            for (int i = 0; i < inv.size(); i++) {
                if (inv.get(i) == ItemStack.EMPTY) continue;
                if (inv.get(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    replaceTotem(i);
                    break;
                }
            }
        }
    }

    @Override
    public String getModTag() {
        return String.valueOf(totemCount);
    }

    @Override
    public void onEnabled() {
        delay = 0;
    }

    private void replaceTotem(int i) {
        if (MC.player.containerMenu instanceof InventoryMenu) {
            MC.gameMode.handleInventoryMouseClick(0, i < 9 ? i + 36 : i, 0, ClickType.PICKUP, MC.player);
            MC.gameMode.handleInventoryMouseClick(0, 45, 0, ClickType.PICKUP, MC.player);
            MC.gameMode.handleInventoryMouseClick(0, i < 9 ? i + 36 : i, 0, ClickType.PICKUP, MC.player);
        }
    }
}
