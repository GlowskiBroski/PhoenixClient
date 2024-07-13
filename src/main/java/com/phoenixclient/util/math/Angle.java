package com.phoenixclient.util.math;

public class Angle {

    public static final double PI = Math.PI;

    /**
     * Angle (in radians)
     */
    protected double angle;

    public Angle(double angle, boolean isDegrees) {
        if (isDegrees) this.angle = angle * PI / 180;
        else this.angle = angle;
    }

    public Angle(double angle) {
        this(angle, false);
    }


    public Angle getAdded(double angle, boolean isDegrees) {
        return new Angle(angle + (isDegrees ? getDegrees() : getRadians()), isDegrees);
    }

    public Angle getAdded(Angle angle) {
        return getAdded(angle.getRadians(),false);
    }


    public Angle getSubtracted(double angle, boolean isDegrees) {
        return new Angle(angle - (isDegrees ? getDegrees() : getRadians()), isDegrees);
    }

    public Angle getSubtracted(Angle angle) {
        return getSubtracted(angle.getRadians(),false);
    }


    public Angle getMultiplied(double mul) {
        return new Angle(getRadians() * mul);
    }

    /**
     * Returns a simplified angle (0 - 2PI)
     * @return
     */
    public Angle getSimplified() {
        double rad = getRadians();
        while (rad > Math.PI * 2) rad -= Math.PI * 2;
        while (rad < 0) rad += Math.PI * 2;
        return new Angle(rad);
    }

    public double getSin() {
        return Math.sin(getRadians());
    }

    public double getCos() {
        return Math.cos(getRadians());
    }

    public Vector getUnitVector(boolean shouldRound) {
        double x = shouldRound ? MathUtil.roundDouble(Math.cos(getRadians()), 6) : Math.cos(getRadians());
        double y = shouldRound ? MathUtil.roundDouble(Math.sin(getRadians()), 6) : Math.sin(getRadians());
        return new Vector(x, y);
    }

    public Vector getUnitVector() {
        return getUnitVector(false);
    }


    public double getRadians() {
        return angle;
    }

    public double getDegrees() {
        return getRadians() * 180 / PI;
    }


    // ------------------------------ MODIFIABLE METHODS BELOW ------------------------------

    public Angle add(Angle angle) {
        this.angle += angle.getRadians();
        return this;
    }

    @Override
    protected Object clone() {
        return new Angle(getRadians());
    }

    @Override
    public String toString() {
        return "[Angle: " + getRadians() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Angle ang)) return false;
        return ang.getRadians() == getRadians();
    }
}