package org.ulpgc.dacd.model;

import java.util.List;

public class Hotel {
    private final String id;
    private final String name;
    private final String location;
    private final double price;
    private final double pricePerNight;
    private final double discountPerNightForBookingOnline;
    private final String review;
    private final String reviewNumber;
    private final String distanceToCenter;
    private final String starsNumber;
    private final boolean freeCancelation;
    private final List<String> services;
    public Hotel(String id, String name, String location, double price, double pricePerNight, double discountPerNightForBookingOnline, String review, String reviewNumber, String distanceToCenter, String starsNumber, boolean freeCancelation, List<String> services){

        this.id = id;
        this.name = name;
        this.location = location;
        this.price = price;
        this.pricePerNight = pricePerNight;
        this.discountPerNightForBookingOnline = discountPerNightForBookingOnline;
        this.review = review;
        this.reviewNumber = reviewNumber;
        this.distanceToCenter = distanceToCenter;
        this.starsNumber = starsNumber;
        this.freeCancelation = freeCancelation;
        this.services = services;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public double getPrice() {
        return price;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public double getDiscountPerNightForBookingOnline() {
        return discountPerNightForBookingOnline;
    }

    public String getReview() {
        return review;
    }

    public String getReviewNumber() {
        return reviewNumber;
    }

    public String getDistanceToCenter() {
        return distanceToCenter;
    }

    public String getStarsNumber() {
        return starsNumber;
    }

    public boolean isFreeCancelation() {
        return freeCancelation;
    }

    public List<String> getServices() {
        return services;
    }
}
