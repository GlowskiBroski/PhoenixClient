package com.phoenixclient.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.event.events.RenderNameTagEvent;
import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "renderNameTag", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderNametag(S entityRenderState, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        RenderNameTagEvent event = Event.EVENT_RENDER_NAMETAG;
        event.post(null,component); //TODO: FIX THIS
        event.updateCancelled(ci);
    }

}
