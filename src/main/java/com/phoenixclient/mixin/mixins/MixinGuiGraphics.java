package com.phoenixclient.mixin.mixins;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderItemTooltipEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {

    @Shadow public abstract void renderTooltip(Font font, List<Component> list, Optional<TooltipComponent> optional, int i, int j);

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "HEAD"), cancellable = true)
    public void renderTooltipInject(Font font, ItemStack itemStack, int mouseX, int mouseY, CallbackInfo ci) {
        RenderItemTooltipEvent event = Event.EVENT_DRAW_ITEM_TOOLTIP;
        List<Component> list = Screen.getTooltipFromItem(MC, itemStack);
        event.post(GuiGraphics.class.cast(this),itemStack,list,mouseX,mouseY);
        event.updateCancelled(ci);
        renderTooltip(font, list, itemStack.getTooltipImage(), mouseX, mouseY);
        ci.cancel();
    }


}
