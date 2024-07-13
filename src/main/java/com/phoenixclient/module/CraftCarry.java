package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;

public class CraftCarry extends Module {

    public CraftCarry() {
        super("CraftCarry", "Allows the crafting grid to be inventory space", Category.SERVER, false, -1);
        addEventSubscriber(Event.EVENT_PACKET, this::onPacket);
    }

    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof ServerboundContainerClosePacket) {
            event.setCancelled(true); //TODO: When a movement packet is sent, the items leave the crafting grid
        }
    }

}
