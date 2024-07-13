package com.phoenixclient.mixin.mixins;

import com.phoenixclient.event.Event;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Post Packet Event
 */
@Mixin(KeyboardHandler.class)
public abstract class MixinKeyboardHandler {

    @Inject(method = "keyPress", at = @At(value = "RETURN"), cancellable = true)
    private void keyPress(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (l == Minecraft.getInstance().getWindow().getWindow()) Event.EVENT_KEY_PRESS.post(i,j,k,m);
    }

}
