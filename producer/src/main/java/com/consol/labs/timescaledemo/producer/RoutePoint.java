package com.consol.labs.timescaledemo.producer;

public class RoutePoint {

    private final double x;
    private final double y;
    private final double z;

    public RoutePoint(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RoutePoint move(final RoutePoint to, final double by) {
        return addVector(vector(to).normalize().scale(by));
    }

    public double distanceTo(final RoutePoint other) {
        return vector(other).norm();
    }

    public RoutePoint vector(final RoutePoint to) {
        return new RoutePoint(to.x - x, to.y - y, to.z - y);
    }

    public RoutePoint addVector(final RoutePoint vector) {
        return new RoutePoint(x + vector.x, y + vector.y, z + vector.z);
    }

    public RoutePoint normalize() {
        return scale(1 / norm());
    }

    public double norm() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public RoutePoint scale(final double by) {
        return new RoutePoint(x * by, y * by, y * by);
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
}
