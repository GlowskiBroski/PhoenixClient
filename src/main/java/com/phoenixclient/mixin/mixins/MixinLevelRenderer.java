package com.phoenixclient.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.event.Event;
import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {

    //Modifies the renderer to think you're in spectator
    @ModifyVariable(method = "setupRender", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private boolean changeSpectatorRenderer(boolean bl2) {
        if (MixinHooks.noCaveCulling) return true;
        return bl2;
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/Camera;)V"), cancellable = true)
    private void renderLevel(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci, @Local(ordinal = 0) PoseStack poseStack, @Local(ordinal = 0) MultiBufferSource.BufferSource bufferSource, @Local(ordinal = 0) float f) {
       Event.EVENT_RENDER_DEBUG.post(poseStack,bufferSource,camera,f);
    }
}
