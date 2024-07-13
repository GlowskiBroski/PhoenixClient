package com.phoenixclient.mixin.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.util.Brightness;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Consumer;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(OptionInstance.class)
public abstract class MixinOptionInstance<T> {

    @Shadow
    T value;

    @Inject(method = "set", at = @At(value = "HEAD"), cancellable = true)
    public void thing(T object, CallbackInfo ci) {
        if (MC.player != null && OptionInstance.class.cast(this).equals(MC.options.gamma())) {
            if (!Minecraft.getInstance().isRunning()) {
                this.value = object;
            }
            if (!Objects.equals(this.value, object)) {
                this.value = object;
            }
            ci.cancel();
        }
    }

}