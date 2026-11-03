package com.example.ozora.senda.model;

public final class RoutePoint {
    public final double latitude, longitude;
    public final long segment;

    public RoutePoint(double latitude, double longitude, long segment) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.segment = segment;
    }
}
