package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private final double temperature;
    private final double precipitation;
    private final int humidity;
    private final int clouds;
    private final double windSpeed;
    private final Instant predictionTime;
    private final Location location;
    private final Instant ts;
    private final String ss;

    public Weather(double temperature, double precipitation, int humidity, int clouds, double windSpeed, Instant predictionTime, Location location, Instant ts, String ss){
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.predictionTime = predictionTime;
        this.location = location;
        this.ts = ts;
        this.ss = ss;
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

    public Instant getPredictionTime() {
        return predictionTime;
    }

    public Location getLocation() {
        return location;
    }

    public double getPossibilityOfPrecipitation() {
        return precipitation;
    }

    public Instant getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }
}
