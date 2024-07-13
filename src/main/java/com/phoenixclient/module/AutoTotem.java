package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoTotem extends Module {

    private int delay;

    public AutoTotem() {
        super("AutoTotem", "Automatically forces a totem in offhand", Category.COMBAT, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (delay > 0) {
            delay--;
            return;
        }
        NonNullList<ItemStack> inv = MC.player.inventoryMenu.getItems();
        int inventoryIndex;
        if ((MC.player.getItemBySlot(EquipmentSlot.OFFHAND).getItem() != Items.TOTEM_OF_UNDYING)) {
            for (inventoryIndex = 0; inventoryIndex < inv.size(); inventoryIndex++) {
                if (inv.get(inventoryIndex) != ItemStack.EMPTY) {
                    if (inv.get(inventoryIndex).getItem() == Items.TOTEM_OF_UNDYING) {
                        replaceTotem(inventoryIndex);
                        break;
                    }
                }
            }
            delay = 3;
        }
    }

    @Override
    public String getModTag() {
        //return totem count here;
        return "";
    }

    @Override
    public void onEnabled() {
        delay = 0;
    }


    private void replaceTotem(int inventoryIndex) {
        if (MC.player.containerMenu instanceof InventoryMenu) {
            MC.gameMode.handleInventoryMouseClick(0, inventoryIndex < 9 ? inventoryIndex + 36 : inventoryIndex, 0, ClickType.PICKUP, MC.player);
            MC.gameMode.handleInventoryMouseClick(0, 45, 0, ClickType.PICKUP, MC.player);
            MC.gameMode.handleInventoryMouseClick(0, inventoryIndex < 9 ? inventoryIndex + 36 : inventoryIndex, 0, ClickType.PICKUP, MC.player);
        }
    }
}
