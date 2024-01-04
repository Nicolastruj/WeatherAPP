package org.ulpgc.dacd.control;

public class Main {
    public static void main(String[] args) {
        EventsReceiver hotelEventsReceiver = new HotelEventsReceiver();
        EventsReceiver weatherEventsReceiver = new WeatherEventsReceiver();
        EventsController hotelEventsController = new EventsController(hotelEventsReceiver, weatherEventsReceiver);
        hotelEventsController.runTask();
    }
}