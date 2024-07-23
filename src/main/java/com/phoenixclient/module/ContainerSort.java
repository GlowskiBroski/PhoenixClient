package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderScreenEvent;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static com.phoenixclient.PhoenixClient.MC;

public class ContainerSort extends Module {

    private final SettingGUI<String> type = new SettingGUI<>(
            this,
            "Type",
            "Sorting type for containers",
            "Name").setModeData("Name","ID","Category");

    private int hintFade = 0;
    private boolean hintFadeIn = true;

    public ContainerSort() {
        super("ContainerSort", "Allows for non-inventory containers to be sorted by holding the space bar", Category.SERVER, false, -1);
        addSettings(type);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE, this::onPlayerUpdate);
        addEventSubscriber(Event.EVENT_RENDER_SCREEN, this::onRenderScreen);
    }

    public void onPlayerUpdate(Event event) {
        if (!(MC.screen instanceof AbstractContainerScreen<?> containerScreen)) return;
        if (shouldSort() && Key.KEY_SPACE.isKeyDown()) {
            AbstractContainerMenu containerMenu = containerScreen.getMenu();

            //TODO: Add speed limiter here for these 3 methods below
            combineStacks(containerMenu);
            sortStacks(containerMenu);

            //If the player is still holding an item, place it in the next empty slot. This is often the result from combining stacks
            //This will be unsorted after the first loop, holding sort will try to sort again afterwards
            if (!containerMenu.getCarried().isEmpty()) clickWindow(containerMenu,containerMenu.getItems().indexOf(ItemStack.EMPTY));
        }
    }

    public void onRenderScreen(RenderScreenEvent event) {
        if (!(MC.screen instanceof AbstractContainerScreen<?>)) return;
        String hint = "Hold SPACE to sort container!";
        if (hintFadeIn) {
            if (hintFade < 255) hintFade += 3;
            if (hintFade >= 255) hintFadeIn = false;
        } else {
            if (hintFade > 25) hintFade -= 3;
            if (hintFade <= 25) hintFadeIn = true;
        }

        Color color = new Color(255, 255, 255, Math.clamp(hintFade, 0, 255));
        TextBuilder.start().text(hint).pos(new Vector(0, MC.getWindow().getGuiScaledHeight() - 14)).centerX().color(color).draw(event.getGraphics());
    }

    private void sortStacks(AbstractContainerMenu containerMenu) {
        ArrayList<ItemStack> templateList = getSortedItemTemplateList(containerMenu);
        for (int i = 0; i < templateList.size(); i++) {
            ItemStack trueItem = containerMenu.getSlot(i).getItem();
            ItemStack templateItem = templateList.get(i);

            //If the next sorted item equals the true item, go to the next item
            if (ItemStack.isSameItem(trueItem,templateItem)) continue;

            //Swap next item with the template's next sorted item
            int nextTemplateItemSlot = containerMenu.getItems().indexOf(templateItem);
            clickWindow(containerMenu,nextTemplateItemSlot);
            clickWindow(containerMenu,i);
        }
    }

    private void combineStacks(AbstractContainerMenu containerMenu) {
        for (int i = 0; i < getNonInventorySlots(containerMenu); i++) {
            ItemStack stack = containerMenu.getSlot(i).getItem();

            //Go to the next item if the item doesn't exist or the stack size is maxed out
            if (stack.isEmpty() || stack.getCount() >= stack.getMaxStackSize()) continue;

            //Loop through the next items to see if any are stackable. If they are, stack them
            for (int j = i + 1; j < getNonInventorySlots(containerMenu); j++) {
                ItemStack nextItem = containerMenu.getSlot(j).getItem();

                //If the items are stackable, stack them
                if (ItemStack.isSameItemSameComponents(stack,nextItem)) {
                    clickWindow(containerMenu,j);
                    clickWindow(containerMenu,i);
                }
            }
        }
    }

    private void clickWindow(AbstractContainerMenu menu, int index) {
        MC.gameMode.handleInventoryMouseClick(menu.containerId, index, 0, ClickType.PICKUP, MC.player);
    }

    private boolean shouldSort() {
        if (MC.player == null) return false;
        if (!MC.player.containerMenu.getCarried().isEmpty()) return false;
        if (!MC.player.isSpectator()) return true;
        return !(MC.screen instanceof InventoryScreen);
    }

    private int getNonInventorySlots(AbstractContainerMenu containerMenu) {
        int count = 0;
        for (int i = 0; i < containerMenu.slots.size(); i++) {
            boolean isSlotInPlayerInventory = (containerMenu.getSlot(i).container instanceof Inventory);
            if (!isSlotInPlayerInventory) count++;
        }
        return count;
    }

    private ArrayList<ItemStack> getSortedItemTemplateList(AbstractContainerMenu containerMenu) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < getNonInventorySlots(containerMenu); i++) stacks.add(containerMenu.getSlot(i).getItem());
        stacks.sort(new StackComparator());
        return stacks;
    }

    // This comparator compares item stacks by their item name.
    private class StackComparator implements Comparator<ItemStack> {
        @Override
        public int compare(ItemStack stack1, ItemStack stack2) {
            if (stack1.equals(stack2)) return 0;
            if (stack1.isEmpty()) return 1;
            if (stack2.isEmpty()) return -1;

            switch (type.get()) {
                case "ID" -> {
                    String item1Str = Item.getId(stack1.getItem()) + "";
                    String item2Str = Item.getId(stack2.getItem()) + "";
                    return item1Str.compareTo(item2Str);
                }
                case "Category" -> {
                    String item1Str = stack1.getItem().getDescriptionId();
                    String item2Str = stack2.getItem().getDescriptionId();
                    return item1Str.compareTo(item2Str);
                }
                default /*"Name"*/ -> {
                    String item1Str = stack1.getItem().getName(stack1).getString();
                    String item2Str = stack2.getItem().getName(stack2).getString();
                    return item1Str.compareTo(item2Str);
                }
            }
        }
    }

}

