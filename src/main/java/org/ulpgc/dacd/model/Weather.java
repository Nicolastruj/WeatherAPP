package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private final double temperature;
    private final double precipitation;
    private final int humidity;
    private final int clouds;
    private final double windSpeed;
    private final Instant timeStand;
    private final Location locaiton;

    public Weather(double temperature, double precipitation, int humidity, int clouds, double windSpeed, Instant timeStand, Location locaiton){
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.timeStand = timeStand;
        this.locaiton = locaiton;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getCloudisness() {
        return clouds;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public Instant getTimeStand() {
        return timeStand;
    }

    public Location getLocation() {
        return locaiton;
    }

    public double getPossibilityOfPrecipitation() {
        return precipitation;
    }
}
