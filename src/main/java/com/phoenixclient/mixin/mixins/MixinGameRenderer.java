package com.phoenixclient.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.event.Event;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "render", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemActivationAnimation(Lnet/minecraft/client/gui/GuiGraphics;F)V"))
    private void onRender(CallbackInfo ci) {
        Event.EVENT_RENDER_HUD.post();
    }

    //TODO: Please add guiGraphics as a parameter for the post
    @Inject(method = "render", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/screens/Screen;handleDelayedNarration()V"), cancellable = true)
    private void onRenderScreen(CallbackInfo ci) {
        Event.EVENT_RENDER_SCREEN.post();
    }

    @Inject(method = "renderLevel", at = @At(value = "FIELD",target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z"), cancellable = true)
    private void onRenderLevel(DeltaTracker deltaTracker, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2, @Local(ordinal = 1) float tickDelta) {
        PoseStack matrixStack = new PoseStack();
        matrixStack.mulPose(matrix4f2);
        Vec3 camPos = MC.getBlockEntityRenderDispatcher().camera.getPosition();
        matrixStack.translate(-camPos.x,-camPos.y,-camPos.z);
        Event.EVENT_RENDER_LEVEL.post(matrixStack,tickDelta);
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
