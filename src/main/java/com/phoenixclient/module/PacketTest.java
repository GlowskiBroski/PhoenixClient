package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.level.GameType;

import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

//Some kid was posting this in chat, its probably a troll but i decided to write it down: 598,120 68 1,471,050

//Maybe see if you can bypass the firework star crafting limit?

public class PacketTest extends Module {

    public PacketTest() {
        super("PacketTest", "Development Packet Mod - DO NOT ENABLE THIS", Category.SERVER, false, -1);
        addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onUpdate);
    }


    Packet<?> exceptionPacket = null;

    Entity backup = null;

    int ticks = 0;

    private final Packet<?> packet = null;

    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        //if (packet instanceof ServerboundMovePlayerPacket) event.setCancelled(true);
        //if (event.getType().equals(PacketEvent.Type.SEND)) System.out.println(packet);

        //if (packet instanceof ServerboundUseItemPacket) System.out.println(packet);
        //if (packet instanceof ServerboundPlayerActionPacket) this.exceptionPacket = packet;

        //if (event.getType().equals(PacketEvent.Type.SEND)) System.out.println(packet);

        // ----------------------------------------------
        if (true) return;
        if (packet instanceof ServerboundContainerClickPacket) {
            //event.setCancelled(true);
            MC.options.keyShift.setDown(true);
            ticks = 0;
        }

        if (ticks >=1) { //1 keeps in inventory. 2 keeps in donkey
            MC.getConnection().handleDisconnect(new ClientboundDisconnectPacket(Component.translatable("Disconnect"))); //I tried lol
            disable();
            ticks = -1;
        }

    }


    public void onUpdate(Event event) {

        if (Key.KEY_I.isKeyDown()) {
            if (MC.player.getMainHandItem().getItem() instanceof CrossbowItem i) {
                MC.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM,MC.player.getBlockPosBelowThatAffectsMyMovement(), Direction.DOWN,0));
                i.releaseUsing(MC.player.getMainHandItem(),MC.level,MC.player,0);
            }
        }

        // -------------------------

        if (true) return;
        if (ticks >= 0) {
            ticks ++;
        }

        if (backup != null) {
            backup.setPos(MC.player.getPosition(0));
            if (MC.options.keySprint.isDown()) MC.getConnection().send(new ServerboundMoveVehiclePacket(backup));
        }


    }

    @Override
    public void onEnabled() {
        if (updateDisableOnEnabled()) return;
        backup = MC.player.getVehicle();
        if (backup != null) {
            MC.player.removeVehicle();
            backup.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void onDisabled() {
        if (MC.player == null) return;
        if (backup != null) {
            if (!MC.options.keyShift.isDown()) {
                //MC.level.addEntity(backup);
                MC.player.startRiding(backup, true);
            }
        }
        backup = null;
    }


}
