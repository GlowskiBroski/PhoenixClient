package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.actions.OnChange;
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

    private final SettingGUI<Integer> lockOnTickCount = new SettingGUI<>(
            this,
            "Lock On Ticks",
            "The amount of ticks waited before attacking when your target changes",
            4)
            .setSliderData(0,20,1);

    private final OnChange<Entity> onChangeTarget = new OnChange<>();

    private int lockOnTicks = 0;

    public KillAura() {
        super("KillAura", "Automatically attacks entities around you", Category.COMBAT,false, -1);
        addSettings(range,lockOnTickCount);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        Vector playerPos = new Vector(MC.player.getPosition(0));
        Vector eyePos = new Vector(MC.player.getEyePosition());
        Vector playerLook = new Vector(MC.player.getLookAngle());

        FreeCam freeCam = (FreeCam) PhoenixClient.getModule("FreeCam");
        if (freeCam.isEnabled()) {
            playerPos = new Vector(freeCam.dummyPlayer.getPosition(0));
            eyePos = new Vector(freeCam.dummyPlayer.getEyePosition());
            playerLook = new Vector(freeCam.dummyPlayer.getLookAngle());
        }

        Entity target = getTarget(playerPos, playerLook);

        onChangeTarget.run(target, () -> lockOnTicks = 0);

        if (target == null) {
            PhoenixClient.getRotationManager().stopSpoofing();
            return;
        }

        Vector distVec = getCenterAABB(target).getSubtracted(eyePos);

        float yaw = (float) distVec.getYaw().getDegrees();
        float pitch = (float) distVec.getPitch().getDegrees();

        PhoenixClient.getRotationManager().spoof(yaw, pitch);

        if (MC.player.getAttackStrengthScale(0) >= 1 && lockOnTicks >= lockOnTickCount.get()) {
            MC.gameMode.attack(MC.player, target);
            MC.player.swing(InteractionHand.MAIN_HAND);
        }
        lockOnTicks++;
    }

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
        FreeCam freeCam = (FreeCam) PhoenixClient.getModule("FreeCam");
        if (entity.equals(freeCam.dummyPlayer)) return false;
        if (!(entity instanceof LivingEntity)) return false;
        return Math.sqrt(entity.distanceToSqr(playerPos.getVec3())) < range.get();
    }

    public static Vector getCenterAABB(Entity entity) {
        AABB obb = entity.getBoundingBox();
        return new Vector((obb.maxX + obb.minX) / 2, (obb.maxY + obb.minY) / 2, (obb.maxZ + obb.minZ) / 2);
    }

    @Override
    public String getModTag() {
        return String.format("%.1f", range.get());
    }

    @Override
    public void onDisabled() {
        PhoenixClient.getRotationManager().stopSpoofing();
    }
}
