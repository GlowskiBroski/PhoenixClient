package com.phoenixclient.util.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class Vector {

    private static final Vector NULL = new Vector(0, 0, 0);
    private static final Vector I = new Vector(1, 0, 0);
    private static final Vector J = new Vector(0, 1, 0);
    private static final Vector K = new Vector(0, 0, 1);

    public static Vector NULL() {
        return NULL.clone();
    }

    public static Vector I() {
        return I.clone();
    }
    public static Vector J() {
        return J.clone();
    }
    public static Vector K() {
        return K.clone();
    }


    protected double x, y, z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(double x, double y) {
        this(x, y, 0);
    }

    public Vector(Angle yaw, Angle pitch, double magnitude) {
        double yawRadians = yaw.getRadians();
        double pitchRadians = pitch.getRadians();

        this.x = -Math.sin(yawRadians) * Math.cos(pitchRadians);
        this.y = -Math.sin(pitchRadians);
        this.z = Math.cos(yawRadians) * Math.cos(pitchRadians);
        multiply(magnitude);
    }

    public Vector(Position pos) {
        this.x = pos.x();
        this.y = pos.y();
        this.z = pos.z();
    }

    public Vector(Vec3i vec) {
        this.x = vec.getX();
        this.y = vec.getY();
        this.z = vec.getZ();
    }


    public Vector x(double x) {
        return new Vector(x,getY(),getZ());
    }

    public Vector y(double y) {
        return new Vector(getX(),y,getZ());
    }

    public Vector z(double z) {
        return new Vector(getX(),getY(),z);
    }


    public Vector getAdded(Vector vec) {
        return new Vector(getX() + vec.getX(), getY() + vec.getY(), getZ() + vec.getZ());
    }

    public Vector getAdded(double x, double y) {
        return getAdded(x, y, 0);
    }

    public Vector getAdded(double x, double y, double z) {
        return getAdded(new Vector(x, y, z));
    }


    public Vector getSubtracted(Vector vec) {
        return new Vector(getX() - vec.getX(), getY() - vec.getY(), getZ() - vec.getZ());
    }

    public Vector getSubtracted(double x, double y) {
        return getSubtracted(x, y, 0);
    }

    public Vector getSubtracted(double x, double y, double z) {
        return getSubtracted(new Vector(x, y, z));
    }


    public Vector getScaled(Vector vec) {
        return new Vector(getX() * vec.getX(), getY() * vec.getY(), getZ() * vec.getZ());
    }

    public Vector getScaled(double x, double y) {
        return new Vector(getX() * x, getY() * y, getZ() * 1);
    }

    public Vector getScaled(double x, double y, double z) {
        return new Vector(getX() * x, getY() * y, getZ() * z);
    }


    public Vector getMultiplied(double multiplier) {
        return new Vector(getX() * multiplier, getY() * multiplier, getZ() * multiplier);
    }

    public Vector getCross(Vector vec) {
        return new Vector(
                getY() * vec.getZ() - getZ() * vec.getY(),
                -getX() * vec.getZ() + getZ() * vec.getX(),
                getX() * vec.getY() - getY() * vec.getX());
    }

    public double getDot(Vector vec) {
        return getX() * vec.getX() + getY() * vec.getY() + getZ() * vec.getZ();
    }


    public double getMagnitude() {
        return Math.sqrt(getX() * getX() + getY() * getY() + getZ() * getZ());
    }

    public Vector getUnitVector() {
        return getMultiplied(1 / getMagnitude());
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }


    public Angle getYaw() {
        return new Angle(Math.atan2(getZ(), getX()) - Math.PI / 2);
    }

    public Angle getPitch() {
        return new Angle(Math.acos(getY() / getMagnitude()) - Math.PI / 2);
    }

    // ------------------------------ MODIFIABLE METHODS BELOW ------------------------------
    // Try and avoid using these unless you clone a vector as to not destroy static vectors

    public Vector setX(double x) {
        this.x = x;
        return this;
    }

    public Vector setY(double y) {
        this.y = y;
        return this;
    }

    public Vector setZ(double z) {
        this.z = z;
        return this;
    }

    public Vector add(Vector vec) {
        this.x += vec.getX();
        this.y += vec.getY();
        this.z += vec.getZ();
        return this;
    }

    public Vector subtract(Vector vec) {
        this.x -= vec.getX();
        this.y -= vec.getY();
        this.z -= vec.getZ();
        return this;
    }

    public Vector scale(Vector vec) {
        this.x *= vec.getX();
        this.y *= vec.getY();
        this.z *= vec.getZ();
        return this;
    }

    public Vector multiply(double multiplier) {
        this.x *= multiplier;
        this.y *= multiplier;
        this.z *= multiplier;
        return this;
    }

    // ------------------------------ MINECRAFT ADAPTER METHODS BELOW ------------------------------

    public Vec3 getVec3() {
        return new Vec3(getX(),getY(),getZ());
    }

    public Vec3i getVec3i() {
        return new Vec3i((int)getX(),(int)getY(),(int)getZ());
    }

    public BlockPos getBlockPos() {
        return BlockPos.containing(getVec3());
    }


    @Override
    public Vector clone() {
        return new Vector(x,y,z);
    }

    @Override
    public String toString() {
        return "<" + getX() + "|" + getY() + "|" + getZ() + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector newVec)) return false;
        try {
            return getX() == newVec.getX() && getY() == newVec.getY() && getZ() == newVec.getZ();
        } catch (Exception e) {
            return false;
        }
    }
}