package com.phoenixclient.util;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.Container;

import static com.phoenixclient.PhoenixClient.MC;

/**
 * Allows for serverside changing of angles without sending extra packets. It just modifies outgoing ones
 */
public class RotationManager {

    private final OnChange<Vector> onChange = new OnChange<>();

    private boolean spoofing;

    private float spoofedYaw;
    private float spoofedPitch;

    public RotationManager() {
        this.spoofedYaw = 0;
        this.spoofedPitch = 0;
        this.spoofing = false;
    }

    public final EventAction updateSpoofedAngles = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        Container<Boolean> rotChange = new Container<>(false);
        onChange.run(new Vector(spoofedYaw,spoofedPitch),() -> rotChange.set(true));
        if (rotChange.get() && spoofing) forceSendRotationPacket();
    });


    boolean forceSendRotationToggle = false;
    private void forceSendRotationPacket() {
        int i = forceSendRotationToggle ? 1 : -1;
        //Modifies the pitch an amount the player cannot notice to force a rotation change packet. It works I guess ¯\_(ツ)_/¯
        MC.player.setXRot(MC.player.getXRot() + .0004f * i);
        forceSendRotationToggle = !forceSendRotationToggle;
    }

    public void spoof(float yaw, float pitch) {
        if (!spoofing) forceSendRotationPacket();
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
        forceSendRotationPacket();
        spoofing = false;
    }

}
