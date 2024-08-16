package com.phoenixclient.event.events;

import com.phoenixclient.event.Event;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PacketEvent extends CancellableEvent {

    private Packet<?> packet;
    private Type type;

    @Override
    public void post(Object... args) {
        this.packet = (Packet<?>) args[0];
        this.type = (Type) args[1];
        super.post(args);
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SEND,
        RECEIVE
    }

}
