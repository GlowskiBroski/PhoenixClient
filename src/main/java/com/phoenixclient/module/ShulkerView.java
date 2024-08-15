package com.phoenixclient.module;

import com.mojang.blaze3d.systems.RenderSystem;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.RenderItemTooltipEvent;
import com.phoenixclient.event.events.RenderScreenEvent;
import com.phoenixclient.gui.hud.element.InventoryWindow;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.phoenixclient.PhoenixClient.MC;

public class ShulkerView extends Module {

    private final SettingGUI<Boolean> chestView = new SettingGUI<>(
            this,
            "Chest Expand",
            "Expands all shulkers in a chest around the GUI",
            true);

    private final SettingGUI<Boolean> tooltipView = new SettingGUI<>(
            this,
            "Tooltips",
            "Draws the shulker inventory as a tooltip",
            true);

    private final SettingGUI<Double> tooltipScale = new SettingGUI<>(
            this,
            "Tooltip Scale",
            "Scale of the tooltip",
            1d).setSliderData(.25, 1, .25).setDependency(tooltipView, true);

    private final DoOnce setHoldOnce = new DoOnce();
    private boolean shouldHold = false;
    private Vector holdPos = Vector.NULL();
    private ItemStack holdStack = null;

    public ShulkerView() {
        super("ShulkerView", "Renders shulker box inventories on tooltips or extends chests", Category.RENDER, false, -1);
        addSettings(chestView, tooltipView, tooltipScale);
        addEventSubscriber(Event.EVENT_RENDER_INVENTORY_ITEM_TOOLTIP,this::onRenderTooltip);
        addEventSubscriber(Event.EVENT_RENDER_SCREEN,this::onRenderScreen);
    }

    public void onRenderTooltip(RenderItemTooltipEvent event) {
        ItemStack hoveredItem = event.getItemStack();

        if (tooltipView.get()) {
            if (shouldHold && holdStack != null) event.setCancelled(true);
            if (hoveredItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock) {
                event.setCancelled(true);
                if (shouldHold) {
                    setHoldOnce.run(() -> {
                        holdPos = event.getMousePos().getAdded(8, -16);
                        holdStack = event.getItemStack();
                    });
                } else {
                    RenderSystem.disableDepthTest();
                    renderShulkerTooltip(hoveredItem, event.getMousePos().getAdded(8, -16), tooltipScale.get().floatValue(), 1);
                    setHoldOnce.reset();
                }
            }
        }
    }

    public void onRenderScreen(RenderScreenEvent event) {
        updateHeldTooltip();
        event.getGraphics().flush();
        updateChestView();
    }

    private void updateHeldTooltip() {
        if (tooltipView.get() && shouldHold) {
            RenderSystem.disableDepthTest();
            renderShulkerTooltip(holdStack, holdPos.clone(), tooltipScale.get().floatValue(), 1);
        } else {
            holdStack = null;
        }
        shouldHold = (Key.KEY_LCONTROL.isKeyDown() || Key.KEY_RCONTROL.isKeyDown());
    }

    private void updateChestView() {
        if (chestView.get() && (MC.screen instanceof ContainerScreen || MC.screen instanceof InventoryScreen)) {
            GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());

            float scale = (float) (1/MC.getWindow().getGuiScale());
            graphics.pose().scale(scale, scale, 1);

            Vector pos = new Vector(2, 2);
            String orient = "Vertical";
            int layer = 0;

            List<ItemStack> containerItems = MC.player.containerMenu.getItems();

            for (int i = 0; i < containerItems.size(); i++) {
                ItemStack stack = containerItems.get(i);
                if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock) {

                    renderShulkerTooltip(stack, pos.getMultiplied(scale), scale, .5f);
                    TextBuilder.start("(" + ((i / 9) + 1) + "," + (i - 9 * (i / 9) + 1) + ")", pos.getAdded(148, 5), Color.WHITE).defaultFont().draw(graphics);

                    switch (orient) {
                        case "Vertical" -> {
                            pos.add(new Vector(0, 81));
                            if ((pos.getY() + 80) * scale > MC.getWindow().getGuiScaledHeight()) {
                                pos.setY(2 + 81 * layer).add(new Vector(176, 0));
                                orient = "Horizontal";
                                layer++;
                            }
                        }

                        case "Horizontal" -> {
                            pos.add(new Vector(178, 0));
                            if ((pos.getX() + 176) * scale > MC.getWindow().getGuiScaledWidth()) {
                                pos.setX(2 + 177 * layer).add(new Vector(0, 81));
                                orient = "Vertical";
                            }
                        }
                    }
                }
            }
        }
    }

    private void renderShulkerTooltip(ItemStack hoveredItem, Vector pos, float scale, float transparency) {
        if (hoveredItem == null) return;
        GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());
        graphics.pose().translate(0f, 0f, 232f);
        graphics.pose().scale(scale, scale, 1);

        Color color = getShulkerFrameColor(hoveredItem);
        graphics.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, transparency);

        InventoryWindow.renderChestInventory(graphics, hoveredItem.getHoverName().getString(), pos.getMultiplied(1 / scale));

        int mouseX = (int) (MC.mouseHandler.xpos() * (double) MC.getWindow().getGuiScaledWidth() / (double) MC.getWindow().getScreenWidth());
        int mouseY = (int) (MC.mouseHandler.ypos() * (double) MC.getWindow().getGuiScaledHeight() / (double) MC.getWindow().getScreenHeight());
        Vector mousePos = new Vector(mouseX, mouseY);

        InventoryWindow.renderInventoryItems(graphics, getShulkerItemInventory(hoveredItem), 0, 50, true, pos.getMultiplied(1 / scale).getAdded(0, 126), mousePos.getMultiplied(1 / scale));
    }

    private Color getShulkerFrameColor(ItemStack stack) {
        if (stack == null) return Color.WHITE;
        return switch (stack.getItem().toString().replace("minecraft:","")) {
            case "shulker_box" -> new Color(218, 160, 255);
            case "white_shulker_box" -> new Color(255, 255, 255);
            case "light_gray_shulker_box" -> new Color(150, 150, 150);
            case "gray_shulker_box" -> new Color(100, 100, 100);
            case "black_shulker_box" -> new Color(100, 100, 100);
            case "brown_shulker_box" -> new Color(255, 168, 130);
            case "red_shulker_box" -> new Color(255, 118, 118);
            case "orange_shulker_box" -> new Color(255, 166, 36);
            case "yellow_shulker_box" -> new Color(255, 235, 75);
            case "lime_shulker_box" -> new Color(159, 255, 142);
            case "green_shulker_box" -> new Color(81, 152, 83);
            case "cyan_shulker_box" -> new Color(103, 209, 255);
            case "light_blue_shulker_box" -> new Color(162, 190, 255);
            case "blue_shulker_box" -> new Color(94, 120, 255);
            case "purple_shulker_box" -> new Color(148, 85, 199);
            case "magenta_shulker_box" -> new Color(178, 65, 172);
            case "pink_shulker_box" -> new Color(255, 153, 232);
            default -> Color.WHITE;
        };
    }

    public static List<ItemStack> getShulkerItemInventory(ItemStack stack) {
        ArrayList<ItemStack> list = new ArrayList<>();
        stack.getComponents().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).stream().forEach(list::add);
        while (list.size() < 27) list.add(Blocks.AIR.asItem().getDefaultInstance());
        return list;
    }
}