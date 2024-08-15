package com.phoenixclient.mixin.mixins;

import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeToast.class)
public abstract class MixinRecipeToast {

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, ToastComponent toastComponent, long l, CallbackInfoReturnable<Toast.Visibility> cir) {
        if (MixinHooks.noTipsWindow) cir.setReturnValue(Toast.Visibility.HIDE);
    }
}
