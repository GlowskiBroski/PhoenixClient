package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.util.input.Key;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

import static com.phoenixclient.PhoenixClient.MC;

//Some kid was posting this in chat, its probably a troll but i decided to write it down: 598,120 68 1,471,050

//Maybe see if you can bypass the firework star crafting limit?

public class TestModule extends Module {

    public TestModule() {
        super("TestModule", "Development Mod - DO NOT ENABLE THIS", Category.SERVER, false, -1);
        //addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onUpdate);


    }


    public void onUpdate(Event event) {
        if (Key.KEY_UP.isKeyDown()) {
            MC.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS,MC.player.blockPosition(),Direction.DOWN));
        }
        if (Key.KEY_DOWN.isKeyDown()) {
            MC.getConnection().handleDisconnect(new ClientboundDisconnectPacket(Component.translatable("TestModule Disconnect")));
        }

        if (Key.KEY_ENTER.isKeyDown()) {
            if (MC.player.containerMenu instanceof BrewingStandMenu m && m.getSlot(2).getItem().getItem().equals(Items.GLASS_BOTTLE)) {
                MC.gameMode.handleInventoryMouseClick(m.containerId, 2, 0, ClickType.QUICK_MOVE, MC.player);
                MC.gameMode.handleInventoryMouseClick(m.containerId, 5, 0, ClickType.QUICK_MOVE, MC.player);
                //MC.gameMode.handleInventoryMouseClick(m.containerId);
            }
        }

        if (Key.KEY_PERIOD.isKeyDown()) {
            if (MC.player.containerMenu instanceof BrewingStandMenu m) {
                MC.gameMode.handleInventoryMouseClick(m.containerId, 5, 0, ClickType.QUICK_MOVE, MC.player);
            }
        }

    }



    /*
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


     */

}
