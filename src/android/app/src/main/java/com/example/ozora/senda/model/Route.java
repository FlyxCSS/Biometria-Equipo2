package com.example.ozora.senda.model;

public final class Route {
    public final long id, start;
    public final Long end;
    public final double meters, mean;
    public final String sensor;
    public final int count, minimum, maximum;

    public Route(long id, long start, Long end, double meters, String sensor, int count,
                 double mean, int minimum, int maximum) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.meters = meters;
        this.sensor = sensor;
        this.count = count;
        this.mean = mean;
        this.minimum = minimum;
        this.maximum = maximum;
    }
}
