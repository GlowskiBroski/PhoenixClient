package com.phoenixclient.mixin.mixins;

import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity  {

    @Unique
    private boolean prevFlying = false;

    @Inject(method = "isFallFlying", at = @At("TAIL"), cancellable = true)
    public void tryReFlyOnLand(CallbackInfoReturnable<Boolean> cir) {
        boolean flying = cir.getReturnValue();
        boolean stoppedFlying = prevFlying && !flying;
        if (MixinHooks.keepElytraOnGround && stoppedFlying) {
            MC.player.startFallFlying();
            MC.getConnection().send(new ServerboundPlayerCommandPacket(MC.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            cir.setReturnValue(MixinHooks.keepElytraOnGround);
        }
        prevFlying = flying;
    }

}
