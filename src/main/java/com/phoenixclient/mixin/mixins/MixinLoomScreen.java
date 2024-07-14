package com.phoenixclient.mixin.mixins;

import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoomScreen.class)
public abstract class MixinLoomScreen {

    @Shadow private boolean hasMaxPatterns;

    @Inject(method = "containerChanged", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/LoomScreen;hasMaxPatterns:Z"), cancellable = true)
    protected void maxPatterns(CallbackInfo ci) {
        if (MixinHooks.allowOverloadedBanners) hasMaxPatterns = false;
    }

}
