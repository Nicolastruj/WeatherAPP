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

    public void setTemperature(double temperature) {//Esto se puede hacer un patron builder para setter
        this.temperature = temperature;
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

    public void setLocation(Location locaiton) {
        this.locaiton = locaiton;
    }

    public void setPossibilityOfPrecipitation(Double possibilityOfPrecipitation) {
        this.precipitation = possibilityOfPrecipitation;
    }

    public double getPossibilityOfPrecipitation() {
        return precipitation;
    }
}
