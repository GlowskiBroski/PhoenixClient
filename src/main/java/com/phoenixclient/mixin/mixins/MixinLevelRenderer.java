package com.phoenixclient.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.event.Event;
import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {

    //Modifies the renderer to think you're in spectator
    @ModifyVariable(method = "setupRender", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private boolean changeSpectatorRenderer(boolean bl2) {
        if (MixinHooks.noCaveCulling) return true;
        return bl2;
    }
}
