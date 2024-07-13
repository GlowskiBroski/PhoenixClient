package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.GameType;

import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

//Some kid was posting this in chat, its probably a troll but i decided to write it down: 598,120 68 1,471,050

public class PacketTest extends Module {

    public PacketTest() {
        super("PacketTest", "Development Packet Mod - DO NOT ENABLE THIS", Category.SERVER, false, -1);
        addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
    }

    Packet<?> exceptionPacket = null;

    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        if (true) return;

        if (packet.equals(exceptionPacket)) return;

        if (event.getType().equals(PacketEvent.Type.RECEIVE)) System.out.println(packet);

        if (packet instanceof ClientboundPlayerPositionPacket) {
            event.setCancelled(true);
        }

        if (packet instanceof ClientboundTeleportEntityPacket) {
            Vector pos = new Vector(85131,90,347878);
            MC.getConnection().send(exceptionPacket = new ServerboundMovePlayerPacket.Pos(pos.getX(),pos.getY(),pos.getZ(),false));
            MC.player.setPosRaw(pos.getX(),pos.getY(),pos.getZ());
            if (MC.player.getVehicle() != null) MC.player.getVehicle().setPosRaw(pos.getX(),pos.getY(),pos.getZ());
            event.setCancelled(true);
        }

        /*

        if (packet instanceof ServerboundMovePlayerPacket.PosRot e) {
            float yRot = 0;//e.getYRot(0);
            float xRot = 0;//e.getXRot(0);
            MC.getConnection().send(exceptionPacket = new ServerboundMovePlayerPacket.PosRot(e.getX(0),e.getY(0),e.getZ(0),yRot,xRot,e.isOnGround()));
            event.setCancelled(true);
        }
        if (packet instanceof ServerboundMovePlayerPacket.Rot e) {
            float yRot = 0;//e.getYRot(0);
            float xRot = 0;//e.getXRot(0);
            MC.getConnection().send(exceptionPacket = new ServerboundMovePlayerPacket.Rot(yRot,xRot,e.isOnGround()));
            event.setCancelled(true);
        }

         */
    }

    @Override
    public void onEnabled() {
        /*
        System.out.println("----------------------------");
        if (MC.getConnection() == null || MC.player == null) return;
        Vector pos = new Vector(85131,90,347878);
        MC.getConnection().send(new ServerboundMovePlayerPacket.Pos(pos.getX(),pos.getY(),pos.getZ(),false));
        MC.player.setPosRaw(pos.getX(),pos.getY(),pos.getZ());
        MC.player.setPos(pos.getX(),pos.getY(),pos.getZ());

         */
    }

    @Override
    public void onDisabled() {
    }

}
