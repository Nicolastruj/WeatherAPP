package org.ulpgc.dacd.model;

public class Location {
    private final double lat;
    private final double lon;
    private final String island;

    public Location(double lat, double lon, String island) {
        this.lat = lat;
        this.lon = lon;
        this.island = island;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getIsland() {
        return island;
    }

}
