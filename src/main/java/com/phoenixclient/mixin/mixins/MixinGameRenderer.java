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

    @Shadow @Final private Camera mainCamera;

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
        //Vec3 camPos = mainCamera.getPosition();
        //poseStack.translate(-camPos.x,-camPos.y,-camPos.z);
        Event.EVENT_RENDER_LEVEL.post(poseStack, tickDelta);
    }

    //TODO: This is MeteorClient's render event and mode. Look into it to try and fix your sad excuse for render mods
    /*
    @Inject(method = "renderLevel", at = @At(value = "FIELD",target = "Lnet/minecraft/client/renderer/GameRenderer;renderHand:Z"), cancellable = true)
    private void onRenderWorld(DeltaTracker deltaTracker, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2, @Local(ordinal = 1) float tickDelta, @Local PoseStack matrixStack) {
        if (!Utils.canUpdate()) return;

        client.getProfiler().push(MeteorClient.MOD_ID + "_render");

        // Create renderer and event

        if (renderer == null) renderer = new Renderer3D();
        Render3DEvent event = Render3DEvent.get(matrixStack, renderer, tickDelta, camera.getPos().x, camera.getPos().y, camera.getPos().z);

        // Call utility classes

        RenderUtils.updateScreenCenter();
        NametagUtils.onRender(matrix4f2);

        // Update model view matrix

        RenderSystem.getModelViewStack().pushMatrix().mul(matrix4f2);

        matrices.push();

        tiltViewWhenHurt(matrices, camera.getLastTickDelta());
        if (client.options.getBobView().getValue()) bobView(matrices, camera.getLastTickDelta());

        RenderSystem.getModelViewStack().mul(matrices.peek().getPositionMatrix().invert());
        matrices.pop();

        RenderSystem.applyModelViewMatrix();

        // Render

        renderer.begin();
        MeteorClient.EVENT_BUS.post(event);
        renderer.render(matrixStack);

        // Revert model view matrix

        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.applyModelViewMatrix();

        client.getProfiler().pop();
    }

     */


    @Inject(method = "bobHurt", at = @At(value = "HEAD"), cancellable = true)
    private void onHurt(PoseStack poseStack, float f, CallbackInfo ci) {
        if (MixinHooks.noHurtCam) ci.cancel();
    }

    @Inject(method = "bobView", at = @At(value = "HEAD"), cancellable = true)
    private void onBob(PoseStack poseStack, float f, CallbackInfo ci) {
        if (MixinHooks.noCameraBob) ci.cancel();
    }
}
