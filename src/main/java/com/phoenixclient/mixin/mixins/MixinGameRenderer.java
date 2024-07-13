package com.phoenixclient.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.event.Event;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "render", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;F)V"), cancellable = true)
    private void onRender(CallbackInfo ci) {
        Event.EVENT_RENDER_HUD.post();
    }


    @Inject(method = "render", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/screens/Screen;handleDelayedNarration()V"), cancellable = true)
    private void onRenderScreen(CallbackInfo ci) {
        Event.EVENT_RENDER_SCREEN.post();
    }


    @Inject(method = "bobHurt", at = @At(value = "HEAD"), cancellable = true)
    private void onHurt(PoseStack poseStack, float f, CallbackInfo ci) {
        if (MixinHooks.noHurtCam) ci.cancel();
    }

    @Inject(method = "bobView", at = @At(value = "HEAD"), cancellable = true)
    private void onBob(PoseStack poseStack, float f, CallbackInfo ci) {
        if (MixinHooks.noCameraBob) ci.cancel();
    }
}
