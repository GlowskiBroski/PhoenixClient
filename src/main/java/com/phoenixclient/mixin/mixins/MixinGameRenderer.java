package com.phoenixclient.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.event.Event;
import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemActivationAnimation(Lnet/minecraft/client/gui/GuiGraphics;F)V"))
    private void onRender(CallbackInfo ci, @Local(ordinal = 0) GuiGraphics guiGraphics, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j) {
        Event.EVENT_RENDER_HUD.post(guiGraphics,i,j);
    }

    //TODO: Please add guiGraphics as a parameter for the post
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;handleDelayedNarration()V"), cancellable = true)
    private void onRenderScreen(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci, @Local(ordinal = 0) GuiGraphics guiGraphics, @Local(ordinal = 0) int i, @Local(ordinal = 1) int j) {
        Event.EVENT_RENDER_SCREEN.post(guiGraphics, i, j);
    }


    @Inject(method = "renderLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z"), cancellable = true)
    private void onRenderLevel(DeltaTracker deltaTracker, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2, @Local(ordinal = 1) float tickDelta) {
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(matrix4f2); //Matrix4f2 is the POSITION MATRIX
        Event.EVENT_RENDER_LEVEL.post(poseStack, tickDelta);
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
