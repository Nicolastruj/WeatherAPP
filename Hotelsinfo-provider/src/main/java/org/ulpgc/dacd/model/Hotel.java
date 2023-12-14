package org.ulpgc.dacd.model;

import java.util.List;

public class Hotel {
    private String id;
    private String name;
    private String town;
    private Location location;
    private String price;
    private String review;
    private String reviewNumber;
    private String starsNumber;
    private List<String> services;
    private List<String> interestingPlaces;
    public Hotel(String id, String name, String town, Location location, String price, String review, String reviewNumber, String starsNumber, List<String> services, List<String> interestingPlaces){

        this.id = id;
        this.name = name;
        this.town = town;
        this.location = location;
        this.price = price;
        this.review = review;
        this.reviewNumber = reviewNumber;
        this.starsNumber = starsNumber;
        this.services = services;
        this.interestingPlaces = interestingPlaces;
    }
}
