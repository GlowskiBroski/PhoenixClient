package com.phoenixclient.util;

import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static com.phoenixclient.PhoenixClient.MC;

public class MotionUtil {

    public static boolean isInputActive(boolean includeY) {
        boolean yMotion = includeY && (MC.options.keyJump.isDown() || MC.options.keyShift.isDown());
        return MC.options.keyUp.isDown() || MC.options.keyDown.isDown() || MC.options.keyLeft.isDown() || MC.options.keyRight.isDown() || yMotion;
    }

    public static void addEntityMotionInLookDirection(Entity entity, double speed) {
        Angle yaw = new Angle(entity.getRotationVector().y, true);
        Angle pitch = new Angle(entity.getRotationVector().x, true);

        if (MC.options.keyUp.isDown())
            entity.addDeltaMovement(new Vector(yaw, pitch, speed).getVec3());

        if (MC.options.keyDown.isDown())
            entity.addDeltaMovement(new Vector(yaw.getAdded(180, true), pitch, speed).getVec3());

        if (MC.options.keyLeft.isDown())
            entity.addDeltaMovement(new Vector(yaw.getAdded(-90, true), pitch, speed).getVec3());

        if (MC.options.keyRight.isDown())
            entity.addDeltaMovement(new Vector(yaw.getAdded(90, true), pitch, speed).getVec3());

        if (MC.options.keyJump.isDown())
            entity.addDeltaMovement(new Vec3(0, speed, 0));

        if (MC.options.keyShift.isDown())
            entity.addDeltaMovement(new Vec3(0, -speed, 0));
    }

    public static void setEntityMotionInLookDirection(Entity entity, double speed) {
        Angle yaw = new Angle(entity.getRotationVector().y, true);
        Angle pitch = new Angle(entity.getRotationVector().x, true);

        if (MC.options.keyUp.isDown())
            entity.setDeltaMovement(new Vector(yaw, pitch, speed).getVec3());

        if (MC.options.keyDown.isDown())
            entity.setDeltaMovement(new Vector(yaw.getAdded(180, true), pitch, speed).getVec3());

        if (MC.options.keyLeft.isDown())
            entity.setDeltaMovement(new Vector(yaw.getAdded(-90, true), pitch, speed).getVec3());

        if (MC.options.keyRight.isDown())
            entity.setDeltaMovement(new Vector(yaw.getAdded(90, true), pitch, speed).getVec3());

        if (MC.options.keyJump.isDown())
            entity.setDeltaMovement(new Vec3(0, speed, 0));

        if (MC.options.keyShift.isDown())
            entity.setDeltaMovement(new Vec3(0, -speed, 0));

        if (!isInputActive(true)) entity.setDeltaMovement(0, 0, 0);

        //Mount Override
        if (!entity.equals(MC.player) && MC.options.keySprint.isDown())
            entity.setDeltaMovement(new Vec3(0, -speed, 0));
    }

    public static void moveEntityStrafe(double speed, Entity entity) {
        //Entity is able to be the mount OR the player
        if (entity == null) return;
        double forward = MC.player.input.forwardImpulse;
        double strafe = MC.player.input.leftImpulse;
        Angle yaw = new Angle(MC.player.getYRot() + 90,true);

        if (forward == 0 && strafe == 0) {
            entity.setDeltaMovement(0, entity.getDeltaMovement().y(), 0);
            return;
        }

        if (forward != 0) {
            int mul = strafe > 0 ? 1 : -1;
            if (strafe != 0) yaw.add(new Angle((forward > 0 ? mul * -45 : mul * 45),true));

            forward = forward > 0 ? 1 : -1;
            strafe = 0;
        }

        double motionX = speed * (forward * yaw.getCos() + strafe * yaw.getSin());
        double motionZ = speed * (forward * yaw.getSin() - strafe * yaw.getCos());
        entity.setDeltaMovement(motionX, entity.getDeltaMovement().y(), motionZ);
    }

}
