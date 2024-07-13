package com.phoenixclient.mixin.mixins;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

    @Inject(method = "isSpectator", at = @At(value = "HEAD"), cancellable = true)
    private void isSpectator(CallbackInfoReturnable<Boolean> cir) {
        if (getClass().cast(this).equals(MC.player)) {
            cir.setReturnValue(MC.gameMode.getPlayerMode() == GameType.SPECTATOR);
        }
    }
}
