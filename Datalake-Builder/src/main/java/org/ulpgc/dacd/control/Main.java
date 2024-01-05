package org.ulpgc.dacd.control;

public class Main {
    public static void main(String[] args) {
        EventsReceiver weatherEventsReceiver = new WeatherEventsReceiver();
        EventsReceiver hotelReceiver = new HotelEventsReceiver();
        EventsController weatherEventsController = new EventsController(weatherEventsReceiver, hotelReceiver);
        weatherEventsController.runTask();
    }
}