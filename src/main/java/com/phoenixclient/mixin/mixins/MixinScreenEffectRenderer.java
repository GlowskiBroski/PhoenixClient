package com.phoenixclient.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenEffectRenderer.class)
public abstract class MixinScreenEffectRenderer {

    @Inject(method = "getViewBlockingState", at = @At(value = "HEAD"), cancellable = true)
    private static void onBlockView(Player player, CallbackInfoReturnable<BlockState> cir) {
        if (MixinHooks.noSuffocationHud) cir.setReturnValue(null);
    }

    @Inject(method = "renderFire", at = @At(value = "HEAD"), cancellable = true)
    private static void onRenderFireTexture(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
        if (MixinHooks.noFireHud) ci.cancel();
    }

}
