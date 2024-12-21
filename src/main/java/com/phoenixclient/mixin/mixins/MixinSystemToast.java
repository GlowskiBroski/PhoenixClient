package com.phoenixclient.mixin.mixins;

import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SystemToast.class)
public abstract class MixinSystemToast {

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, Font font, long l, CallbackInfo ci) {
        if (MixinHooks.noTipsWindow) ci.cancel();//cir.setReturnValue(Toast.Visibility.HIDE);
    }
}
