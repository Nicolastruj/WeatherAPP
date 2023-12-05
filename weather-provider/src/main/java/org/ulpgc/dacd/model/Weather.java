package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private final double temperature;
    private final double precipitation;
    private final int humidity;
    private final int clouds;
    private final double windSpeed;
    private final Instant predictionTime;
    private final Location locaiton;
    private final Instant ts;
    private final String dataFont;

    public Weather(double temperature, double precipitation, int humidity, int clouds, double windSpeed, Instant predictionTime, Location locaiton, Instant ts, String dataFont){
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.predictionTime = predictionTime;
        this.locaiton = locaiton;
        this.ts = ts;
        this.dataFont = dataFont;
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
        return locaiton;
    }

    public double getPossibilityOfPrecipitation() {
        return precipitation;
    }

    public Instant getTs() {
        return ts;
    }
}
