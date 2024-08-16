package com.phoenixclient.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.event.events.RenderNameTagEvent;
import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Inject(method = "renderNameTag", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderNametag(Entity entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f, CallbackInfo ci) {
        RenderNameTagEvent event = Event.EVENT_RENDER_NAMETAG;
        event.post(entity,component);
        event.updateCancelled(ci);
    }

}
