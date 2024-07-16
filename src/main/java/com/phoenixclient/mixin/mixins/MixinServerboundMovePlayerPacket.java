package com.phoenixclient.mixin.mixins;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.RotationManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ServerboundMovePlayerPacket.class)
public class MixinServerboundMovePlayerPacket {

    @Mutable @Shadow @Final protected float yRot;

    @Mutable @Shadow @Final protected float xRot;

    /**
     * Enable rotation spoofing to all player packets
     */
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void constructor(double d, double e, double f, float g, float h, boolean bl, boolean bl2, boolean bl3, CallbackInfo ci) {
        RotationManager r = PhoenixClient.getRotationManager();
        if (r.isSpoofing()) {
            yRot = r.getSpoofedYaw();
            xRot = r.getSpoofedPitch();
        }
    }

}
