package com.phoenixclient.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderItemTooltipEvent;
import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen {

    @Shadow @Nullable protected Slot hoveredSlot;

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"), cancellable = true)
    private void onRenderItemTooltipPost(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        RenderItemTooltipEvent event = Event.EVENT_RENDER_ITEM_TOOLTIP;
        event.post(hoveredSlot.getItem(),i,j);
        Event.EVENT_RENDER_ITEM_TOOLTIP.updateCancelled(ci);
    }


}
