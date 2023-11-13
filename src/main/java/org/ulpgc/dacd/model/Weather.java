package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private double temperature;
    private double precipitation;
    private int humidity;
    private int clouds;
    private double windSpeed;
    private Instant timeStand;
    private Location locaiton;

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

    public void setLocation(Location locaiton) {
        this.locaiton = locaiton;
    }

    public double getPossibilityOfPrecipitation() {
        return precipitation;
    }
}
