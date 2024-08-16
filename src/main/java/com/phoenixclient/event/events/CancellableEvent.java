package com.phoenixclient.event.events;

import com.phoenixclient.event.Event;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class CancellableEvent extends Event {

    private boolean cancelled = false;

    public void updateCancelled(CallbackInfo ci) {
        if (isCancelled()) {
            setCancelled(false);
            ci.cancel();
        }
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }


}
