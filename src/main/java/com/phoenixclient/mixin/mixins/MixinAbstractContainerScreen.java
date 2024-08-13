package com.phoenixclient.mixin.mixins;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderItemTooltipEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen {

    @Shadow @Nullable protected Slot hoveredSlot;

    @Shadow protected abstract List<Component> getTooltipFromContainerItem(ItemStack itemStack);

    @Unique List<Component> containerItemList;

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"), cancellable = true)
    private void onRenderItemTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        RenderItemTooltipEvent event = Event.EVENT_RENDER_INVENTORY_ITEM_TOOLTIP;
        ItemStack item = hoveredSlot.getItem();
        List<Component> list = getTooltipFromContainerItem(item);
        containerItemList = list;

        event.post(guiGraphics,item,list,mouseX,mouseY);
        event.updateCancelled(ci);
    }


    @Redirect(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void getTooltipGuy(GuiGraphics instance, Font font, List<Component> list, Optional<TooltipComponent> optional, int i, int j) {
        instance.renderTooltip(font, containerItemList, optional, i, j);
    }

}
