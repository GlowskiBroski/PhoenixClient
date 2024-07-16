package com.phoenixclient.mixin.mixins;

import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "HEAD"), cancellable = true)
    private void push(Entity entity, CallbackInfo ci) {
        if (MixinHooks.noPush) ci.cancel();
    }
}
