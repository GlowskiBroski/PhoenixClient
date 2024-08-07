package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;

import static com.phoenixclient.PhoenixClient.MC;

public class FastFirework extends Module {

    public FastFirework() {
        super("FastFirework", "Makes fireworks faster", Category.MOTION, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }
    StopWatch extensionWatch = new StopWatch();

    int step = 0;

    public void onPlayerUpdate(Event event) {
        if (MC.player.isFallFlying()) {
            boolean fireworkActive = false;
            for (Entity entity : MC.level.entitiesForRendering()) {
                if (entity instanceof FireworkRocketEntity) {
                    fireworkActive = true;
                    break;
                }
            }

            if (fireworkActive) {
                Angle yaw = new Angle(MC.player.getRotationVector().y, true);
                Angle pitch = new Angle(MC.player.getRotationVector().x, true);

                MC.player.setDeltaMovement(new Vector(yaw, pitch, .12 * step).getVec3());
                step = Math.clamp(step + 3, 0, 42);
            } else {
                step = 14;
            }

        }
    }

}
