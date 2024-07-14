package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.GameType;

import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

//Some kid was posting this in chat, its probably a troll but i decided to write it down: 598,120 68 1,471,050

public class PacketTest extends Module {

    public PacketTest() {
        super("PacketTest", "Development Packet Mod - DO NOT ENABLE THIS", Category.SERVER, false, -1);
        addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onUpdate);
    }

    Packet<?> exceptionPacket = null;

    Entity backup = null;

    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        //if (packet instanceof ServerboundMovePlayerPacket) event.setCancelled(true);
        //if (packet instanceof ServerboundMoveVehiclePacket) event.setCancelled(true);
        if (event.getType().equals(PacketEvent.Type.SEND)) System.out.println(packet);

    }


    public void onUpdate(Event event) {
        if (backup != null) {
            backup.setPos(MC.player.getPosition(0));
            MC.getConnection().send(new ServerboundMoveVehiclePacket(backup));
        }
    }

    @Override
    public void onEnabled() {
        if (MC.player == null) {
            disable();
            for (EventAction action : getEventActions()) action.unsubscribe();
            return;
        }
        backup = MC.player.getVehicle();
        if (backup != null) {
            MC.player.removeVehicle();
            backup.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void onDisabled() {
        if (backup != null) {
            if (!MC.options.keyShift.isDown()) {
                MC.level.addEntity(backup);
                MC.player.startRiding(backup, true);
            }
        }
        backup = null;
    }
}
