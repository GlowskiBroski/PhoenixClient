package com.phoenixclient.util;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.setting.Container;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: This does not work...
/**
 * Allows for serverside changing of angles without sending extra packets. It just modifies outgoing ones
 */
public class RotationManager {

    private Packet<?> exceptionPacket = null;

    private final OnChange<Float> onChange = new OnChange<>();

    private boolean spoofing;

    private float spoofedYaw;
    private float spoofedPitch;

    public RotationManager() {
        this.spoofedYaw = 0;
        this.spoofedPitch = 0;
        this.spoofing = false;
    }

    //This will send 1 tick behind normal gameplay. Shouldn't be a noticeable issue. I think... ¯\_(ツ)_/¯
    public final EventAction setSpoofedAngles = new EventAction(Event.EVENT_PACKET, () -> {
        if (!spoofing) return;
        PacketEvent event = Event.EVENT_PACKET;
        Packet<?> packet = event.getPacket();

        if (packet.equals(exceptionPacket)) return;
        float yRot = spoofedYaw;
        float xRot = spoofedPitch;
        Container<Boolean> forceUpdate = new Container<>(false);
        onChange.run(yRot, () -> forceUpdate.set(true));
        onChange.run(xRot, () -> forceUpdate.set(true));

        if (packet instanceof ServerboundMovePlayerPacket.Pos p) {
            //If the spoofed angles are changed, send a PosRot instead of a pos on a position packet send
            if (forceUpdate.get()) {
                MC.getConnection().send(exceptionPacket = new ServerboundMovePlayerPacket.PosRot(p.getX(0), p.getY(0), p.getZ(0), yRot, xRot, p.isOnGround()));
                event.setCancelled(true);
            }
        }
        if (packet instanceof ServerboundMovePlayerPacket.PosRot p) {
            MC.getConnection().send(exceptionPacket = new ServerboundMovePlayerPacket.PosRot(p.getX(0), p.getY(0), p.getZ(0), yRot, xRot, p.isOnGround()));
            event.setCancelled(true);
        }
        if (packet instanceof ServerboundMovePlayerPacket.Rot p) {
            MC.getConnection().send(exceptionPacket = new ServerboundMovePlayerPacket.Rot(yRot, xRot, p.isOnGround()));
            event.setCancelled(true);
        }

        if (forceUpdate.get()) {
            MC.getConnection().send(exceptionPacket = new ServerboundMovePlayerPacket.Rot(yRot, xRot, MC.player.onGround()));
        }
    });

    public void spoof(float yaw, float pitch) {
        spoofedYaw = yaw;
        spoofedPitch = pitch;
        spoofing = true;
    }

    public boolean isSpoofing() {
        return spoofing;
    }

    public float getSpoofedYaw() {
        return spoofedYaw;
    }

    public float getSpoofedPitch() {
        return spoofedPitch;
    }

    public void stopSpoofing() {
        spoofing = false;
    }

}
