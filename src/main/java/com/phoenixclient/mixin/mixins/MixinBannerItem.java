package com.phoenixclient.mixin.mixins;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BannerItem.class)
public abstract class MixinBannerItem {

    /**
     * @author Me
     * @reason See all the banner patterns on the tooltip
     */
    @Overwrite
    public static void appendHoverTextFromBannerBlockEntityTag(ItemStack itemStack, List<Component> list) {
        BannerPatternLayers bannerPatternLayers = itemStack.get(DataComponents.BANNER_PATTERNS);
        if (bannerPatternLayers == null) {
            return;
        }
        for (int i = 0; i < bannerPatternLayers.layers().size(); ++i) {
            BannerPatternLayers.Layer layer = bannerPatternLayers.layers().get(i);
            list.add(layer.description().withStyle(ChatFormatting.GRAY));
        }
        list.add(Component.translatable("Pattern Count: " + bannerPatternLayers.layers().size()));
    }
}
