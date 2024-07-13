package com.phoenixclient.event.events;

import com.phoenixclient.event.Event;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PacketEvent extends Event {

    private boolean cancelled = false;

    private Packet<?> packet;
    private Type type;

    @Override
    public void post(Object... args) {
        this.packet = (Packet<?>) args[0];
        this.type = (Type) args[1];
        super.post(args);
    }

    public void updateCancelled(CallbackInfo ci) {
        if (isCancelled()) {
            setCancelled(false);
            ci.cancel();
        }
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SEND,
        RECEIVE
    }

}
