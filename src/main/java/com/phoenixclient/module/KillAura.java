package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import static com.phoenixclient.PhoenixClient.MC;

public class KillAura extends Module {

    private final SettingGUI<Double> range = new SettingGUI<>(
            this,
            "Range",
            "Attack Range of Kill Aura",
            4.5d)
            .setSliderData(.5,5,.5);

    public KillAura() {
        super("KillAura", "Automatically attacks entities around you", Category.COMBAT,false, -1);
        addSettings(range);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
        addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
    }


    @Override
    public String getModTag() {
        return String.format("%.1f", range.get());
    }

    public void onPlayerUpdate(Event event) {
        Vector playerPos = new Vector(MC.player.getPosition(0));
        Vector eyePos = new Vector(MC.player.getEyePosition());
        Vector playerLook = new Vector(MC.player.getLookAngle());

        Entity target = getTarget(playerPos,playerLook);

        if (target != null) {

            Vector distVec = getCenterAABB(target).getSubtracted(eyePos);

            float yaw = (float)distVec.getYaw().getDegrees();
            float pitch = (float)distVec.getPitch().getDegrees();

            //TODO: Add rotation spoofing here
            //MC.player.setYRot(yaw);
            //MC.player.setXRot(pitch);

            if (MC.player.getAttackStrengthScale(0) >= 1) {
                MC.getConnection().send(new ServerboundMovePlayerPacket.Rot(yaw,pitch,MC.player.onGround()));
                MC.gameMode.attack(MC.player, target);
                MC.player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    Vector targetVector = Vector.NULL();

    //Keep playerPosition a parameter so you can set it to the freecam dummy's location
    private Entity getTarget(Vector playerPosition, Vector playerLook) {
        Entity target = null;
        double shortestDistance = -1;

        //Loop through all valid entities and find the closest one to the cursor
        for (Entity entity : MC.level.entitiesForRendering()) {
            if (!isTarget(entity, playerPosition)) continue;

            double cursorDistance = getCenterAABB(entity).getSubtracted(playerPosition).getUnitVector().getSubtracted(playerLook.getUnitVector()).getMagnitude();

            //If the next target is closer, set that to be the new target
            if (target == null || cursorDistance < shortestDistance) {
                shortestDistance = cursorDistance;
                target = entity;
            }
        }
        return target;
    }

    private boolean isTarget(Entity entity, Vector playerPos) {
        if (entity == null) return false;
        if (entity.equals(MC.player)) return false;
        if (!(entity instanceof LivingEntity)) return false;
        return Math.sqrt(entity.distanceToSqr(playerPos.getVec3())) < range.get();
    }

    public static Vector getCenterAABB(Entity entity) {
        AABB obb = entity.getBoundingBox();
        return new Vector((obb.maxX + obb.minX) / 2, (obb.maxY + obb.minY) / 2, (obb.maxZ + obb.minZ) / 2);
    }

    private Packet<?> exceptionPacket = null;

    public void onPacket(PacketEvent event) {
        /*
        PacketEvent event = Event.EVENT_PACKET;
        Packet<?> packet = event.getPacket();

        if (packet.equals(exceptionPacket)) return;

        float yRot = (float)targetVector.getYaw().getDegrees();
        float xRot = (float)targetVector.getPitch().getDegrees();

        if (packet instanceof ServerboundMovePlayerPacket.PosRot e) {
            MC.getConnection().send(exceptionPacket = new ServerboundMovePlayerPacket.PosRot(e.getX(0),e.getY(0),e.getZ(0),yRot,xRot,e.isOnGround()));
            event.setCancelled(true);
        }
        if (packet instanceof ServerboundMovePlayerPacket.Rot e) {
            MC.getConnection().send(exceptionPacket = new ServerboundMovePlayerPacket.Rot(yRot,xRot,e.isOnGround()));
            event.setCancelled(true);
        }
        */
    }

}
