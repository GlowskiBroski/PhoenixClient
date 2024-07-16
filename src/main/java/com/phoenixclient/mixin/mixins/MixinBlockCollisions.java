package com.phoenixclient.mixin.mixins;

import com.google.common.collect.AbstractIterator;
import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(BlockCollisions.class)
public abstract class MixinBlockCollisions<T> extends AbstractIterator<T>  {

    @Shadow @Final private CollisionContext context;

    @Inject(method = "computeNext", at = @At(value = "HEAD"), cancellable = true)
    private void computeNext(CallbackInfoReturnable<T> cir) {
        if (context instanceof EntityCollisionContext e) {
            boolean playerNoClip = MixinHooks.noClip && Objects.equals(e.getEntity(), MC.player);
            boolean mountNoClip = MixinHooks.mountNoClip && Objects.equals(e.getEntity(), MC.player != null ? MC.player.getVehicle() : null);
            if (playerNoClip || mountNoClip) cir.setReturnValue(this.endOfData());
        }
    }

}
