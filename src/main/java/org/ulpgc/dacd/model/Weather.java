package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private double temperature;
    private double precipitation;
    private double humidity;
    private int clouds;
    private double windSpeed;
    private Instant timeStand;
    private Location locaiton;

    public Weather(double temperature, double precipitation, double humidity, int clouds, double windSpeed, Instant timeStand, Location locaiton){
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.timeStand = timeStand;
        this.locaiton = locaiton;
    }
    public Weather(){}

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Instant getTimeStand() {
        return timeStand;
    }

    public void setTimeStand(Instant timeStand) {
        this.timeStand = timeStand;
    }

    public Location getLocaiton() {
        return locaiton;
    }

    public void setLocaiton(Location locaiton) {
        this.locaiton = locaiton;
    }
}
