package com.phoenixclient.mixin.mixins;

import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeToast.class)
public abstract class MixinRecipeToast {

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, Font font, long l, CallbackInfo ci) {
        if (MixinHooks.noTipsWindow) ci.cancel();//ci.setReturnValue(Toast.Visibility.HIDE);
    }
}
